package ru.krivonosovdenis.fintechapp.data.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import ru.krivonosovdenis.fintechapp.dataclasses.getgroupsdataclasses.GroupsApiResponse
import ru.krivonosovdenis.fintechapp.dataclasses.getpostcommentsdataclasses.PostCommentsResponse
import ru.krivonosovdenis.fintechapp.dataclasses.newsfeeddataclasses.NewsfeedApiResponse
import ru.krivonosovdenis.fintechapp.dataclasses.objectdislikedataclasses.ObjectDislikeResponse
import ru.krivonosovdenis.fintechapp.dataclasses.objectlikedataclasses.ObjectLikeResponse
import ru.krivonosovdenis.fintechapp.dataclasses.postdeletedataclasses.PostDeleteResponse
import ru.krivonosovdenis.fintechapp.dataclasses.sendcommentdataclasses.SendCommentResponse
import ru.krivonosovdenis.fintechapp.dataclasses.sendpostdataclasses.SendPostResponse
import ru.krivonosovdenis.fintechapp.dataclasses.userprofiledataclasses.userfullinfodataclasses.UserFullInfoResponse
import ru.krivonosovdenis.fintechapp.dataclasses.userprofiledataclasses.userwallpostsdataclasses.UserWallPostsResponse

interface ApiInterface {
    //FEED TAB
    @GET("newsfeed.get")
    fun getNewsFeed(): Single<NewsfeedApiResponse>

    @GET("groups.getById")
    fun getGroups(
        @Query("group_ids") groups: String,
    ): Single<GroupsApiResponse>

    //По доке непонятно как слать пост запросом с телом. Пока шлю гетом. Можно также постом и
    //добавлять параметры в юрл запроса, но это криво
    @GET("likes.add")
    fun addLike(
        @Query("type") type: String,
        @Query("owner_id") ownerId: Int,
        @Query("item_id") itemId: Int,
    ): Single<ObjectLikeResponse>

    @GET("likes.delete")
    fun removeLike(
        @Query("type") type: String,
        @Query("owner_id") ownerId: Int,
        @Query("item_id") itemId: Int,
    ): Single<ObjectDislikeResponse>

    @GET("newsfeed.ignoreItem")
    fun deletePostFromFeed(
        @Query("type") type: String,
        @Query("owner_id") ownerId: Int,
        @Query("item_id") itemId: Int,
    ): Single<PostDeleteResponse>

    //POST DETAILS TAB
    @GET("wall.getComments")
    fun getPostComments(
        @Query("need_likes") needLikes: Int,
        @Query("type") type: String,
        @Query("post_id") postId: Int,
        @Query("owner_id") ownerId: Int,
        @Query("extended") extended: Int,
    ): Single<PostCommentsResponse>

    //та же фигня. Отправляю  get'ом, а не POST'ом
    @GET("wall.createComment")
    fun sendCommentToPost(
        @Query("message") message: String,
        @Query("post_id") postId: Int,
        @Query("owner_id") ownerId: Int,
    ): Single<SendCommentResponse>


    //PROFILE TAB
    @GET("users.get")
    fun getUserProfileInfo(
        @Query("fields") fields: String,
    ): Single<UserFullInfoResponse>

    @GET("wall.get")
    fun getUserOwnPosts(
        @Query("count") count: Int,
        @Query("filter") filter: String,
    ): Single<UserWallPostsResponse>

    //та же фигня. Отправляю  get'ом, а не POST'ом
    @GET("wall.post")
    fun sendPostToOwnWall(
        @Query("message") message: String,
        @Query("attachments") attachments: String,
    ): Single<SendPostResponse>

}
