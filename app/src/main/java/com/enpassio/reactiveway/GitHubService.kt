package com.enpassio.reactiveway

import com.enpassio.reactiveway.Model.AccessToken
import com.enpassio.reactiveway.Model.GitHubRepo
import retrofit2.Call
import retrofit2.http.*
import rx.Observable

/**
 * Created by Greta Grigutė on 2018-08-30.
 */
interface GitHubService {
    @Headers ("Accept: application/json")
    @POST("login/oauth/access_token")
    @FormUrlEncoded
    fun getAccessToken(@Field ("client_id") clientId: String,
                       @Field ("client_secret") clientSecret: String,
                       @Field ("code") code: String
                       ): Call<AccessToken>

    @GET("users/{user}/starred")
    fun getStarredRepositories(@Path("user") userName: String): Observable<List<GitHubRepo>>
}