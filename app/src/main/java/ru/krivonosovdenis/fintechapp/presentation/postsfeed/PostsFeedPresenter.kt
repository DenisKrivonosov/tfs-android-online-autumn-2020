package ru.krivonosovdenis.fintechapp.presentation.postsfeed

import android.util.Log
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.dataclasses.PostFullData
import ru.krivonosovdenis.fintechapp.di.GlobalDI
import ru.krivonosovdenis.fintechapp.presentation.base.mvp.presenter.RxPresenter

class PostsFeedPresenter(
    private val repository: Repository
) : RxPresenter<PostsFeedView>(PostsFeedView::class.java) {

    fun loadPostsFromApiAndInsertIntoDB() {
        if (GlobalDI.INSTANCE.isFirstAllPostsFragmentOpen) {
            view.showLoadingView()
        }
        repository.getFeedPostsFromApi()
            .flatMapCompletable {
                Completable.fromAction {
                    repository.deleteAllPostsAndInsertIntoDb(it)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterTerminate {
                GlobalDI.INSTANCE.isFirstAllPostsFragmentOpen = false
                view.setRefreshing(false)
                view.showPostsView()
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
                    if (it.count() > 0) {
                        view.showPosts(it)
                    }
                },
                onError = {
                    view.showDbGetFeedErrorView()
                }
            )
            .disposeOnFinish()
    }

    fun deletePostOnApiAndDb(post: PostFullData) {
        repository.deletePostFromFeedApi(post)
            .flatMapCompletable {
                repository.deletePostFromDb(post)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {},
                onError = {
                    view.showPostUpdateErrorToast()
                }
            )
            .disposeOnFinish()
    }

    fun likePostOnApiAndDb(post: PostFullData) {
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
                    view.showPostUpdateErrorToast()
                }
            )
            .disposeOnFinish()
    }

    fun dislikePostOnApiAndDb(post: PostFullData) {
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
                    view.showPostUpdateErrorToast()
                }
            )
            .disposeOnFinish()
    }
}
