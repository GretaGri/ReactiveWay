package com.enpassio.reactiveway.network.model.model


/**
 * Created by Greta Grigutė on 2018-09-13.
 *
 * BaseResponse – As every response will have a error node, we define the error node in
 * BaseResponse class and extend this class in other models.
 */
open class BaseResponse {
    var error: String? = null
        internal set


}