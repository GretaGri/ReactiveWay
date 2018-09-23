package com.enpassio.reactiveway.network

import com.enpassio.reactiveway.network.model.Contact
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Created by Greta Grigutė on 2018-09-23.
 */
interface ApiService {

    @GET("contacts.php")
    fun getContacts(@Query("source") source: String, @Query("search") query: String): Single<List<Contact>>
}