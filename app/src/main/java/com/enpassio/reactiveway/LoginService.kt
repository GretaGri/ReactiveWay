package com.enpassio.reactiveway

import retrofit2.Call
import retrofit2.http.POST



/**
 * Created by Greta Grigutė on 2018-08-31.
 */
interface LoginService {
    @POST("/login")
    fun basicLogin(): Call<User>
}