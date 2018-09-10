package com.enpassio.reactiveway

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import hu.akarnokd.rxjava2.math.MathObservable
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable


class MathematicalOperatorsActivity : AppCompatActivity() {
    /**
     * Source: https://www.androidhive.info/RxJava/mathematical-operators-rxjava/
     *
     *  Max, Min, Sum, Average, Count & Reduce operators
     */

    companion object {
        private val TAG = MathematicalOperatorsActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mathematical_operators)

        /**
         * Max() operator
         * ---
         * Max() finds the maximum valued item in the Observable sequence and emits that value.
         *
         * The below example emits the max value of an integer series.
         */
        val numbers: Array<Int> = arrayOf(5, 101, 404, 22, 3, 1024, 65)

        val observable = Observable.fromArray(*numbers)

        MathObservable.max(observable)
                .subscribe(object : Observer<Int> {
                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG, "onSubscribe for max() operator example")
                    }

                    override fun onNext(integer: Int) {
                        Log.d(TAG, "Max value: " + integer)
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "onError: " + e.message)
                    }

                    override fun onComplete() {
                        Log.d(TAG, "onComplete max() integer example")
                    }
                })

        //The below example emits the max value of an float series.

        val floatObservable = Observable.just(10.5f, 14.5f, 11.5f, 5.6f)

        MathObservable.max(floatObservable)
                .subscribe(object : Observer<Float> {
                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG, "onSubscribe for max() operator example")
                    }

                    override fun onNext(aFloat: Float) {
                        Log.d(TAG, "Max of 10.5f, 11.5f, 14.5f: " + aFloat)
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "onError: " + e.message)
                    }

                    override fun onComplete() {
                        Log.d(TAG, "onComplete max() float example")
                    }
                })

        /**
         * Min() operator
         * ---
         *  min() emits the minimum valued item in the Observable data set.
         */

        MathObservable.min(observable)
                .subscribe(object : Observer<Int> {
                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG, "onSubscribe for min() operator example")
                    }

                    override fun onNext(integer: Int) {
                        Log.d(TAG, "Min value: " + integer)
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "onError: " + e.message)
                    }

                    override fun onComplete() {
                        Log.d(TAG, "onComplete min() integer example")
                    }
                })

        /**
         * Sum() operator
         * ---
         *  sum() calculates the sum of all the items emitted by an Observable and emits only the
         *  Sum value.
         *
         *  In the below example, sumInt() is used to calculate the sum of Integers. Likewise,
         *  we have sumFloat(), sumDouble() and sumLong() available to calculate sum of other
         *  primitive datatypes.
         */
        MathObservable.sumInt(observable)
                .subscribe(object : Observer<Int> {
                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG, "onSubscribe for sumInt() (for integer) operator example")
                    }

                    override fun onNext(integer: Int) {
                        Log.d(TAG, "Sum value: " + integer)
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "onError: " + e.message)
                    }

                    override fun onComplete() {
                        Log.d(TAG, "onComplete sumInt() integer example")
                    }
                })

        /**
         * Average() operator
         * ---
         *  averageDouble() calculates the sum of all the items emitted by an Observable and emits only
         *  the Average value.
         *
         *  The below example calculates the average value of double using averageDouble() method.
         *  To calculate average of float averageFloat() is available.
         */
        MathObservable.averageDouble(observable)
                .subscribe(object : Observer<Double> {
                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG, "onSubscribe for sumInt() (for integer) operator example")
                    }

                    override fun onNext(double: Double) {
                        Log.d(TAG, "Sum value: " + double.toInt())
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "onError: " + e.message)
                    }

                    override fun onComplete() {
                        Log.d(TAG, "onComplete sumInt() integer example")
                    }
                })
    }
}
