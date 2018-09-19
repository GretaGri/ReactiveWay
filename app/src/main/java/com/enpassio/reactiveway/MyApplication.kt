package com.enpassio.reactiveway

import android.text.TextUtils
import com.android.volley.toolbox.Volley
import com.android.volley.RequestQueue
import android.app.Application
import com.android.volley.Request


/**
 * Created by Greta Grigutė on 2018-09-19.
 */
class MyApplication : Application() {

    private var mRequestQueue: RequestQueue? = null

    val requestQueue: RequestQueue?
        get() {
            if (mRequestQueue == null) {
                mRequestQueue = Volley.newRequestQueue(applicationContext)
            }

            return mRequestQueue
        }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun <T> addToRequestQueue(req: Request<T>, tag: String) {
        // set the default tag if tag is empty
        req.setTag(if (TextUtils.isEmpty(tag)) TAG else tag)
        requestQueue!!.add(req)
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        req.setTag(TAG)
        requestQueue!!.add(req)
    }

    fun cancelPendingRequests(tag: Any) {
        if (mRequestQueue != null) {
            mRequestQueue!!.cancelAll(tag)
        }
    }

    companion object {

        val TAG = MyApplication::class.java
                .simpleName

        @get:Synchronized
        var instance: MyApplication? = null
            private set
    }
}