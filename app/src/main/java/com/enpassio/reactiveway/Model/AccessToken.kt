package com.enpassio.reactiveway.Model

import com.google.gson.annotations.SerializedName

/**
 * Created by Greta Grigutė on 2018-09-01.
 */
data class AccessToken (
        @SerializedName("access_token")
        private val accessToken: String,

        @SerializedName("token_type")
        private val tokenType: String
)