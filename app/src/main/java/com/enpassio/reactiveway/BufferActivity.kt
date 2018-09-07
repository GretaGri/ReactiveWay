package com.enpassio.reactiveway

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.buffer_example.*
import java.util.concurrent.TimeUnit

/**
 * from sources: https://www.androidhive.info/RxJava/tutorials/
 *
 * https://www.androidhive.info/RxJava/rxjava-operators-buffer-debounce/#buffer
 * Buffer example
 */
class BufferActivity : AppCompatActivity() {
    companion object {

        private val TAG = BufferActivity::class.java.getSimpleName()
    }

    private var disposable: Disposable? = null
    private var maxTaps = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.buffer_example)

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
                        Log.e(TAG, "onComplete")
                    }
                })
    }

    override fun onDestroy() {
        super.onDestroy()

        disposable?.dispose()
    }

}

