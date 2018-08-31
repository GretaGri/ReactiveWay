package com.enpassio.reactiveway

import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import retrofit2.http.Url
import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

    val adapter = GitHubRepoAdapter()

    private val redirecturi = "com.enpassio.reactiveway://callbackurl"

    private val clientId = BuildConfig.CLIENT_ID
    private val clientSecret = BuildConfig.CLIENT_SECRET

    var subscription: Subscription?= null
    lateinit var listView: ListView
    lateinit var editTextUsername: EditText
    lateinit var buttonSearch: Button
    lateinit var buttonAuthorise: Button
    var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonAuthorise = findViewById(R.id.button_authorise)
        editTextUsername = findViewById(R.id.edit_text_username)
        buttonSearch = findViewById(R.id.button_search)

        buttonAuthorise.setOnClickListener(
                object : View.OnClickListener {
                    override fun onClick(v: View) {
                        val intent = Intent (Intent.ACTION_VIEW,
                                Uri.parse("https://github.com/login/oauth/authorize"
                                        +"?client_id="
                                        + clientId
                                        + "&scope=repo&redirect_uri="
                                        + redirecturi))
                        startActivity(intent)
                    }
                })

        if (uri == null) {
            buttonSearch.visibility = View.VISIBLE
            editTextUsername.visibility = View.VISIBLE

            buttonSearch.setOnClickListener { view ->
                    val username = editTextUsername.text.toString()
                    if (!TextUtils.isEmpty(username)) {
                        getStarredRepos(username)
                    }}
        }else{

        }

        buttonSearch.setOnClickListener { view ->
                val username = editTextUsername.text.toString()
                if (!TextUtils.isEmpty(username)) {
                    getStarredRepos(username)
                }
            }

        listView = findViewById(R.id.list_view_repos)
        listView.setAdapter(adapter);
    }

    override fun onResume() {
        uri  = intent.data

        Toast.makeText(this, "Url is: ${uri}",Toast.LENGTH_LONG).show()
        super.onResume()
    }

    override fun onDestroy() {
        if (subscription != null && !subscription!!.isUnsubscribed()) {
            subscription!!.unsubscribe()
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

private fun Button.setOnClickListener() {

}
