package ru.krivonosovdenis.fintechapp.data

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
import ru.krivonosovdenis.fintechapp.dataclasses.groupsdataclasses.GroupsApiResponse
import ru.krivonosovdenis.fintechapp.dataclasses.newsfeeddataclasses.NewsfeedApiResponse
import ru.krivonosovdenis.fintechapp.dataclasses.userprofiledataclasses.userfullinfodataclasses.UserFullInfoResponse
import ru.krivonosovdenis.fintechapp.dataclasses.userprofiledataclasses.userwallpostsdataclasses.UserWallPostsResponse

class Repository(
    private val authNetworkService: ApiInterface,
    private val dbConnection: ApplicationDatabase
) {

    //DB REQUESTS
    //posts
    fun getAllPostsFromDb(): Flowable<List<PostFullData>> {
        return dbConnection.feedPostsDao().subscribeOnAllFeedPosts()
    }

    fun getLikedPostsFromDb(): Flowable<List<PostFullData>> {
        return dbConnection.feedPostsDao().subscribeOnLikedFeedPosts()
    }

    fun getLikedPostsCount(): Flowable<Int> {
        return dbConnection.feedPostsDao().subscribeOnFeedLikedCount()
    }

    fun getPostFromDb(postId: Int, sourceId: Int): Single<PostFullData> {
        return dbConnection.feedPostsDao().getPostById(postId, sourceId)
    }

    fun deletePostFromDb(post: PostFullData): Completable {
        return dbConnection.feedPostsDao().deletePostById(post.postId, post.sourceId)
    }

    fun setPostLikeInDb(post: PostFullData): Completable {
        return dbConnection.feedPostsDao()
            .setPostLikeById(post.postId, post.sourceId, post.likesCount)
    }

    fun deleteAllPostsAndInsertIntoDb(posts: ArrayList<PostFullData>) {
        return dbConnection.feedPostsDao().deleteAllFeedPostsAndInsert(posts)
    }

    fun deleteAllUserProfileInfoAndPostsAndInsertIntoDb(data: ArrayList<InfoRepresentationClass>) {
        val userProfile = data.filterIsInstance<UserProfileMainInfo>().first()
        val userPosts = data.filterIsInstance<PostFullData>()
        return dbConnection.feedPostsDao().deleteAllUserProfileInfoAndPostsAndInsert(userProfile,userPosts as ArrayList<PostFullData>)
    }


    //NETWORK REQUESTS
    //posts
    fun getPostsFromApi(): Single<ArrayList<PostFullData>> {
        return authNetworkService.getNewsFeed()
            .flatMap { addGroupsInfoToPosts(it) }
    }


    fun likePostApi(post: PostFullData): Completable {
        return authNetworkService
            .addLike(ADD_POST_TYPE, post.sourceId, post.postId)
    }

    fun deletePostFromFeedApi(post: PostFullData): Completable {
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

    private fun combinePostsAndGroups(
        postsData: NewsfeedApiResponse,
        groupsData: GroupsApiResponse
    ): ArrayList<PostFullData> {
        val currentRenderData = ArrayList<PostFullData>()
        postsData.response.items.forEach iterator@{ postData ->
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
                    postData.comments.count,
                    postData.reposts.count,
                    postData.views.count,
                    POST_SOURCE_FEED
                )
            )
        }
        currentRenderData.sortByDescending { it.date }
        return currentRenderData
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
        return VkApiClient.getAuthRetrofitClient().getUserOwnPosts(USER_WALL_OWN_POSTS_COUNT,USER_WALL_POSTS_OWNER )
            .flatMap {
                Single.just(combineUserInfoAndWallPosts(userProfileInfoApiResponse, it))
            }
    }
    private fun combineUserInfoAndWallPosts(
        userProfileInfoApiResponse: UserFullInfoResponse,
        userWallPostsResponse:UserWallPostsResponse

    ):ArrayList<InfoRepresentationClass>{
        val finalData = ArrayList<InfoRepresentationClass>()
        val userProfileApiResponse = userProfileInfoApiResponse.response.first()
        val userProfileMainInfo = UserProfileMainInfo(
            userProfileApiResponse.id,
            userProfileApiResponse.firstName,
            userProfileApiResponse.lastName,
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
                    postData.views.count,
                    POST_SOURCE_PROFILE
                )
            )
        }
        return  finalData
    }

    companion object {
        const val ADD_POST_TYPE = "post"
        const val DELETE_POST_TYPE = "wall"
        const val USER_PROFILE_INFO_FIELDS =
            "first_name, last_name, photo, about, bdate, city, country, career, education, followers_count, last_seen"
        const val USER_WALL_OWN_POSTS_COUNT = 100
        const val USER_WALL_POSTS_OWNER = "owner"
    }
}
