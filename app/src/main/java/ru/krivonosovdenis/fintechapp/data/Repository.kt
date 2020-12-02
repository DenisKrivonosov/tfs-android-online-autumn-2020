package ru.krivonosovdenis.fintechapp.data

import android.util.Log
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import org.joda.time.DateTime
import ru.krivonosovdenis.fintechapp.data.db.ApplicationDatabase
import ru.krivonosovdenis.fintechapp.data.db.DBConstants.POST_SOURCE_FEED
import ru.krivonosovdenis.fintechapp.data.db.DBConstants.POST_SOURCE_PROFILE
import ru.krivonosovdenis.fintechapp.data.network.ApiInterface
import ru.krivonosovdenis.fintechapp.data.network.VkApiClient
import ru.krivonosovdenis.fintechapp.dataclasses.*
import ru.krivonosovdenis.fintechapp.dataclasses.getgroupsdataclasses.GroupsApiResponse
import ru.krivonosovdenis.fintechapp.dataclasses.getpostcommentsdataclasses.PostCommentsResponse
import ru.krivonosovdenis.fintechapp.dataclasses.newsfeeddataclasses.NewsfeedApiResponse
import ru.krivonosovdenis.fintechapp.dataclasses.postdeletedataclasses.PostDeleteResponse
import ru.krivonosovdenis.fintechapp.dataclasses.postdislikedataclasses.PostDislikeResponse
import ru.krivonosovdenis.fintechapp.dataclasses.postlikedataclasses.PostLikeResponse
import ru.krivonosovdenis.fintechapp.dataclasses.sendpostdataclasses.SendPostResponse
import ru.krivonosovdenis.fintechapp.dataclasses.userprofiledataclasses.userfullinfodataclasses.UserFullInfoResponse
import ru.krivonosovdenis.fintechapp.dataclasses.userprofiledataclasses.userwallpostsdataclasses.UserWallPostsResponse

