package ru.krivonosovdenis.fintechapp.presentation.postdetails

import android.util.Log
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.dataclasses.CommentData
import ru.krivonosovdenis.fintechapp.dataclasses.PostFullData
import ru.krivonosovdenis.fintechapp.dataclasses.UserProfileMainInfo
import ru.krivonosovdenis.fintechapp.di.GlobalDI
import ru.krivonosovdenis.fintechapp.presentation.base.mvp.presenter.RxPresenter

class PostDetailsPresenter(
    private val repository: Repository
) : RxPresenter<PostDetailsView>(PostDetailsView::class.java) {

    fun subscribeOnPostDetailsFromDb(postId: Int, sourceId: Int) {
        repository.getPostFromDb(postId, sourceId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onNext = {
                    view.renderPostDetails(it)
                }, onError = {
                    view.showDbLoadingErrorView()
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
                    Log.e("new_post_comments",it.count().toString())
                    Log.e("new_post_comments",it[0].commenterAvatar)
                    view.renderPostComments(it)
                }, onError = {
                    view.showDbLoadingErrorView()
                }
            )
            .disposeOnFinish()
    }

    fun loadPostCommentsFromApiAndInsertIntoDB(postId: Int, ownerId: Int){
        repository.getPostCommentsFromApi(postId,ownerId)
            .flatMapCompletable {
                Completable.fromAction {
                    repository.deleteAllPostCommentsAndInsertIntoDb(postId,ownerId,it as ArrayList<CommentData>)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterTerminate {
                view.setRefreshing(false)
                view.showPostView()
            }
            .subscribeBy (
                onComplete = {},
                onError = {
                    Log.e("postDetailsError",it.toString());
                    view.showLoadDataFromNetworkErrorView()
                }
            )
            .disposeOnFinish()
    }
}
