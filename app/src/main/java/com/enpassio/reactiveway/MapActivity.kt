package com.enpassio.reactiveway

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.internal.util.NotificationLite.disposable
import javax.xml.datatype.DatatypeConstants.SECONDS
import android.R.attr.delay
import android.support.v4.app.FragmentActivity
import android.util.Log
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import io.reactivex.internal.disposables.DisposableHelper.isDisposed
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import com.enpassio.reactiveway.MapActivity.User
import io.reactivex.internal.util.NotificationLite.disposable






class MapActivity : AppCompatActivity() {

    /**
     *  Source: https://www.androidhive.info/RxJava/map-flatmap-switchmap-concatmap/
     *  additional source: https://medium.com/appunite-edu-collection/rxjava-flatmap-switchmap-and-concatmap-differences-examples-6d1f3ff88ee0
     *
     * Map, FlatMap, SwitchMAp, ConcatMap
     * ---
     * In short, Map, FlatMap, ConcatMap and SwitchMap applies a function or modifies the data
     * emitted by an Observable.
     *
     * Map modifies each item emitted by a source Observable and emits the modified item.
     * -
     * FlatMap, SwitchMap and ConcatMap also applies a function on each emitted item but instead of
     * returning the modified item, it returns the Observable itself which can emit data again.
     * -
     * FlatMap and ConcatMap work is pretty much same. They merges items emitted by multiple
     * Observables and returns a single Observable. The difference between FlatMap and ConcatMap is,
     * the order in which the items are emitted. FlatMap can interleave items while emitting i.e the
     * emitted items order is not maintained.ConcatMap preserves the order of items. But the main
     * disadvantage of ConcatMap is, it has to wait for each Observable to complete its work thus
     * asynchronous is not maintained.
     * -
     * SwitchMap is a bit different from FlatMap and ConcatMap. SwitchMap unsubscribe from previous
     * source Observable whenever new item started emitting, thus always emitting the items from
     * current Observable.
     *
     */

companion object {
        val TAG = MapActivity::class.java.simpleName
}
    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

      //1. Map operator transform each item emitted by an Observable and emits the modified item.
        getUsersObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { user ->
                    // modifying user object by adding email address
                    // turning user name to uppercase
                    // adding address
                    user.email = String.format("%s@rxjava.wtf", user.name)
                    user.name = user.name!!.toUpperCase()
                    user.address = String.format("Address of %s", user.name)
                    user
                }
                .subscribe(object : Observer<User> {
                    override fun onSubscribe(d: Disposable) {
                        disposable = d
                    }

                    override fun onNext(user: User) {
                        Log.d(TAG, "onNext: " + user.name + ", "+ user.gender + ", "+ user.email + ", " + user.address)
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "onError: " + e.message)
                    }

                    override fun onComplete() {
                        Log.d(TAG, "All users emitted!")
                    }
                })

    }


    // assume this method is making a network call and fetching user objects.
    // This returns an Observable that emits User objects with name and gender,
    // but missing email id

    private fun getUsersObservable(): Observable<User> {
        val names = arrayOf("mark", "john", "trump", "obama")

        val users = mutableListOf<User>()
        for (newName in names) {
            val user = User()
            user.name = newName
            user.gender = "male"
            users.add(user)
        }
        return Observable
                .create(ObservableOnSubscribe<User> { emitter ->
                    for (user in users) {
                        if (!emitter.isDisposed) {
                            emitter.onNext(user)
                        }
                    }

                    if (!emitter.isDisposed) {
                        emitter.onComplete()
                    }
                }).subscribeOn(Schedulers.io())
    }

    internal data class User (var name: String? = null,
                             var email: String? = null,
                             var gender: String? = null,
                             var address: String? = null)


    override fun onDestroy() {
        super.onDestroy()

        //Don't send events once the activity is destroyed
        disposable!!.dispose()
    }
}
