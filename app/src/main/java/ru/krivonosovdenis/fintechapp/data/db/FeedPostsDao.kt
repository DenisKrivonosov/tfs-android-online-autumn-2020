package ru.krivonosovdenis.fintechapp.data.db

import android.util.Log
import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import ru.krivonosovdenis.fintechapp.dataclasses.CommentData
import ru.krivonosovdenis.fintechapp.dataclasses.PostFullData
import ru.krivonosovdenis.fintechapp.dataclasses.UserProfileMainInfo

@Dao
interface FeedPostsDao {
    @Query("SELECT * from posts where postSource=${DBConstants.POST_SOURCE_FEED} order by date desc")
    fun subscribeOnFeedPosts(): Flowable<List<PostFullData>>

    @Query("SELECT * from posts where  postSource = ${DBConstants.POST_SOURCE_FEED} and isLiked=1 order by date desc")
    fun subscribeOnLikedPosts(): Flowable<List<PostFullData>>

    @Query("SELECT * from posts where postSource=${DBConstants.POST_SOURCE_PROFILE} order by date desc")
    fun subscribeOnUserOwnPosts(): Flowable<List<PostFullData>>

    @Query("SELECT count(*) from posts where isLiked=1 and postSource= ${DBConstants.POST_SOURCE_FEED}")
    fun subscribeOnFeedLikedCount(): Flowable<Int>

    @Query("SELECT count(*) from posts where postSource= ${DBConstants.POST_SOURCE_FEED}")
    fun getAllFeedPostsCount(): Single<Int>

    @Query("DELETE from posts where postId=:postId and sourceId=:sourceId ")
    fun deletePostById(postId: Int, sourceId: Int): Completable

    @Query("UPDATE posts set isLiked=:isLiked, likesCount=:likesCount where postId=:postId and sourceId=:sourceId")
    fun setPostIsLikedById(postId: Int, sourceId: Int, likesCount: Int, isLiked:Int): Completable

    @Query("SELECT * from posts where postId=:postId and sourceId=:sourceId")
    fun getPostById(postId: Int, sourceId: Int): Flowable<PostFullData>

    @Query("SELECT * from comments where postId=:postId and ownerId=:ownerId")
    fun subscribeOnPostComments(postId: Int, ownerId: Int): Flowable<List<CommentData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPostsInDb(posts: ArrayList<PostFullData>)

    @Query("DELETE FROM posts where sourceId = ${DBConstants.POST_SOURCE_FEED}")
    fun deleteFeedPosts()

    @Query("DELETE FROM posts where sourceId = ${DBConstants.POST_SOURCE_PROFILE}")
    fun deleteUserOwnPosts()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserInfo(userInfo: UserProfileMainInfo)

    @Query("DELETE from user_profile_info")
    fun deleteUserInfo()

    @Query("SELECT * from user_profile_info limit 1")
    fun subscribeOnUserInfo(): Flowable<UserProfileMainInfo>


    @Query("SELECT * from user_profile_info where userId=:vkId")
    fun getUserInfoById(vkId: Int): Single<UserProfileMainInfo>

    @Query("DELETE FROM comments where postId=:postId and ownerId=:ownerId")
    fun deletePostComments(postId:Int, ownerId:Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCommentsInDb(comments: ArrayList<CommentData>)

    @Transaction
    fun deleteAllFeedPostsAndInsert(posts: ArrayList<PostFullData>) {
        deleteFeedPosts()
        insertPostsInDb(posts)
    }

    @Transaction
    fun deleteAllUserProfileInfoAndPostsAndInsert(userInfo:UserProfileMainInfo, posts: ArrayList<PostFullData>) {
        deleteUserInfo()
        deleteUserOwnPosts()
        insertUserInfo(userInfo)
        insertPostsInDb(posts)
    }

    @Transaction
    fun deleteAllPostCommentsAndInsertIntoDb(postId:Int, ownerId:Int, comments:ArrayList<CommentData>) {
        Log.e("insidetrans","true1");
        deletePostComments(postId,ownerId)
        Log.e("insidetrans","true2");
        insertCommentsInDb(comments)
        Log.e("insidetrans","true3");

    }

}
