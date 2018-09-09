package com.enpassio.reactiveway

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class ConcatMergeActivity : AppCompatActivity() {

    companion object {
        val TAG = ConcatMergeActivity::class.java.simpleName
    }

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_concat_merge)

        /**
         * 1. Concat()
         * ---
         * Concat operator combines output of two or more Observables into a single Observable.
         * Concat operator always maintains the sequential execution without interleaving the
         * emissions. So the first Observables completes its emission before the second starts
         * and so forth if there are more observables. The sequential order is maintained while
         * emitting the items.
         *
         * Let’s say we have two separate Observables that emits Male and Female users. When Concat
         * operator is used, the both Observables will be combined and act as single Observable.
         */
        Observable
                .concat(getMaleObservable(), getFemaleObservable())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<User> {
                    override fun onSubscribe(d: Disposable) {
                        disposable = d
                    }

                    override fun onNext(user: User) {
                        Log.d(TAG, "onNext concat() example:" + user.name + ", " + user.gender)
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "onError: " + e.message)
                    }

                    override fun onComplete() {
                        Log.d(TAG, "All users emitted!")
                    }
                })
    }

    private fun getFemaleObservable(): Observable<User> {
        val names = arrayOf("Lucy", "Scarlett", "April")

        val users = mutableListOf<User>()
        for (name in names) {
            val user = User()
            user.name = name
            user.gender = "female"

            users.add(user)
        }
        return Observable
                .create(ObservableOnSubscribe<User> { emitter ->
                    for (user in users) {
                        if (!emitter.isDisposed) {
                            Thread.sleep(1000)
                            emitter.onNext(user)
                        }
                    }

                    if (!emitter.isDisposed) {
                        emitter.onComplete()
                    }
                }).subscribeOn(Schedulers.io())
    }

    private fun getMaleObservable(): Observable<User> {
        val names = arrayOf("Mark", "John", "Trump", "Obama")

        val users = mutableListOf<User>()

        for (name in names) {
            val user = User()
            user.name = name
            user.gender = "male"

            users.add(user)
        }
        return Observable
                .create(ObservableOnSubscribe<User> { emitter ->
                    for (user in users) {
                        if (!emitter.isDisposed) {
                            Thread.sleep(500)
                            emitter.onNext(user)
                        }
                    }

                    if (!emitter.isDisposed) {
                        emitter.onComplete()
                    }
                }).subscribeOn(Schedulers.io())
    }

    internal data class User(var name: String? = null,
                             var gender: String? = null)

    override fun onDestroy() {
        super.onDestroy()

        //Don't send events once the activity is destroyed
        disposable!!.dispose()
    }
}
