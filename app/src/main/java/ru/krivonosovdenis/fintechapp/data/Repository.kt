package ru.krivonosovdenis.fintechapp.data

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import org.joda.time.DateTime
import ru.krivonosovdenis.fintechapp.data.db.ApplicationDatabase
import ru.krivonosovdenis.fintechapp.data.network.ApiInterface
import ru.krivonosovdenis.fintechapp.data.network.VkApiClient
import ru.krivonosovdenis.fintechapp.dataclasses.PostFullData
import ru.krivonosovdenis.fintechapp.dataclasses.groupsdataclasses.GroupsApiResponse
import ru.krivonosovdenis.fintechapp.dataclasses.newsfeeddataclasses.NewsfeedApiResponse

class Repository(
    private val authNetworkService: ApiInterface,
    private val dbConnection: ApplicationDatabase
) {
    companion object {
        const val ADD_POST_TYPE = "post"
        const val DELETE_POST_TYPE = "wall"
    }

    //DB REQUESTS
    fun getAllPostsFromDb(): Flowable<List<PostFullData>> {
        return dbConnection.feedPostsDao().subscribeOnAllFeedPosts()
    }

    fun getLikedPostsFromDb(): Flowable<List<PostFullData>> {
        return dbConnection.feedPostsDao().subscribeOnLikedFeedPosts()
    }

    fun getLikedPostsCount(): Flowable<Int> {
        return dbConnection.feedPostsDao().subscribeOnLikedCount()
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

    fun deleteAllAndInsertIntoDb(posts: ArrayList<PostFullData>) {
        return dbConnection.feedPostsDao().deleteAllAndInsert(posts)
    }

    //NETWORK REQUESTS
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
            try {
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
                        isCommented = false,
                        isShared = false,
                        isHidden = false
                    )
                )
            } catch (e: Exception) {
                //как-то прокинуть ошибку дальше
            }

        }
        currentRenderData.sortByDescending { it.date }
        return currentRenderData
    }
}
