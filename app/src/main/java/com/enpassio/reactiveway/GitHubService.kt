package com.enpassio.reactiveway

import retrofit2.http.GET
import retrofit2.http.Path
import rx.Observable

/**
 * Created by Greta Grigutė on 2018-08-30.
 */
interface GitHubService {
    @GET("users/{user}/starred")
    fun getStarredRepositories(@Path("user") userName: String): Observable<List<GitHubRepo>>
}