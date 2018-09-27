package com.enpassio.reactiveway.network.model

/**
 * Created by Greta Grigutė on 2018-09-25.
 */
import com.google.gson.annotations.SerializedName

data class Price (
    var price: Float = 0.toFloat(),
    var seats: String? = null,
    var currency: String? = null,

    @SerializedName("flight_number")
    var flightNumber: String? = null,

    var from: String? = null,
    var to: String? = null
)