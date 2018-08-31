package com.enpassio.reactiveway

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import android.text.TextUtils
import okhttp3.Credentials


/**
 * Created by Greta Grigutė on 2018-08-30.
 */
object ServiceGenerator {

    private val BASE_URL = "https://api.github.com/"

    val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()

    private var builder = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))

    private var retrofit = builder.build()

    private val httpClient = OkHttpClient.Builder()

    fun <S> createService(
            serviceClass: Class<S>): S {
        builder = builder.client(httpClient.build())
        retrofit = builder.build()
        return retrofit.create(serviceClass)
    }

    fun <S> createService(
            serviceClass: Class<S>, clientId: String?, clientSecret: String?): S {
        if (!TextUtils.isEmpty(clientId) && !TextUtils.isEmpty(clientSecret)) {
            val authToken = Credentials.basic(clientId, clientSecret)
            return createService(serviceClass, authToken)
        }

        return createService(serviceClass, null, null)
    }

    fun <S> createService(
            serviceClass: Class<S>, authToken: String): S {
        if (!TextUtils.isEmpty(authToken)) {
            val interceptor = AuthenticationInterceptor(authToken)

            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor)

                builder.client(httpClient.build())
                retrofit = builder.build()
            }
        }

        return retrofit.create(serviceClass)
    }
}