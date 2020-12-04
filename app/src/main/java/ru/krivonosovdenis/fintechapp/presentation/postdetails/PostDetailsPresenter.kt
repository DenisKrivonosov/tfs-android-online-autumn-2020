package ru.krivonosovdenis.fintechapp.presentation.postdetails

import android.util.Log
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.dataclasses.CommentData
import ru.krivonosovdenis.fintechapp.dataclasses.PostData
import ru.krivonosovdenis.fintechapp.presentation.base.mvp.presenter.BaseRxPresenter

@InjectViewState
class PostDetailsPresenter(
    private val repository: Repository
) : BaseRxPresenter<PostDetailsView>() {

    fun subscribeOnPostDetailsFromDb(postId: Int, sourceId: Int) {
        repository.getPostFromDb(postId, sourceId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onNext = {
                    Log.e("postUpdated",it.likesCount.toString());
                    Log.e("postUpdated",it.isLiked.toString());
                    viewState.renderPostDetails(it)
                }, onError = {
                    Log.e("error1",it.stackTraceToString())
                    viewState.showDbLoadingErrorView()
                }
            )
            .disposeOnFinish()
    }

    fun subscribeOnPostCommentsFromDb(postId: Int, sourceId: Int) {
        repository.getPostCommentsFromDb(postId, sourceId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onNext = {
                    viewState.renderPostComments(it)
                }, onError = {
                    Log.e("error2",it.stackTraceToString())
                    viewState.showDbLoadingErrorView()
                }
            )
            .disposeOnFinish()
    }

    fun loadPostCommentsFromApiAndInsertIntoDB(postId: Int, ownerId: Int) {
        repository.getPostCommentsFromApi(postId, ownerId)
            .flatMapCompletable {
                Completable.fromAction {
                    repository.deleteAllPostCommentsAndInsertIntoDb(
                        postId,
                        ownerId,
                        it as ArrayList<CommentData>
                    )
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterTerminate {
                viewState.setRefreshing(false)
                viewState.showPostView()
            }
            .subscribeBy(
                onComplete = {},
                onError = {
                    Log.e("error3",it.stackTraceToString())
                    viewState.showLoadDataFromNetworkErrorView()
                }
            )
            .disposeOnFinish()
    }

    fun likePostOnApiAndDb(post: PostData) {
        repository.likePostApi(post)
            .flatMapCompletable {
                repository.setPostIsLikedInDb(
                    post.copy(
                        likesCount = it.response.likes,
                        isLiked = true
                    )
                )
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {},
                onError = {
                    viewState.showPostUpdateErrorToast()
                }
            )
            .disposeOnFinish()
    }

    fun dislikePostOnApiAndDb(post: PostData) {
        repository.dislikePostApi(post)
            .flatMapCompletable {
                repository.setPostIsLikedInDb(
                    post.copy(
                        likesCount = it.response.likes,
                        isLiked = false
                    )
                )
            }

            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {},
                onError = {
                    viewState.showPostUpdateErrorToast()
                }
            )
            .disposeOnFinish()
    }

    fun likeCommentOnApiAndDb(comment: CommentData) {
        repository.likeCommentApi(comment)
            .flatMapCompletable {
                repository.setCommentIsLikedInDb(
                    comment.copy(
                        likesCount = it.response.likes,
                        isLiked = true
                    )
                )
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {},
                onError = {
                    viewState.showCommentUpdateErrorToast()
                }
            )
            .disposeOnFinish()
    }

    fun dislikeCommentOnApiAndDb(comment: CommentData) {
        repository.dislikeCommentApi(comment)
            .flatMapCompletable {
                repository.setCommentIsLikedInDb(
                    comment.copy(
                        likesCount = it.response.likes,
                        isLiked = false
                    )
                )
            }

            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {},
                onError = {
                    viewState.showCommentUpdateErrorToast()
                }
            )
            .disposeOnFinish()
    }
}
