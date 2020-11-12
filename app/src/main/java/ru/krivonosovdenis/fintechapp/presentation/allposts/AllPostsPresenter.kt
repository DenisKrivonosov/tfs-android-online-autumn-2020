package ru.krivonosovdenis.fintechapp.presentation.allposts

import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.dataclasses.PostFullData
import ru.krivonosovdenis.fintechapp.di.GlobalDI
import ru.krivonosovdenis.fintechapp.presentation.base.mvp.presenter.RxPresenter

class AllPostsPresenter(
    private val repository: Repository
) : RxPresenter<AllPostsView>(AllPostsView::class.java) {

    fun loadPostsFromApiAndInsertIntoDB() {
        if (GlobalDI.INSTANCE.isFirstAllPostsFragmentOpen) {
            view.showLoadingView()
        }
        repository.getPostsFromApi()
            .flatMapCompletable {
                Completable.fromAction {
                    repository.deleteAllAndInsertIntoDb(it)
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
                    view.showErrorView()
                }
            )
            .disposeOnFinish()
    }

    fun deletePostApiAndDb(post: PostFullData) {
        repository.deletePostFromFeedApi(post)
            .andThen(repository.deletePostFromDb(post))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
            .disposeOnFinish()
    }

    fun likePostOnApiAndDb(post: PostFullData) {
        repository.likePostApi(post)
            .andThen(
                repository.setPostLikeInDb(
                    post.copy(
                        likesCount = post.likesCount + 1,
                        isLiked = true
                    )
                )
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
            .disposeOnFinish()
    }
}
