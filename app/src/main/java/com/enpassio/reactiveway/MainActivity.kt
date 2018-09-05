package com.enpassio.reactiveway

import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Note
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Predicate
import io.reactivex.internal.operators.flowable.FlowableBlockingSubscribe.subscribe
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
                .map {integer: Int -> "${integer} is even number" }
                .subscribe(getStringObserver())
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
