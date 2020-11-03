package ru.krivonosovdenis.fintechapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_liked_posts.*
import ru.krivonosovdenis.fintechapp.MainActivity
import ru.krivonosovdenis.fintechapp.R
import ru.krivonosovdenis.fintechapp.dbclasses.ApplicationDatabase
import ru.krivonosovdenis.fintechapp.rvcomponents.LikedPostsAdapter
import ru.krivonosovdenis.fintechapp.rvcomponents.PostsListItemDecoration

class PostsLikedFragment : Fragment() {

    private lateinit var rvAdapter: LikedPostsAdapter
    private val compositeDisposable = CompositeDisposable()

    companion object {
        fun newInstance(): PostsLikedFragment {
            return PostsLikedFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_liked_posts, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).postsBottomNavigation.isVisible = true
        rvAdapter = LikedPostsAdapter(activity as MainActivity)
        likedPostsRecyclerView.layoutManager = LinearLayoutManager(activity)
        likedPostsRecyclerView.adapter = rvAdapter
        likedPostsRecyclerView.addItemDecoration(PostsListItemDecoration())

        getLikedPosts()
    }

    private fun getLikedPosts() {
        compositeDisposable.add(
            ApplicationDatabase.getInstance(context!!)?.feedPostsDao()
                ?.subscribeOnLikedFeedPosts()!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onNext = {
                        rvAdapter.posts = it.toMutableList()
                    },
                    onError = {
                        showGetPostErrorDialog()
                    }
                )
        )
    }

    private fun showGetPostErrorDialog() {
        AlertDialog.Builder(activity as MainActivity)
            .setTitle(getString(R.string.alert_dialog_error_title_text))
            .setMessage(getString(R.string.get_data_alert_dialog_message_text))
            .setCancelable(false)
            .setNegativeButton(
                R.string.get_data_alert_dialog_negative_button_text
            ) { _, _ -> (activity as MainActivity).finish() }
            .setPositiveButton(
                R.string.get_data_alert_dialog_positive_button_text
            ) { _, _ ->
                getLikedPosts()
            }
            .create().show()
    }

}
