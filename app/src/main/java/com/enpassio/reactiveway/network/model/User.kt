package com.enpassio.reactiveway.network.model.model

/**
 * Created by Greta Grigutė on 2018-09-13.
 *
 * User – User response once the device is registered. For now this model will have apiKey only.
 */
import com.enpassio.reactiveway.network.model.model.BaseResponse
import com.google.gson.annotations.SerializedName

class User : BaseResponse() {

    @SerializedName("api_key")
    var apiKey: String? = null
        internal set
}