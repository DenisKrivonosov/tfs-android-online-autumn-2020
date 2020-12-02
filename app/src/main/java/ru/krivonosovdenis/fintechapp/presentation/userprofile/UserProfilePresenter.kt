package ru.krivonosovdenis.fintechapp.presentation.userprofile

import android.util.Log
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.dataclasses.PostFullData
import ru.krivonosovdenis.fintechapp.dataclasses.UserProfileMainInfo
import ru.krivonosovdenis.fintechapp.di.GlobalDI
import ru.krivonosovdenis.fintechapp.presentation.base.mvp.presenter.RxPresenter

class UserProfilePresenter(
    private val repository: Repository
): RxPresenter<UserProfileView>(UserProfileView::class.java) {
    fun loadUserProfileAndPostsInfoFromApiAndInsertIntoDB(){
        if (GlobalDI.INSTANCE.isFirstUserProfileFragmentOpen) {
            view.showLoadingView()
        }
        repository.getUserProfileAndWallFromApi()
            .flatMapCompletable {
                Completable.fromAction {
                    val userProfile = it.first() as UserProfileMainInfo
                    val wallPostItem = it[1] as PostFullData
                    Log.e("userProfile",userProfile.firstName);
                    Log.e("userWallPost",wallPostItem.text)
                    repository.deleteAllUserProfileInfoAndPostsAndInsertIntoDb(it)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterTerminate {
                GlobalDI.INSTANCE.isFirstUserProfileFragmentOpen = false
                view.setRefreshing(false)
                view.showProfileInfo()
            }
            .subscribeBy (
                onComplete = {},
                onError = {
                    Log.e("userProfileError",it.stackTraceToString());
                    view.showLoadDataFromNetworkErrorView()
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
                    Log.e("userProfileSucces",it.firstName);
                    view.renderProfileAndShow(it)
                },
                onError = {
                    Log.e("userProfileError",it.stackTraceToString());
                    view.showErrorView()
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
                        view.renderUserPostsAndShow(it)
                    }
                },
                onError = {
                    view.showErrorView()
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
