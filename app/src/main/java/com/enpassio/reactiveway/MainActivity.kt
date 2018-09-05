package com.enpassio.reactiveway

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Predicate
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.internal.disposables.DisposableHelper.isDisposed
import android.provider.ContactsContract.CommonDataKinds.Note
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe




class MainActivity : AppCompatActivity() {

    /**
     * from source: https://www.androidhive.info/RxJava/tutorials/
     * https://www.androidhive.info/RxJava/android-getting-started-with-reactive-programming/
     *
     * Basic Observable, Observer, Subscriber, Disposable, Operators, CompositeDisposable and
     * DisposableObservercustom data type - example 1, 2, 3, 4, 5
     *
     * The observable emits custom data type (Note) instead of primitive data types
     * .map() operator is used to turn the note into all uppercase letters
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
                .map{note:Note -> Note(note.id, note.note.toUpperCase())}
                .subscribeWith(getNotesObserver()))
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
