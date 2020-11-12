package ru.krivonosovdenis.fintechapp.data.db

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import ru.krivonosovdenis.fintechapp.dataclasses.PostFullData

@Dao
interface FeedPostsDao {
    @Query("SELECT * from all_feed_posts order by date desc")
    fun subscribeOnAllFeedPosts(): Flowable<List<PostFullData>>

    @Query("SELECT * from all_feed_posts where isLiked=1 order by date desc")
    fun subscribeOnLikedFeedPosts(): Flowable<List<PostFullData>>

    @Query("SELECT count(*) from all_feed_posts")
    fun getAllPostsCount(): Single<Int>

    @Query("SELECT count(*) from all_feed_posts where isLiked=1")
    fun subscribeOnLikedCount(): Flowable<Int>

    @Query("DELETE from all_feed_posts where postId=:postId and sourceId=:sourceId")
    fun deletePostById(postId: Int, sourceId: Int): Completable

    @Query("UPDATE all_feed_posts set isLiked=1, likesCount=:likesCount where postId=:postId and sourceId=:sourceId")
    fun setPostLikeById(postId: Int, sourceId: Int, likesCount: Int): Completable

    @Query("SELECT * from all_feed_posts where postId=:postId and sourceId=:sourceId")
    fun getPostById(postId: Int, sourceId: Int): Single<PostFullData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPostsInDb(posts: ArrayList<PostFullData>)

    @Query("DELETE FROM all_feed_posts")
    fun deleteAllPosts()

    @Transaction
    fun deleteAllAndInsert(posts: ArrayList<PostFullData>) {
        deleteAllPosts()
        insertPostsInDb(posts)
    }

}
