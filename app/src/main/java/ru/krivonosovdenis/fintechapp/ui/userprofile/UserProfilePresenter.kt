package ru.krivonosovdenis.fintechapp.ui.userprofile

import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.dataclasses.PostData
import ru.krivonosovdenis.fintechapp.ui.base.mvp.presenter.BaseRxPresenter


@InjectViewState
class UserProfilePresenter(
    private val repository: Repository
) : BaseRxPresenter<UserProfileView>() {
    fun loadUserProfileAndPostsInfoFromApiAndInsertIntoDB() {
        repository.getUserProfileAndWallFromApi()
            .flatMapCompletable {
                Completable.fromAction {
                    repository.deleteAllUserProfileInfoAndPostsAndInsertIntoDb(it)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterTerminate {
                viewState.setRefreshing(false)
                viewState.showProfileInfo()
            }
            .subscribeBy(
                onComplete = {},
                onError = {
                    viewState.showLoadDataFromNetworkErrorView()
                }
            )
            .disposeOnFinish()
    }

    fun subscribeOnUserInfoFromDB() {
        repository.getUserInfoFlowableFromDb()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    //Пришлось обернуть счетчик в лист, так как сталкивался с багом, устранить
                    //который не получилось. База не эммитит ничего, если ы ходе выполнения
                    //запроса не было найдено ни одного профиля. Лист же эммитится в любом случае
                    if (it.count() == 0) {
                        viewState.showLoadingView()
                    } else {
                        viewState.renderProfileAndShow(it[0])
                    }

                },
                onError = {
                    viewState.showErrorView()
                }
            )
            .disposeOnFinish()
    }

    fun subscribeOnUserOwnPostsFromDB() {
        repository.getUserOwnPostsFromDb()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    if (it.count() > 0) {
                        viewState.renderUserPostsAndShow(it)
                    }
                },
                onError = {
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
