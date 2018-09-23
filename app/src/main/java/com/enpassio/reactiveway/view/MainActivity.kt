package com.enpassio.reactiveway.view

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.enpassio.reactiveway.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


/**
 * Example of Note app following this tutorial (Java):
 * https://www.androidhive.info/RxJava/android-rxjava-instant-search-local-remote-databases/#adding-retrofit
 * Big work here - > conversion to Kotlin :)
 */

 class MainActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
            setSupportActionBar(toolbar)

            // toolbar fancy stuff
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)

            // white background notification bar
            whiteNotificationBar(toolbar)


            btn_local_search.setOnClickListener{
                // launching local search activity
                startActivity(Intent(this@MainActivity, LocalSearchActivity::class.java))
            }

            btn_remote_search.setOnClickListener{
                // launch remote search activity
                startActivity(Intent(this@MainActivity, RemoteSearchActivity::class.java))
            }
        }

       private fun whiteNotificationBar(view: View?) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                var flags = view!!.systemUiVisibility
                flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                view.systemUiVisibility = flags
                window.statusBarColor = Color.WHITE
            }
        }
    }

