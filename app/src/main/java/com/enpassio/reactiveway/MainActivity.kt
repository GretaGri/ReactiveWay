package com.enpassio.reactiveway

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Predicate
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers


class MainActivity : AppCompatActivity() {

    /**
     * from source: https://www.androidhive.info/RxJava/tutorials/
     * https://www.androidhive.info/RxJava/android-getting-started-with-reactive-programming/
     *
     * Basic Observable, Observer, Subscriber, Disposable, Operators example 1, 2, 3, 4
     * Observable emits list of animal names
     * Added Disposable
     * Added operator - filter()
     * Added CompositeDisposable
     */

    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Observable
        val animalsObservable = getAnimalsObservable()

        //Observer with filter letter b
        val animalsObserver = getAnimalsObserver()

        //Observer with filter letter c + all caps
        val animalsObserverAllCaps = getAnimalsAllCapsObserver()


        //Observer subscribing to observable
        // filter() is used to filter out the animal names starting with `b`
        compositeDisposable.add(animalsObservable
                //subscribeOn(Schedulers.io()): This tell the Observable to run the task on a
                // background thread.
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                // observeOn(AndroidSchedulers.mainThread()): This tells the Observer to receive
                // the data on Android UI thread so that you can take any UI related actions.
                //filter() operator filters the data by applying a conditional statement. The data
                // which meets the condition will be emitted and the remaining will be ignored.
                .filter {s: String -> s.toLowerCase().startsWith("b")}
                .subscribeWith(animalsObserver))

        // filter() is used to filter out the animal names starting with `c`
        //map() is used to transform all the characters to UPPER case
        compositeDisposable.add(animalsObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter {s: String -> s.toLowerCase().startsWith("c")}
                .map{s: String-> s.toUpperCase()}
                .subscribeWith(animalsObserverAllCaps))
    }


    private fun getAnimalsObservable(): Observable<String> {
        return Observable.fromArray(
                "Ant", "Ape",
                "Bat", "Bee", "Bear", "Butterfly",
                "Cat", "Crab", "Cod",
                "Dog", "Dove",
                "Fox", "Frog");
    }

    private fun getAnimalsObserver(): DisposableObserver<String> {
        return object : DisposableObserver<String>() {

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

    private fun getAnimalsAllCapsObserver(): DisposableObserver<String> {
        return object : DisposableObserver<String>() {

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
        compositeDisposable.clear();
    }
}
