package com.enpassio.reactiveway.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import com.enpassio.reactiveway.network.model.Price
import com.enpassio.reactiveway.network.model.Ticket


/**
 * Created by Greta Grigutė on 2018-09-23.
 */
interface ApiService {

    @GET("airline-tickets.php")
    fun searchTickets(@Query("from") from: String, @Query("to") to: String): Single<List<Ticket>>

    @GET("airline-tickets-price.php")
    fun getPrice(@Query("flight_number") flightNumber: String, @Query("from") from: String, @Query("to") to: String): Single<Price>
}