package ru.krivonosovdenis.fintechapp.networkutils

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import ru.krivonosovdenis.fintechapp.dataclasses.groupsdataclasses.GroupsApiResponse
import ru.krivonosovdenis.fintechapp.dataclasses.newsfeeddataclasses.NewsfeedApiResponse
import ru.krivonosovdenis.fintechapp.dataclasses.postdislikedataclasses.PostDislikeResponse
import ru.krivonosovdenis.fintechapp.dataclasses.postlikedataclasses.PostLikeResponse

interface ApiInterface {
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
}
