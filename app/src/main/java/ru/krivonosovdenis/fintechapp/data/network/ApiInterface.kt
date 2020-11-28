package ru.krivonosovdenis.fintechapp.data.network

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import ru.krivonosovdenis.fintechapp.dataclasses.groupsdataclasses.GroupsApiResponse
import ru.krivonosovdenis.fintechapp.dataclasses.newsfeeddataclasses.NewsfeedApiResponse
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
    ): Completable

    @GET("newsfeed.ignoreItem")
    fun deletePostFromFeed(
        @Query("type") type: String,
        @Query("owner_id") ownerId: Int,
        @Query("item_id") itemId: Int,
    ): Completable

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

}