class Repository(
    private val authNetworkService: ApiInterface,
    private val dbConnection: ApplicationDatabase
) {

    //DB REQUESTS
    fun getAllPostsFromDb(): Flowable<List<PostFullData>> {
        return dbConnection.feedPostsDao().subscribeOnFeedPosts()
    }

    fun getLikedPostsFromDb(): Flowable<List<PostFullData>> {
        return dbConnection.feedPostsDao().subscribeOnLikedPosts()
    }

    fun getLikedPostsCount(): Flowable<Int> {
        return dbConnection.feedPostsDao().subscribeOnFeedLikedCount()
    }

    fun getPostFromDb(postId: Int, sourceId: Int): Flowable<PostFullData> {
        return dbConnection.feedPostsDao().getPostById(postId, sourceId)
    }

    fun getPostCommentsFromDb(postId: Int, ownerId: Int): Flowable<List<CommentData>> {
        return dbConnection.feedPostsDao().subscribeOnPostComments(postId, ownerId)
    }

    fun deletePostFromDb(post: PostFullData): Completable {
        return dbConnection.feedPostsDao().deletePostById(post.postId, post.sourceId)
    }

    fun setPostIsLikedInDb(post: PostFullData): Completable {
        return dbConnection.feedPostsDao()
            .setPostIsLikedById(
                post.postId,
                post.sourceId,
                post.likesCount,
                if (post.isLiked) 1 else 0
            )
    }

    fun deleteAllPostsAndInsertIntoDb(posts: ArrayList<PostFullData>) {
        return dbConnection.feedPostsDao().deleteAllFeedPostsAndInsert(posts)
    }

    fun getUserInfoFlowableFromDb(): Flowable<UserProfileMainInfo> {
        return dbConnection.feedPostsDao().subscribeOnUserInfo()
    }

    fun getUserInfoFromDb(vkId: Int): Single<UserProfileMainInfo> {
        return dbConnection.feedPostsDao().getUserInfoById(vkId)
    }

    fun getUserOwnPostsFromDb(): Flowable<List<PostFullData>> {
        return dbConnection.feedPostsDao().subscribeOnUserOwnPosts()
    }

    fun deleteAllUserProfileInfoAndPostsAndInsertIntoDb(data: ArrayList<InfoRepresentationClass>) {
        val userProfile = data.filterIsInstance<UserProfileMainInfo>().first()
        val userPosts = data.filterIsInstance<PostFullData>()
        return dbConnection.feedPostsDao().deleteAllUserProfileInfoAndPostsAndInsert(
            userProfile,
            userPosts as ArrayList<PostFullData>
        )
    }

    fun deleteAllPostCommentsAndInsertIntoDb(postId:Int, ownerId:Int, comments:ArrayList<CommentData>){
        return dbConnection.feedPostsDao().deleteAllPostCommentsAndInsertIntoDb(
            postId,
            ownerId,
            comments
        )
    }


    //NETWORK REQUESTS
    //posts
    fun getFeedPostsFromApi(): Single<ArrayList<PostFullData>> {
        return authNetworkService.getNewsFeed()
            .flatMap { addGroupsInfoToPosts(it) }
    }


    fun likePostApi(post: PostFullData): Single<PostLikeResponse> {
        return authNetworkService
            .addLike(POST_TYPE, post.sourceId, post.postId)
    }

    fun dislikePostApi(post: PostFullData): Single<PostDislikeResponse> {
        return authNetworkService
            .removeLike(POST_TYPE, post.sourceId, post.postId)
    }

    fun deletePostFromFeedApi(post: PostFullData): Single<PostDeleteResponse> {
        return authNetworkService
            .deletePostFromFeed(DELETE_POST_TYPE, post.sourceId, post.postId)
    }

    private fun getDistinctGroups(apiResponse: NewsfeedApiResponse): String {
        return apiResponse.response.items.map { it.sourceId }.filter { it < 0 }.map { -it }
            .joinToString(separator = ",")
    }

    private fun addGroupsInfoToPosts(newsfeedApiResponse: NewsfeedApiResponse): Single<ArrayList<PostFullData>> {
        val groups = getDistinctGroups(newsfeedApiResponse)
        return VkApiClient.getAuthRetrofitClient().getGroups(groups)
            .flatMap {
                Single.just(combinePostsAndGroups(newsfeedApiResponse, it))
            }
    }

    //У нас получается крайне урезанная версия фида. Во вконтакте можно разместить большое количество
    //разнообразных типов постов. Например, может быть пост только с видео. Или пост, содержащий
    //только репост. Поэтому дальше большое количество постов мы будем исключать из выборки. Оставляем
    //только те посты, которые мы можем нормально отобразить
    private fun combinePostsAndGroups(
        postsData: NewsfeedApiResponse,
        groupsData: GroupsApiResponse
    ): ArrayList<PostFullData> {
        val currentRenderData = ArrayList<PostFullData>()
        postsData.response.items.forEach iterator@{ postData ->
            //Во первых - мы отсекаем все сущности, тип которых не является "post"
            if (postData.type != "post") {
                return@iterator
            }
            //Во вторых - мы отсекаем сущности, у которых нет текста и картинок в attachments
            if (postData.text == null && postData.attachments?.find { it.photo != null } == null) {
                return@iterator
            }
            //В третьих - по факту фид и информацию об авторах постов из фида можно получать
            //1 запросом. Но в целях изучения rxjava2 во время выполнения домашки было решено забрать
            // информацию об авторах постов отдельным запросом. Поэтому мы отсекаем все посты, авторы
            //которых не группы. По хорошему - надо переделать. Если такое поведение осталось
            // в финальной версии работы - значит не хватило времени на переписывание этой части =/
            if (postData.sourceId > 0) {
                return@iterator
            }
            val group = groupsData.response.first { it.id == -postData.sourceId }
            currentRenderData.add(
                PostFullData(
                    postData.postId,
                    postData.sourceId,
                    group.photo200,
                    group.name,
                    DateTime(postData.date * 1000L),
                    postData.text ?: "",
                    postData.attachments?.get(0)?.photo?.sizes?.last()?.url ?: "",
                    postData.likes?.count ?: 0,
                    postData.likes?.userLikes == 1,
                    postData.comments?.count ?: 0,
                    postData.reposts?.count ?: 0,
                    postData.views?.count ?: 0,
                    POST_SOURCE_FEED
                )
            )
        }
        currentRenderData.sortByDescending { it.date }
        return currentRenderData
    }

    //post_details
    fun getPostCommentsFromApi(postId: Int, ownerId: Int): Single<List<CommentData>> {
        return authNetworkService.getPostComments(POST_TYPE, postId, ownerId, EXTENDED_TRUE)
            .flatMap { Single.just(transformPostCommentsResponse(it)) }
    }

    //user_profile
    //Было решено использовать последовательные запросы, а не параллельные, так как для корректного
    //рендеринга постов нам все равно нужен ответ от апишки users.get. Можно, конечно, было сделать
    //и параллельные запросы, но тогда сильно усложнилась бы логика работы самого приложения. В рамках
    //текущей задачи был выбран более простой вариант реализации
    fun getUserProfileAndWallFromApi(): Single<ArrayList<InfoRepresentationClass>> {
        return authNetworkService.getUserProfileInfo(USER_PROFILE_INFO_FIELDS)
            .flatMap { addUserPostsToProfileInfo(it) }
    }


    private fun addUserPostsToProfileInfo(userProfileInfoApiResponse: UserFullInfoResponse): Single<ArrayList<InfoRepresentationClass>> {
        return VkApiClient.getAuthRetrofitClient()
            .getUserOwnPosts(USER_WALL_OWN_POSTS_COUNT, USER_WALL_POSTS_OWNER)
            .flatMap {
                Single.just(combineUserInfoAndWallPosts(userProfileInfoApiResponse, it))
            }
    }

    private fun combineUserInfoAndWallPosts(
        userProfileInfoApiResponse: UserFullInfoResponse,
        userWallPostsResponse: UserWallPostsResponse

    ): ArrayList<InfoRepresentationClass> {
        val finalData = ArrayList<InfoRepresentationClass>()
        val userProfileApiResponse = userProfileInfoApiResponse.response.first()
        val userProfileMainInfo = UserProfileMainInfo(
            userProfileApiResponse.id,
            userProfileApiResponse.firstName,
            userProfileApiResponse.lastName,
            userProfileApiResponse.status,
            userProfileApiResponse.bdate,
            userProfileApiResponse.city.title,
            userProfileApiResponse.country.title,
            userProfileApiResponse.photo,
            userProfileApiResponse.lastSeen.time,
            userProfileApiResponse.followersCount,
            userProfileApiResponse.universityName,
            userProfileApiResponse.facultyName,
        )
        finalData.add(userProfileMainInfo)

        userWallPostsResponse.response.items.forEach { postData ->
            finalData.add(
                PostFullData(
                    postData.id,
                    postData.fromId,
                    userProfileApiResponse.photo,
                    "${userProfileApiResponse.firstName} ${userProfileApiResponse.lastName}",
                    DateTime(postData.date * 1000L),
                    postData.text ?: "",
                    postData.attachments?.get(0)?.photo?.sizes?.last()?.url ?: "",
                    postData.likes.count,
                    postData.likes.canLike != 1,
                    postData.comments.count,
                    postData.reposts.count,
                    postData.views?.count ?: 0,
                    POST_SOURCE_PROFILE
                )
            )
        }
        return finalData
    }

    private fun transformPostCommentsResponse(apiResponse: PostCommentsResponse): List<CommentData> {
        Log.e("insiddderesp","herer");
        Log.e("responsefix",apiResponse.response.count.toString());
        val commentsRawData = apiResponse.response.items
        val profilesRawData = apiResponse.response.profiles
        val finalData = ArrayList<CommentData>()
        commentsRawData.forEach { commentRawData ->
            finalData.add(
                CommentData(
                    commentRawData.id,
                    commentRawData.fromId,
                    commentRawData.postId,
                    commentRawData.ownerId,
                    profilesRawData.find { it.id == commentRawData.fromId }?.photo100 ?: "",
                    profilesRawData.find { it.id == commentRawData.fromId }?.firstName ?: "",
                    DateTime(commentRawData.date * 1000L),
                    commentRawData.text?:"",
                    commentRawData.attachments?.get(0)?.photo?.sizes?.last()?.url ?: "",
                    commentRawData.likes?.count ?:0,
                    commentRawData.likes?.canLike != 1,
                )
            )
        }
        Log.e("finalDataCount", finalData.count().toString());
        return finalData
    }

    fun sendPostToOwnWall(message: String, attachments: String): Single<SendPostResponse> {
        return authNetworkService
            .sendPostToOwnWall(message, attachments)
    }

    companion object {
        const val EXTENDED_TRUE = 1
        const val POST_TYPE = "post"
        const val DELETE_POST_TYPE = "wall"
        const val USER_PROFILE_INFO_FIELDS =
            "status,first_name,last_name,photo,about,bdate,city,country,career,education,followers_count,last_seen"
        const val USER_WALL_OWN_POSTS_COUNT = 100
        const val USER_WALL_POSTS_OWNER = "owner"
    }
}
