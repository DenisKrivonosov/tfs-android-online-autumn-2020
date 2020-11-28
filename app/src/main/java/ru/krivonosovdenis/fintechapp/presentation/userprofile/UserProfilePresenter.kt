package ru.krivonosovdenis.fintechapp.presentation.userprofile

import android.util.Log
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.dataclasses.PostFullData
import ru.krivonosovdenis.fintechapp.dataclasses.UserProfileMainInfo
import ru.krivonosovdenis.fintechapp.di.GlobalDI
import ru.krivonosovdenis.fintechapp.presentation.base.mvp.presenter.RxPresenter
import ru.krivonosovdenis.fintechapp.presentation.postdetails.PostDetailsView

class UserProfilePresenter(
    private val repository: Repository
): RxPresenter<UserProfileView>(UserProfileView::class.java) {
    fun loadUserProfileAndPostsInfoFromApiAndInsertIntoDB(){
        repository.getUserProfileAndWallFromApi()
            .flatMapCompletable {
                Completable.fromAction {
                    val userProfile = it.first() as UserProfileMainInfo
                    val wallPostItem = it.get(1) as PostFullData
                    Log.e("userProfile",userProfile.firstName);
                    Log.e("userWallPost",wallPostItem.text)
                    repository.deleteAllUserProfileInfoAndPostsAndInsertIntoDb(it)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterTerminate {
                GlobalDI.INSTANCE.isFirstAllPostsFragmentOpen = false
                view.setRefreshing(false)
                view.showProfileInfo()
            }
            .subscribe()
            .disposeOnFinish()
    }
}
