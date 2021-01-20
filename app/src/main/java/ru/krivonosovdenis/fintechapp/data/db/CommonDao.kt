package ru.krivonosovdenis.fintechapp.data.db

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import ru.krivonosovdenis.fintechapp.dataclasses.CommentData
import ru.krivonosovdenis.fintechapp.dataclasses.PostData
import ru.krivonosovdenis.fintechapp.dataclasses.UserProfileData

@Dao
interface CommonDao {
    @Query("SELECT * from posts where postSource=${ApplicationDatabase.POST_SOURCE_FEED} order by date desc")
    fun subscribeOnFeedPosts(): Flowable<List<PostData>>

    @Query("SELECT * from posts where  postSource = ${ApplicationDatabase.POST_SOURCE_FEED} and isLiked=1 order by date desc")
    fun subscribeOnLikedPosts(): Flowable<List<PostData>>

    @Query("SELECT * from posts where postSource=${ApplicationDatabase.POST_SOURCE_PROFILE} order by date desc")
    fun subscribeOnUserOwnPosts(): Flowable<List<PostData>>

    @Query("SELECT count(*) from posts where isLiked=1 and postSource= ${ApplicationDatabase.POST_SOURCE_FEED}")
    fun subscribeOnFeedLikedCount(): Flowable<List<Int>>

    @Query("SELECT count(*) from posts where postSource= ${ApplicationDatabase.POST_SOURCE_FEED}")
    fun getAllFeedPostsCount(): Single<Int>

    @Query("DELETE from posts where postId=:postId and sourceId=:sourceId ")
    fun deletePostById(postId: Int, sourceId: Int): Completable

    @Query("UPDATE posts set isLiked=:isLiked, likesCount=:likesCount where postId=:postId and sourceId=:sourceId")
    fun setPostIsLikedById(postId: Int, sourceId: Int, likesCount: Int, isLiked: Int): Completable

    @Query("SELECT * from posts where postId=:postId and sourceId=:sourceId")
    fun getPostById(postId: Int, sourceId: Int): Flowable<PostData>

    @Query("SELECT * from comments where postId=:postId and ownerId=:ownerId")
    fun subscribeOnPostComments(postId: Int, ownerId: Int): Flowable<List<CommentData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPostsInDb(posts: ArrayList<PostData>)

    @Query("DELETE FROM posts where sourceId = ${ApplicationDatabase.POST_SOURCE_FEED}")
    fun deleteFeedPosts()

    @Query("DELETE FROM posts where sourceId = ${ApplicationDatabase.POST_SOURCE_PROFILE}")
    fun deleteUserOwnPosts()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserInfo(userInfo: UserProfileData)

    @Query("DELETE from user_profile_info")
    fun deleteUserInfo()

    @Query("SELECT * from user_profile_info limit 1")
    fun subscribeOnUserInfo(): Flowable<List<UserProfileData>>

    @Query("SELECT * from user_profile_info where userId=:vkId")
    fun getUserInfoById(vkId: Int): Single<UserProfileData>

    @Query("DELETE FROM comments where postId=:postId and ownerId=:ownerId")
    fun deletePostComments(postId: Int, ownerId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCommentsInDb(comments: ArrayList<CommentData>)

    @Query("UPDATE posts set commentsCount=:commentsCount where  sourceId=:ownerId  and postId=:postId")
    fun updatePostCommentsCount(postId: Int, ownerId: Int, commentsCount: Int)

    @Query("UPDATE comments set isLiked=:isLiked, likesCount=:likesCount where commentId=:commentId and ownerId=:ownerId")
    fun setCommentIsLikedById(
        commentId: Int,
        ownerId: Int,
        likesCount: Int,
        isLiked: Int
    ): Completable

    @Transaction
    fun deleteAllFeedPostsAndInsert(posts: ArrayList<PostData>) {
        deleteFeedPosts()
        insertPostsInDb(posts)
    }

    @Transaction
    fun deleteAllUserProfileInfoAndPostsAndInsert(
        userInfo: UserProfileData,
        posts: ArrayList<PostData>
    ) {
        deleteUserInfo()
        deleteUserOwnPosts()
        insertUserInfo(userInfo)
        insertPostsInDb(posts)
    }

    @Transaction
    fun deleteAllPostCommentsAndInsertIntoDb(
        postId: Int,
        ownerId: Int,
        comments: ArrayList<CommentData>
    ) {
        deletePostComments(postId, ownerId)
        insertCommentsInDb(comments)
        updatePostCommentsCount(postId, ownerId, comments.count())
    }
}
