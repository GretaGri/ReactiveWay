package com.enpassio.reactiveway

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.*
import io.reactivex.Observable
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

        /**
         * Single and SingleObserver
         * -
         * Single always emits only one value or throws an error. The same job can be done using
         * Observable too with a single emission but Single always makes sure there is an emission.
         *
         * A use case of Single would be making a network call to get response as the response will
         * be fetched at once.
         *
         * Another example could be fetching a Note from database by its Id. Also we need to make
         * sure that the Note is present in database as Single should always emit a value.
         *
         * Notice here, the SingleObserver doesn’t have onNext() to emit the data, instead the
         * data will be received in onSuccess(Note note) method.
         */
        val noteObservableSingle = getNoteObservableSingle()

        val singleObserver = getSingleObserver()

        noteObservableSingle
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(singleObserver)

        /**
         * Maybe and MaybeObserver
         * -
         * Maybe observable may or may not emits a value. This observable can be used when you are
         * expecting an item to be emitted optionally.
         *
         * An example of usage - getting a note from db using ID - there is possibility of not
         * finding the note by ID in the db in this situation, Maybe can be used.
         */
        val noteObservableMaybe = getNoteObservableMaybe()

        val maybeObserver = getMaybeObserver()

        noteObservableMaybe.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(maybeObserver)
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

    private fun getSingleObserver(): SingleObserver<Note> {
        return object : SingleObserver<Note> {
            override fun onSubscribe(d: Disposable) {
                Log.d(TAG, "onSubscribe")
                disposable = d
            }
            // the Single Observer doesn’t have onNext() to emit the data, instead the data will be
            // received in onSuccess(Note note) method.

            override fun onSuccess(note: Note) {
                Log.d(TAG, "onSuccess Single observable example: " + note.note)
            }

            override fun onError(e: Throwable) {
                Log.d(TAG, "onError: " + e.message)
            }
        }
    }

    private fun getMaybeObserver(): MaybeObserver<Note> {
        return object : MaybeObserver<Note> {
            override fun onSubscribe(d: Disposable) {
                disposable = d
            }

            override fun onSuccess(note: Note) {
                Log.d(TAG, "onSuccess Maybe example: " + note.note)
            }

            override fun onError(e: Throwable) {
                Log.e(TAG, "onError: " + e.message)
            }

            override fun onComplete() {
                Log.d(TAG, "onComplete Maybe example")
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

    private fun getNoteObservableSingle(): Single<Note> {
        return Single.create(SingleOnSubscribe<Note> { emitter ->
            val note = Note(1, "Buy milk!")
            emitter.onSuccess(note)
        })
    }

    /**
     * Emits optional data (0 or 1 emission)
     * But for now it emits 1 Note always
     */
    private fun getNoteObservableMaybe(): Maybe<Note> {
        return Maybe.create { emitter ->
            val note = Note(1, "Call brother!")
            if (!emitter.isDisposed) {
                emitter.onSuccess(note)
            }
        }
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
