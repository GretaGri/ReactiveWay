package com.enpassio.reactiveway

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.internal.util.NotificationLite.disposable
import io.reactivex.internal.disposables.DisposableHelper.dispose




class MainActivity : AppCompatActivity() {

    /**
     * from source: https://www.androidhive.info/RxJava/tutorials/
     * https://www.androidhive.info/RxJava/android-getting-started-with-reactive-programming/
     *
     * Basic Observable, Observer, Subscriber, Disposable example 1, 2
     * Observable emits list of animal names
     * Added Disposable
     */

    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Observable
        val animalsObservable = getAnimalsObservable()

        //Observer
        val animalsObserver = getAnimalsObserver()

        //Observer subscribing to observable
        animalsObservable
                //subscribeOn(Schedulers.io()): This tell the Observable to run the task on a
                // background thread.
                .subscribeOn(Schedulers.io())
                // observeOn(AndroidSchedulers.mainThread()): This tells the Observer to receive
                // the data on Android UI thread so that you can take any UI related actions.
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(animalsObserver)
    }

    private fun getAnimalsObservable(): Observable<String> {
        return Observable.just("Ant", "Bee", "Cat", "Dog",
                "Fox")
    }

    private fun getAnimalsObserver(): Observer<String> {
        return object : Observer<String> {

            //onSubscribe(): Method will be called when an Observer subscribes to Observable.
            override fun onSubscribe(d: Disposable) {
                Log.d(TAG, "onSubscribe")
                disposable = d
            }

            //onNext(): This method will be called when Observable starts emitting the data.
            override fun onNext(s: String) {
                Log.d(TAG, "Name: $s")
            }

            //onError(): In case of any error, onError() method will be called.
            override fun onError(e: Throwable) {
                Log.e(TAG, "onError: " + e.message)
            }

            //onComplete(): When an Observable completes the emission of all the items,
            // onCompleted() will be called.
            override fun onComplete() {
                Log.d(TAG, "All items are emitted!")
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()

        //Don't send events once the activity is destroyed
        disposable!!.dispose()
    }
}
