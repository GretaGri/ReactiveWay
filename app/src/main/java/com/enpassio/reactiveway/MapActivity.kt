package com.enpassio.reactiveway

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit


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

        /** 1. Map() example
         * ---
         * Map operator transforms each item emitted by an Observable and emits the modified item.
         */

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
                        Log.d(TAG, "onNext map() example: " + user.name + ", " + user.gender + ", " + user.email + ", " + user.address)
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "onError: " + e.message)
                    }

                    override fun onComplete() {
                        Log.d(TAG, "All users emitted!")
                    }
                })

        /**
         *  2. FlatMap () example
         * ---
         *  A scenario where you have a network call to fetch Users with name and gender. Then you
         *  have another network that gives you address of each user. Now the requirement is to
         *  create an Observable that emits Users with name, gender and address properties.
         *  To achieve this, you need to get the users first, then make separate network call
         *  for each user to fetch his address.
         *
         *  getUsersObservable() : assume it makes a network call and returns an Observable that
         *  emits User (name and gender) objects.
         *
         *  getAddressObservable() : assume it makes another network call just to fetch user
         *  address. This also returns an Observable that emits User by adding address node to
         *  existing name and gender.
         *
         *  flatMap() operator makes getAddressObservable() call each time a User is emitted and
         *  returns an Observable that emits User including the address filed.
         *
         *  Finally flatMap() returns an Observable by merging two Observables together.
         *
         *  Thread.sleep(sleepTime); added here to simulate network latency.
         */
        getUsersObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(Function<User, Observable<User>> { user ->
                    // getting each user address by making another network call
                    getAddressObservable(user)
                })
                .subscribe(object : Observer<User> {
                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG, "onSubscribe")
                        disposable = d
                    }

                    override fun onNext(user: User) {
                        Log.d(TAG, "onNext flatMap() example: " + user.name + ", " + user.gender + ", " + user.address)
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "onError: " + e.message)
                    }

                    override fun onComplete() {
                        Log.d(TAG, "All users emitted!")
                    }
                })

        /**
         * 3. ConcatMap() example
         * ---
         * ConcatMap() maintains the order of items and waits for the current Observable to complete its job before emitting the next one.
         * ConcatMap is more suitable when you want to maintain the order of execution.
         */

        getUsersObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .concatMap { user ->
                    // getting each user address by making another network call
                    getAddressObservable(user)
                }
                .subscribe(object : Observer<User> {
                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG, "onSubscribe")
                        disposable = d
                    }

                    override fun onNext(user: User) {
                        Log.d(TAG, "onNext concatMap() example: " + user.name + ", " + user.gender + ", " + user.address)
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "onError: " + e.message)
                    }

                    override fun onComplete() {
                        Log.d(TAG, "All users emitted!")
                    }
                })

        //4. SwitchMap() example:
        //---
        //SwitchMap always return the latest Observable and emits the items from it.
        //Example: https://www.androidhive.info/RxJava/android-rxjava-instant-search-local-remote-databases/

        val integerObservable = Observable.fromArray(1, 2, 3, 4, 5, 6)

        // it always emits 6 as it un-subscribes the before observer
        integerObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .switchMap { int -> Observable.just(int).delay(1, TimeUnit.SECONDS) }.subscribe(object : Observer<Int> {
                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG, "onSubscribe")
                        disposable = d
                    }

                    override fun onNext(integer: Int) {
                        Log.d(TAG, "onNext switchMap() example: " + integer)
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "onError: " + e.message)
                    }

                    override fun onComplete() {
                        Log.d(TAG, "All users emitted!")
                    }
                })
    }

    /**
     * Assume this as a network call
     * returns Users with address filed added
     */
    private fun getAddressObservable(user: User): Observable<User> {

        val addresses = arrayOf("1600 Amphitheatre Parkway, Mountain View, CA 94043", "2300 Traverwood Dr. Ann Arbor, MI 48105", "500 W 2nd St Suite 2900 Austin, TX 78701", "355 Main Street Cambridge, MA 02142")

        return Observable
                .create(ObservableOnSubscribe<User> { emitter ->
                    val address = addresses[Random().nextInt(2) + 0]
                    if (!emitter.isDisposed) {
                        user.address = address


                        // Generate network latency of random duration
                        val sleepTime = Random().nextInt(1000) + 500

                        Thread.sleep(sleepTime.toLong())
                        emitter.onNext(user)
                        emitter.onComplete()
                    }
                }).subscribeOn(Schedulers.io())
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

    internal data class User(var name: String? = null,
                             var email: String? = null,
                             var gender: String? = null,
                             var address: String? = null)


    override fun onDestroy() {
        super.onDestroy()

        //Don't send events once the activity is destroyed
        disposable!!.dispose()
    }
}
