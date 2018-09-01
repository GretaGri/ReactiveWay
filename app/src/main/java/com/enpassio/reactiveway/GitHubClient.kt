package com.enpassio.reactiveway

import com.enpassio.reactiveway.Model.GitHubRepo
import rx.Observable

/**
 * Created by Greta Grigutė on 2018-08-30.
 */

class GitHubClient private constructor() {
    private val gitHubService: GitHubService

    init {
        gitHubService = ServiceGenerator.createService(GitHubService::class.java)

        //this code replaced by ServiceGenerator
      //  val retrofit = Retrofit.Builder().baseUrl(GITHUB_BASE_URL)
       //         .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
       //         .addConverterFactory(GsonConverterFactory.create(gson))
        //        .build()
      //  gitHubService = retrofit.create(GitHubService::class.java)
    }

    fun getStarredRepos(userName: String): Observable<List<GitHubRepo>> {
        return gitHubService.getStarredRepositories(userName)
    }

    companion object {

      //  private val GITHUB_BASE_URL = "https://api.github.com/"

        private var instance: GitHubClient? = null

        fun getInstance(): GitHubClient {
            if (instance == null) {
                instance = GitHubClient()
            }
            return instance!!
        }
    }
}