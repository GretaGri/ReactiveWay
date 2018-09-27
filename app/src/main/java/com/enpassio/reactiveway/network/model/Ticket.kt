package com.enpassio.reactiveway.network.model

/**
 * Created by Greta Grigutė on 2018-09-25.
 */
import com.google.gson.annotations.SerializedName

data class Ticket (

    var from: String? = null,
    var to: String? = null,

    @SerializedName("flight_number")
    var flightNumber: String? = null,

    var departure: String? = null,
    var arrival: String? = null,
    var duration: String? = null,
    var instructions: String? = null,

    @SerializedName("stops")
    var numberOfStops: Int = 0,

    var airline: Airline? = null,

    var price: Price? = null){

    override fun equals(obj: Any?): Boolean {
        if (obj === this) {
            return true
        }

        return if (obj !is Ticket) {
            false
        } else flightNumber!!.equals(obj.flightNumber!!, ignoreCase = true)

    }

    override fun hashCode(): Int {
        var hash = 3
        hash = 53 * hash + if (this.flightNumber != null) this.flightNumber!!.hashCode() else 0
        return hash
    }
}