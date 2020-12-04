package ru.krivonosovdenis.fintechapp.presentation.postsfeed

import android.util.Log
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.dataclasses.PostData
import ru.krivonosovdenis.fintechapp.presentation.base.mvp.presenter.BaseRxPresenter

@InjectViewState
class PostsFeedPresenter(
    private val repository: Repository
) : BaseRxPresenter<PostsFeedView>() {


    fun loadPostsFromApiAndInsertIntoDB() {
        repository.getFeedPostsFromApi()
            .flatMapCompletable {
                Completable.fromAction {
                    repository.deleteAllPostsAndInsertIntoDb(it)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterTerminate {
                viewState.setRefreshing(false)
                viewState.showPostsView()
            }
            .subscribe()
            .disposeOnFinish()
    }

    fun subscribeOnAllPostsFromDB() {
        repository.getAllPostsFromDb()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    Log.e("POSTSCOUNT",it.count().toString());
                    if (it.count() > 0) {
                        viewState.showPosts(it)
                    }
                    else {
                        viewState.showLoadingView()
                    }
                },
                onError = {
                    Log.e("ERROR", it.stackTraceToString())
                    viewState.showDbGetFeedErrorView()
                }
            )
            .disposeOnFinish()
    }

    fun deletePostOnApiAndDb(post: PostData) {
        repository.deletePostFromFeedApi(post)
            .flatMapCompletable {
                repository.deletePostFromDb(post)
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
}
