package ru.krivonosovdenis.fintechapp.dbclasses

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import ru.krivonosovdenis.fintechapp.dataclasses.PostRenderData

@Dao
interface FeedPostsDao {
    @Query("SELECT * from all_feed_posts order by date desc")
    fun subscribeOnAllFeedPosts(): Flowable<List<PostRenderData>>

    @Query("SELECT * from all_feed_posts where isLiked=1 order by date desc")
    fun subscribeOnLikedFeedPosts(): Flowable<List<PostRenderData>>

    @Query("SELECT count(*) from all_feed_posts where isLiked=1")
    fun subscribeOnLikedCount(): Flowable<Int>

    @Query("DELETE from all_feed_posts where postId=:postId and sourceId=:sourceId")
    fun deletePostById(postId: Int, sourceId: Int): Completable

    @Query("UPDATE all_feed_posts set isLiked=1, likesCount=:likesCount where postId=:postId and sourceId=:sourceId")
    fun setPostLikeById(postId: Int, sourceId: Int, likesCount: Int): Completable

    @Query("SELECT * from all_feed_posts where postId=:postId and sourceId=:sourceId")
    fun getPostById(postId: Int, sourceId: Int): Single<PostRenderData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPostsInDb(posts: ArrayList<PostRenderData>): Completable

    @Query("DELETE FROM all_feed_posts")
    fun deleteAllPosts(): Completable
}
