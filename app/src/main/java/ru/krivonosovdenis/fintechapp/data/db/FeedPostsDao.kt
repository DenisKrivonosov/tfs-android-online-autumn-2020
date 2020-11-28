package ru.krivonosovdenis.fintechapp.data.db

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import ru.krivonosovdenis.fintechapp.dataclasses.PostFullData
import ru.krivonosovdenis.fintechapp.dataclasses.UserProfileMainInfo

@Dao
interface FeedPostsDao {
    @Query("SELECT * from posts where postSource=${DBConstants.POST_SOURCE_FEED} order by date desc")
    fun subscribeOnAllFeedPosts(): Flowable<List<PostFullData>>

    @Query("SELECT * from posts where isLiked=1 and postSource = ${DBConstants.POST_SOURCE_FEED} order by date desc")
    fun subscribeOnLikedFeedPosts(): Flowable<List<PostFullData>>

    @Query("SELECT count(*) from posts where postSource= ${DBConstants.POST_SOURCE_FEED}")
    fun getAllFeedPostsCount(): Single<Int>

    @Query("SELECT count(*) from posts where isLiked=1 and postSource= ${DBConstants.POST_SOURCE_FEED}")
    fun subscribeOnFeedLikedCount(): Flowable<Int>

    @Query("DELETE from posts where postId=:postId and sourceId=:sourceId ")
    fun deletePostById(postId: Int, sourceId: Int): Completable

    @Query("UPDATE posts set isLiked=1, likesCount=:likesCount where postId=:postId and sourceId=:sourceId")
    fun setPostLikeById(postId: Int, sourceId: Int, likesCount: Int): Completable

    @Query("SELECT * from posts where postId=:postId and sourceId=:sourceId")
    fun getPostById(postId: Int, sourceId: Int): Single<PostFullData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPostsInDb(posts: ArrayList<PostFullData>)

    @Query("DELETE FROM posts where sourceId = ${DBConstants.POST_SOURCE_FEED}")
    fun deleteAllFeedPosts()

    @Query("DELETE FROM posts where sourceId = ${DBConstants.POST_SOURCE_PROFILE}")
    fun deleteAllUserProfilePosts()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserInfo(userInfo: UserProfileMainInfo)

    @Query("DELETE from user_profile_info where userId=:id")
    fun deleteUserInfoById(id: Int)

    @Transaction
    fun deleteAllFeedPostsAndInsert(posts: ArrayList<PostFullData>) {
        deleteAllFeedPosts()
        insertPostsInDb(posts)
    }

    @Transaction
    fun deleteAllUserProfileInfoAndPostsAndInsert(userinfo:UserProfileMainInfo, posts: ArrayList<PostFullData>) {
        deleteUserInfoById(userinfo.userId)
        deleteAllUserProfilePosts()
        insertPostsInDb(posts)
    }

}
