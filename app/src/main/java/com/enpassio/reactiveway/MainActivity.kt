package com.enpassio.reactiveway

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import com.enpassio.reactiveway.Model.AccessToken
import com.enpassio.reactiveway.Model.GitHubRepo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
    }

    override fun onResume() {
        super.onResume()
        val uri: Uri? = intent.data

        Toast.makeText(this, "Url is: ${uri}",Toast.LENGTH_LONG).show()

        if (uri == null) {

            buttonSearch.visibility = View.VISIBLE
            editTextUsername.visibility = View.VISIBLE

            buttonSearch.setOnClickListener { view ->
                val username = editTextUsername.text.toString()
                if (!TextUtils.isEmpty(username)) {
                    getStarredRepos(username)
                }
            }

            listView = findViewById(R.id.list_view_repos)
            listView.setAdapter(adapter);

        } else if (uri != null && uri.toString().startsWith(redirecturi)) {

            val code: String? = uri.getQueryParameter("code")

            val builder = Retrofit.Builder()
                    .baseUrl("https://github.com/")
                    .addConverterFactory(GsonConverterFactory.create(ServiceGenerator.gson))

            val retrofit = builder.build()

            val client: GitHubService = retrofit.create(GitHubService::class.java)
            val accessTokenCall: Call<AccessToken> = client.getAccessToken(
                    clientId,
                    clientSecret,
                    code!!
            )

            accessTokenCall.enqueue(object : Callback<AccessToken> {
                override fun onResponse(call: Call<AccessToken>, response: Response<AccessToken>?) {
                    val resource = response?.body()
                    Toast.makeText(this@MainActivity, "Yay, got the respose: ${resource}", Toast.LENGTH_LONG).show()
                }

                override fun onFailure(call: Call<AccessToken>, t: Throwable?) {
                    Toast.makeText(this@MainActivity, "Fail", Toast.LENGTH_LONG).show()
                }
            })
        }
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
