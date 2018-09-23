package com.enpassio.reactiveway.network.model

import com.google.gson.annotations.SerializedName



/**
 * Created by Greta Grigutė on 2018-09-23.
 */
class Contact {
    var name: String? = null
        internal set

    @SerializedName("image")
    var profileImage: String? = null
        internal set

    var phone: String? = null
        internal set
    var email: String? = null
        internal set

    /**
     * Checking contact equality against email
     */
    override fun equals(obj: Any?): Boolean {
        return if (obj != null && obj is Contact) {
            obj.email!!.equals(email!!, ignoreCase = true)
        } else false
    }
}