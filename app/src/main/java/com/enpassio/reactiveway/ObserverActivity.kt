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
import java.util.*


class ObserverActivity : AppCompatActivity() {

    companion object {
        private val TAG = ObserverActivity::class.java.simpleName
    }

    private var disposable: Disposable? = null

    /**
     * From source:
     * https://www.androidhive.info/RxJava/rxjava-understanding-observables/
     *
     * Simple Observable emitting multiple Notes
     * -
     * Observable : Observer
     * Observable can emit one or more items.
     * In the below example, we have an Observable that emits Note items one by one.
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_observer)

        val notesObservable = getNotesObservable()

        val notesObserver = getNotesObserver()

        notesObservable.observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribeWith(notesObserver)
    }

    private fun getNotesObserver(): Observer<Note> {
        return object : Observer<Note> {

            override fun onSubscribe(d: Disposable) {
                Log.d(TAG, "onSubscribe")
                disposable = d
            }

            override fun onNext(note: Note) {
                Log.d(TAG, "onNext: " + note.note)
            }

            override fun onError(e: Throwable) {
                Log.e(TAG, "onError: " + e.message)
            }

            override fun onComplete() {
                Log.d(TAG, "onComplete Observer example")
            }
        }

    }

    private fun getNotesObservable(): Observable<Note> {
        val notes = prepareNotes()

        return Observable.create(ObservableOnSubscribe<Note> { emitter ->
            for (note in notes) {
                if (!emitter.isDisposed) {
                    emitter.onNext(note)
                }
            }

            // all notes are emitted
            if (!emitter.isDisposed) {
                emitter.onComplete()
            }
        })
    }

    private fun prepareNotes(): List<Note> {
        val notes = ArrayList<Note>()
        notes.add(Note(1, "Buy tooth paste!"))
        notes.add(Note(2, "Call brother!"))
        notes.add(Note(3, "Watch Narcos tonight!"))
        notes.add(Note(4, "Pay power bill!"))
        return notes
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable!!.dispose()
    }

    internal data class Note(val id: Int, val note: String)

}
