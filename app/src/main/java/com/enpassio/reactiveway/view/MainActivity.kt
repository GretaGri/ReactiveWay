package com.enpassio.reactiveway.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.enpassio.reactiveway.R

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }
}
