package com.enpassio.reactiveway

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

    val adapter = GitHubRepoAdapter()
    lateinit var subscription: Subscription
    lateinit var listView: ListView
    lateinit var editTextUsername: EditText
    lateinit var buttonSearch: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.list_view_repos)
        listView.setAdapter(adapter);

        editTextUsername = findViewById(R.id.edit_text_username);
        buttonSearch = findViewById(R.id.button_search);

        buttonSearch.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val username = editTextUsername.text.toString()
                if (!TextUtils.isEmpty(username)) {
                    getStarredRepos(username)
                }
            }
        })


    }
    override fun onDestroy() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe()
        }
        super.onDestroy()
    }

    private fun getStarredRepos(username: String) {
        subscription = GitHubClient.getInstance()
                .getStarredRepos(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<GitHubRepo>> {
                    override fun onCompleted() {
                        Log.d(TAG, "In onCompleted()")
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        Log.d(TAG, "In onError()")
                    }

                    override fun onNext(gitHubRepos: List<GitHubRepo>) {
                        Log.d(TAG, "In onNext()")
                        adapter.setGitHubRepos(gitHubRepos)
                    }
                })
    }
}