package ru.krivonosovdenis.fintechapp.presentation.likedposts

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.dataclasses.PostData
import ru.krivonosovdenis.fintechapp.presentation.base.mvp.presenter.BaseRxPresenter

@InjectViewState
class LikedPostsPresenter(
    private val repository: Repository
) : BaseRxPresenter<LikedPostsView>() {

    fun subscribeOnAllLikedPostsFromDB() {
        repository.getLikedPostsFromDb()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    if (it.count() > 0) {
                        viewState.showPosts(it)
                    }
                    else {
                        viewState.showEmptyLikedPostsView()
                    }
                },
                onError = {
                    viewState.showGetPostErrorDialog()
                    viewState.showErrorView()
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
