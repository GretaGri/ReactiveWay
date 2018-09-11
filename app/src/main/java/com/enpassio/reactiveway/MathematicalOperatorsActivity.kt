package com.enpassio.reactiveway

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import hu.akarnokd.rxjava2.math.MathObservable
import io.reactivex.*
import io.reactivex.Observable.fromArray
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers
import java.util.Comparator.comparing


class MathematicalOperatorsActivity : AppCompatActivity() {
    /**
     * Source: https://www.androidhive.info/RxJava/mathematical-operators-rxjava/
     *
     *  Max, Min, Sum, Average, Count & Reduce operators
     */

    companion object {
        private val TAG = MathematicalOperatorsActivity::class.java.simpleName
    }

    private var disposable: Disposable? = null

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
                        disposable = d
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
                        disposable = d
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
                        disposable = d
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
                        disposable = d
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
                        disposable = d
                        Log.d(TAG, "onSubscribe for average() operator example")
                    }

                    override fun onNext(double: Double) {
                        Log.d(TAG, "Average value: " + double.toInt())
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "onError: " + e.message)
                    }

                    override fun onComplete() {
                        Log.d(TAG, "onComplete average() from integer example")
                    }
                })

        /**
         * Count() operator
         * ---
         * Counts number of items emitted by an Observable and emits only the count value.
         *
         * Below, we have an Observable that emits both Male and Female users. We can count number of Male
         * users using count() operator as shown.
         *
         * filter() filters the items by gender by applying
         * user.getGender().equalsIgnoreCase(“male”) on each emitted item.
         * */

        getUsersObservable()
                .filter(object : Predicate<User> {
                    @Throws(Exception::class)
                    override fun test(user: User): Boolean {
                        // for equalsIgnoreCase in Kotlin:
                        //https://stackoverflow.com/questions/50198067/kotlin-equivalent-of-javas-equalsignorecase
                        return user.gender.equals("male", ignoreCase = true)
                    }
                })
                .count()
                .subscribeWith(object : SingleObserver<Long> {
                    override fun onSubscribe(d: Disposable) {
                        disposable = d
                        Log.d(TAG, "onSubscribe for count() operator example")
                    }

                    override fun onSuccess(count: Long) {
                        Log.d(TAG, "Male users count: " + count)
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "onError: " + e.message)
                    }
                })

        /**
         * Reduce() operator
         * ---
         * Reduce applies a function on each item and emits the final result. First, it applies a
         * function to first item, takes the result and feeds back to same function on second item.
         * This process continuous until the last emission. Once all the items are over, it emits
         * the final result.
         *
         * Below we have an Observable that emits numbers from 1 to 10. The reduce() operator
         * calculates the sum of all the numbers and emits the final result.
         */

        Observable
                .range(1, 10)
                .reduce(object : BiFunction<Int, Int, Int> {
                    @Throws(Exception::class)
                    override fun apply(number: Int, sum: Int): Int {
                        return sum + number
                    }
                })
                .subscribe(object : MaybeObserver<Int> {
                    override fun onSubscribe(d: Disposable) {
                        disposable = d
                        Log.d(TAG, "onSubscribe for reduce() operator example")
                    }

                    override fun onSuccess(integer: Int) {
                        Log.d(TAG, "Sum of numbers from 1 - 10 is: " + integer)
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "onError: " + e.message)
                    }

                    override fun onComplete() {
                        Log.d(TAG, "onComplete reduce() example")
                    }
                })

        /** Mathematical operators used on custom data types example
         *
         *
         */
        val persons = ArrayList<Person>()
        persons.addAll(getPersons())

        val personObservable = rx.Observable.from(persons)

        //https://stackoverflow.com/questions/47249096/how-to-target-jvm-9-on-kotlin-with-gradle
        rx.observables.MathObservable.from(personObservable)
                .max(compareBy(Person::age))
                .subscribe(object : rx.Observer<Person> {
                        override fun onError(e: Throwable) {
                        Log.e(TAG, "onError: " + e.message)
                    }

                    override fun onNext(person: Person) {
                        Log.d(TAG, "Person with max age: " + person.name + ", " + person.age + " yrs")
                    }

                    override fun onCompleted() {
                        Log.d(TAG, "onComplete max() with custom data example")
                    }
                })
    }

    private fun getPersons(): List<Person> {
        val persons = ArrayList<Person>()

        val p1 = Person("Lucy", 24)
        persons.add(p1)

        val p2 = Person("John", 45)
        persons.add(p2)

        val p3 = Person("Obama", 51)
        persons.add(p3)

        return persons
    }

    internal data class Person(var name: String? = null,
                               var age: Int? = null)

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
        disposable!!.dispose()
    }
}
