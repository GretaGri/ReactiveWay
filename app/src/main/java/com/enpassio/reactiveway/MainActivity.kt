package com.enpassio.reactiveway

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.enpassio.reactiveway.R.id.always
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Predicate
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers


class MainActivity : AppCompatActivity() {

    /**
     * from sources: https://www.androidhive.info/RxJava/tutorials/
     * https://www.androidhive.info/RxJava/android-getting-started-with-reactive-programming/
     *
     * Basic Observable, Observer, Subscriber, Disposable, Operators, CompositeDisposable and
     * DisposableObserver, custom data type - example 1, 2, 3, 4, 5
     *
     * The observable emits custom data type (Note) instead of primitive data types
     * .map() operator is used to turn the note into all uppercase letters
     *
     * ----------
     * https://www.androidhive.info/RxJava/rxjava-operators-introduction/
     *
     * Example of range() operator usage
     * Example of filter() and map() operators used together
     *
     * ----------
     * https://www.androidhive.info/RxJava/rxjava-operators-just-range-from-repeat/#just
     *
     * Example of just() operator
     */

    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // add to Composite observable
        // .map() operator is used to turn the note into all uppercase letters
        compositeDisposable.add(getNotesObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                // Making the note to all uppercase
                .map { note: Note -> Note(note.id, note.note.toUpperCase()) }
                .subscribeWith(getNotesObserver()))

        //Example for operators - writing values manually:
        val numbers = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)

        Observable.fromArray(*numbers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getIntObserver())

        //https://www.androidhive.info/RxJava/rxjava-operators-introduction/
        // Instead of writing the array of numbers manually, you can do the same using range(1, 20) operator as below.

        Observable.range(1, 20) //range() operator generates the numbers from 1 to 20
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getIntObserver())

        // Emitting numbers from 1 to 20. But in this case we want to filter out the even numbers
        // along with we want to append a string at the end of each number.
        Observable.range(1, 20) //range() operator generates the numbers from 1 to 20
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                //filter(): Filters the numbers by applying a condition onto each number
                .filter(object : Predicate<Int> {
                    @Throws(Exception::class)
                    override fun test(integer: Int): Boolean {
                        return integer % 2 == 0
                    }
                })
                //map(): Map transform the data from Integer to String by appending the string at
                // the end
                .map { integer: Int -> "${integer} is even number" }
                .subscribe(getStringObserver())

        //1. https://www.androidhive.info/RxJava/rxjava-operators-just-range-from-repeat/#just
        //Just() operator takes a list of arguments and converts the items into Observable items.
        //It takes arguments between one to ten. Example: an Observable is created using just() from
        // a series of integers. The limitation of just() is, you can’t pass more than 10 arguments.

        Observable.just(1, 2, 3, 4, 5, 6, 7, 8,
                9, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Int> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(integer: Int) {
                        Log.d(TAG, "onNext: " + integer)
                    }

                    override fun onError(e: Throwable) {
                    }

                    override fun onComplete() {
                    }
                })

        // the below example creates an Observable from an array. The array is emitted as single
        // item instead of individual numbers. The Observer emits the array onNext(Integer[]
        // integers) so you will always have 1 emission irrespective of length of the array
        Observable.just(numbers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Array<Int>> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(integers: Array<Int>) {
                        Log.d(TAG, "onNext: Array size is " + integers.size)
                        // you might have to loop through the array
                    }

                    override fun onError(e: Throwable) {
                    }

                    override fun onComplete() {
                    }
                })
    }

    private fun getIntObserver(): DisposableObserver<Int> {
        return object : DisposableObserver<Int>() {

            override fun onNext(t: Int) {
                Log.d(TAG, "Number: " + t)
            }

            override fun onError(e: Throwable) {
                Log.e(TAG, "onError: " + e.message)
            }

            override fun onComplete() {
                Log.d(TAG, "All numbers emitted!")
            }
        }
    }

    private fun getStringObserver(): Observer<String> {
        return object : Observer<String> {
            override fun onSubscribe(d: Disposable) {

            }

            override fun onNext(text: String) {
                Log.d(TAG, "Number: " + text)
            }

            override fun onError(e: Throwable) {
                Log.e(TAG, "onError: " + e.message)
            }

            override fun onComplete() {
                Log.d(TAG, "All numbers emitted!")
            }
        }
    }

    private fun getNotesObserver(): DisposableObserver<Note> {
        return object : DisposableObserver<Note>() {
            //onNext(): This method will be called when Observable starts emitting the data.
            override fun onNext(note: Note) {
                Log.d(TAG, "Note: " + note.note)
            }

            //onError(): In case of any error, onError() method will be called.
            override fun onError(e: Throwable) {
                Log.e(TAG, "onError: " + e.message)
            }

            //onComplete(): When an Observable completes the emission of all the items,
            // onCompleted() will be called.
            override fun onComplete() {
                Log.d(TAG, "All notes are emitted!")
            }
        }
    }

    private fun getNotesObservable(): Observable<Note> {
        val notes = prepareNotes()

        return Observable.create { emitter ->
            for (note in notes) {
                if (!emitter.isDisposed) {
                    emitter.onNext(note)
                }
            }

            if (!emitter.isDisposed) {
                emitter.onComplete()
            }
        }
    }

    private fun prepareNotes(): List<Note> {
        val notes = mutableListOf<Note>()
        notes.add(Note(1, "buy tooth paste!"))
        notes.add(Note(2, "call brother!"))
        notes.add(Note(3, "watch narcos tonight!"))
        notes.add(Note(4, "pay power bill!"))

        return notes
    }

    internal data class Note(var id: Int, var note: String)

    override fun onDestroy() {
        super.onDestroy()

        //Don't send events once the activity is destroyed
        compositeDisposable.clear();
    }
}
