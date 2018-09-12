package com.enpassio.reactiveway.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.enpassio.reactiveway.R

/**
 * Example of Note app following this tutorial (Java):
 * https://www.androidhive.info/RxJava/android-rxjava-networking-with-retrofit-gson-notes-app/
 * Big work here - > conversion to Kotlin :)
 */

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }
}
