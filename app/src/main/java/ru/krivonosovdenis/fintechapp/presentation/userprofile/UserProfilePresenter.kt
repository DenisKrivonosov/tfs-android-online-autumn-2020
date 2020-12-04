package ru.krivonosovdenis.fintechapp.presentation.userprofile

import android.util.Log
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.dataclasses.PostData
import ru.krivonosovdenis.fintechapp.dataclasses.UserProfileData
import ru.krivonosovdenis.fintechapp.presentation.base.mvp.presenter.BaseRxPresenter


@InjectViewState
class UserProfilePresenter(
    private val repository: Repository
): BaseRxPresenter<UserProfileView>() {
    fun loadUserProfileAndPostsInfoFromApiAndInsertIntoDB(){
        repository.getUserProfileAndWallFromApi()
            .flatMapCompletable {
                Completable.fromAction {
                    val userProfile = it.first() as UserProfileData
                    val wallPostItem = it[1] as PostData
                    Log.e("userProfile",userProfile.firstName);
                    Log.e("userWallPost",wallPostItem.text)
                    repository.deleteAllUserProfileInfoAndPostsAndInsertIntoDb(it)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterTerminate {
                viewState.setRefreshing(false)
                viewState.showProfileInfo()
            }
            .subscribeBy (
                onComplete = {},
                onError = {
                    Log.e("userProfileError",it.stackTraceToString());
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
                    if(it==null){
                        viewState.showLoadingView()
                    }
                    else {
                        Log.e("userProfileSucces",it.firstName);
                        viewState.renderProfileAndShow(it)
                    }

                },
                onError = {
                    Log.e("userProfileError",it.stackTraceToString());
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
