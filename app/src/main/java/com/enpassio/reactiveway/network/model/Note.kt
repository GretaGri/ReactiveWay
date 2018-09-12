package com.enpassio.reactiveway.network.model.model

import com.enpassio.reactiveway.network.model.model.BaseResponse

/**
 * Created by Greta Grigutė on 2018-09-13.
 *
 * Note – Defines the note object with id, note and timestamp fields.
 */
class Note : BaseResponse() {
    var id: Int = 0
        internal set
    var note: String? = null
    var timestamp: String? = null
        internal set
}