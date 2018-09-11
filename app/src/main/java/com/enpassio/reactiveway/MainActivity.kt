package com.enpassio.reactiveway

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Predicate
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*


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
     *
     * https://www.androidhive.info/RxJava/rxjava-operators-just-range-from-repeat/#from
     * Example of fromArray() operator
     *
     * https://www.androidhive.info/RxJava/rxjava-operators-just-range-from-repeat/#range
     * Example of range() operator
     *
     * https://www.androidhive.info/RxJava/rxjava-operators-just-range-from-repeat/#repeat
     * Example of repeat() operator
     *
     * https://www.androidhive.info/RxJava/rxjava-operators-buffer-debounce/#buffer
     * Example of buffer() operator
     *
     * https://www.androidhive.info/RxJava/rxjava-operators-repeat-skip-take-takeuntil/#filter
     * Example of filter() operator
     *
     * https://www.androidhive.info/RxJava/rxjava-operators-repeat-skip-take-takeuntil/#skip
     * Example of skip() operator
     *
     * https://www.androidhive.info/RxJava/rxjava-operators-repeat-skip-take-takeuntil/#skip-last
     * Example of skipLast() operator
     *
     * https://www.androidhive.info/RxJava/rxjava-operators-repeat-skip-take-takeuntil/#take
     * Example of take() operator
     *
     * https://www.androidhive.info/RxJava/rxjava-operators-repeat-skip-take-takeuntil/#take-last
     * Example of takeLast() operator
     *
     * https://www.androidhive.info/RxJava/rxjava-operators-repeat-skip-take-takeuntil/#distinct
     * Example of distinct() operator
     */

    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        button_buffer.setOnClickListener {
            val i = Intent(this, BufferDebounceActivity::class.java)
            startActivity(i)
        }

        button_observer_example.setOnClickListener {
            val i = Intent(this, ObserverActivity::class.java)
            startActivity(i)
        }

        button_map_example.setOnClickListener {
            val i = Intent(this, MapActivity::class.java)
            startActivity(i)
        }

        button_concat_merge.setOnClickListener {
            val i = Intent(this, ConcatMergeActivity::class.java)
            startActivity(i)
        }

        button_mathematical_op_example.setOnClickListener {
            val i = Intent(this, MathematicalOperatorsActivity::class.java)
            startActivity(i)
        }


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
        // Instead of writing the array of numbers manually, you can do the same using range(1, 20)
        // operator as below. Range() creates an Observable from a sequence of generated integers.
        // The function generates sequence of integers by taking starting number and length.

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
                        Log.d(TAG, "onSubscribe for just operator from integers example")
                    }

                    override fun onNext(integer: Int) {
                        Log.d(TAG, "onNext: " + integer)
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "onError: " + e.message)
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
                        Log.d(TAG, "onSubscribe for just operator from array example")
                    }

                    override fun onNext(integers: Array<Int>) {
                        Log.d(TAG, "onNext: Array size is " + integers.size)
                        // you might have to loop through the array
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "onError: " + e.message)
                    }

                    override fun onComplete() {
                    }
                })
        //fromArray() operator as in RxJava2 we have don’t have from().
        Observable.fromArray(numbers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Array<Int>> {
                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG, "onSubscribe for fromArray operator example")
                    }

                    override fun onNext(array: Array<Int>) {
                        Log.d(TAG, "onNext: " + array.size)
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "onError: " + e.message)
                    }

                    override fun onComplete() {
                    }
                })

        // Repeat() creates an Observable that emits an item or series of items repeatedly. You can
        // also pass an argument to limit the number of repetitions.
        // The below example repeats the emission of integers from 1-4 three times using repeat(3).
        Observable
                .range(1, 4)
                .repeat(3)
                .subscribe(object : Observer<Int> {
                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG, "onSubscribe for repeat operator example")
                    }

                    override fun onNext(integer: Int) {
                        Log.d(TAG, "onNext repeat operator example: " + integer)
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "onError: " + e.message)
                    }

                    override fun onComplete() {
                        Log.d(TAG, "Completed repeat operator example")
                    }
                })

        //Buffer gathers items emitted by an Observable into batches and emit the batch instead of
        // emitting one item at a time.
        // Below, we have an Observable that emits integers from 1-9. When buffer(3) is used,
        // it emits 3 integers at a time.
        val integerObservable = Observable.just(1, 2, 3, 4,
                5, 6, 7, 8, 9)

        integerObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .buffer(3)
                .subscribe(object : Observer<List<Int>> {
                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG, "onSubscribe for buffer operator example")
                    }

                    override fun onNext(integers: List<Int>) {
                        Log.d(TAG, "onNext")
                        for (integer in integers) {
                            Log.d(TAG, "Item: $integer")
                        }
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "onError: " + e.message)
                    }

                    override fun onComplete() {
                        Log.d(TAG, "All items emitted!")
                    }
                })
        /**
         * Source: https://www.androidhive.info/RxJava/rxjava-operators-repeat-skip-take-takeuntil
         * Filter() operator example
         * ---
         * filter() example with a custom datatype. Below Observable emits list of Users and we want
         * to filter out the users by gender female.
         *
         * getUsersObservable() creates an Observable that emits list of users combining both male
         * and female users.
         *
         * In the filter() method, each user is checked against female gender by
         * user.gender.equals(“female”, ignoreCase = true) condition.
         */
        val userObservable = getUsersObservable()

        userObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(Predicate<User> { user -> user.gender.equals("female", ignoreCase = true) })
                .subscribeWith(object : DisposableObserver<User>() {
                    override fun onNext(user: User) {
                        Log.e(TAG, user.name + ", " + user.gender)
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "onError: " + e.message)

                    }

                    override fun onComplete() {

                    }
                })

        /**
         * Skip() operator
         * ---
         * Skip(n) skips the emission of first N items emitted by an Observable.
         *
         * Let’s say you have an Observable that emits integers from 1-10 and if skip(4) is operator
         * is used, it skips 1-4 and emits the numbers 5, 6, 7, 8, 9, 10.
         */
        Observable
                .range(1, 10)
                .skip(4)
                .subscribe(object : Observer<Int> {
                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG, "Subscribed for example of skip() operator")
                    }

                    override fun onNext(integer: Int) {
                        Log.d(TAG, "onNext example of skip() operator: " + integer)
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "onError: " + e.message)
                    }

                    override fun onComplete() {
                        Log.d(TAG, "Completed example of skip() operator")
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
                Log.d(TAG, "onSubscribe for filter and map operators combined example")
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

    private fun getUsersObservable(): Observable<User> {
        val maleUsers = arrayOf("Mark", "John", "Trump", "Obama")
        val femaleUsers = arrayOf("Lucy", "Scarlett", "April")

        val users = ArrayList<User>()

        for (name in maleUsers) {
            val user = User()
            user.name = name
            user.gender = "male"

            users.add(user)
        }

        for (name in femaleUsers) {
            val user = User()
            user.name = name
            user.gender = "female"

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
                             var gender: String? = null)


    override fun onDestroy() {
        super.onDestroy()

        //Don't send events once the activity is destroyed
        compositeDisposable.clear();
    }
}
