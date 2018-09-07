package com.enpassio.reactiveway

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.buffer_debounce_example.*
import java.util.concurrent.TimeUnit
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent
import io.reactivex.observers.DisposableObserver




/**
 * from sources: https://www.androidhive.info/RxJava/tutorials/
 *
 * https://www.androidhive.info/RxJava/rxjava-operators-buffer-debounce/#buffer
 * Buffer example
 *
 * https://www.androidhive.info/RxJava/rxjava-operators-buffer-debounce/#debounce
 * Debounce example
 */
class BufferDebounceActivity : AppCompatActivity() {
    companion object {

        private val TAG = BufferDebounceActivity::class.java.getSimpleName()
    }

    private val compositeDisposable = CompositeDisposable()
    private var disposable: Disposable? = null
    private var maxTaps = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.buffer_debounce_example)

        // to get rid of findViewById learning source:
        // https://antonioleiva.com/kotlin-android-extensions/

        // Source to understand RxBinding:
        // https://code.tutsplus.com/tutorials/rxjava-for-android-apps-introducing-rxbinding-and-rxlifecycle--cms-28565
        RxView.clicks(layout_tap_area)
                .map { it: Any -> 1 }
                .buffer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : Observer<List<Int>> {
                    override fun onSubscribe(d: Disposable) {
                        disposable = d
                    }

                    override fun onNext(integers: List<Int>) {
                        Log.e(TAG, "onNext: " + integers.size + " taps received!")
                        if (integers.size > 0) {
                            maxTaps = if (integers.size > maxTaps) integers.size else maxTaps
                            tap_result.setText(String.format("Received %d taps in 3 secs", integers.size))
                            tap_result_max_count.setText(String.format("Maximum of %d taps received in this session", maxTaps))
                        }
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "onError: " + e.message)
                    }

                    override fun onComplete() {
                        Log.d(TAG, "onComplete Buffer example")
                    }
                })

        // Debounce operator
        // -
        // It emits items only when a specified timespan is passed. This operator
        // is very useful when the Observable is rapidly emitting items but you are only interested
        // in receiving them in timely manner.

        compositeDisposable.add(
                RxTextView.textChangeEvents(input_search)
                        .skipInitialValue()
                        .debounce(300, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(searchQuery()));

        txt_search_string.setText(getString(R.string.search_query_info))

    }

    private fun searchQuery(): DisposableObserver<TextViewTextChangeEvent> {
        return object : DisposableObserver<TextViewTextChangeEvent>() {
            override fun onNext(textViewTextChangeEvent: TextViewTextChangeEvent) {
                Log.d(TAG, "search string: " + textViewTextChangeEvent.text().toString())

                txt_search_string.setText("Query: " + textViewTextChangeEvent.text().toString())
            }

            override fun onError(e: Throwable) {
                Log.e(TAG, "onError: " + e.message)
            }

            override fun onComplete() {
                Log.d(TAG, "onComplete Debounce example")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        disposable?.dispose()
        compositeDisposable.clear()
    }

}

