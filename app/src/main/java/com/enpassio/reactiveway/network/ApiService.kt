package com.enpassio.reactiveway.network

/**
 * Created by Greta Grigutė on 2018-09-13.
 *
 * This class holds the interface methods of every endpoint by defining the endpoint,
 * request and response Observable.
 */
import com.enpassio.reactiveway.network.model.model.Note
import com.enpassio.reactiveway.network.model.model.User
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Created by ravi on 20/02/18.
 */

interface ApiService {
    // Register new user
    @FormUrlEncoded
    @POST("notes/user/register")
    fun register(@Field("device_id") deviceId: String): Single<User>

    // Create note
    @FormUrlEncoded
    @POST("notes/new")
    fun createNote(@Field("note") note: String): Single<Note>

    // Fetch all notes
    @GET("notes/all")
    fun fetchAllNotes(): Single<List<Note>>

    // Update single note
    @FormUrlEncoded
    @PUT("notes/{id}")
    fun updateNote(@Path("id") noteId: Int, @Field("note") note: String): Completable

    // Delete note
    @DELETE("notes/{id}")
    fun deleteNote(@Path("id") noteId: Int): Completable
}