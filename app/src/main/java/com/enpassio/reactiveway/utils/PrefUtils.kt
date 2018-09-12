package com.enpassio.reactiveway.utils

/**
 * Created by Greta Grigutė on 2018-09-12.
 */
import android.content.Context
import android.content.SharedPreferences

/**
 * Storing API Key in shared preferences to
 * add it in header part of every retrofit request
 */
class PrefUtils {
    companion object {

        private fun getSharedPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences("APP_PREF", Context.MODE_PRIVATE)
        }

        fun storeApiKey(context: Context, apiKey: String) {
            val editor = getSharedPreferences(context).edit()
            editor.putString("API_KEY", apiKey)
            editor.commit()
        }

        fun getApiKey(context: Context): String? {
            return getSharedPreferences(context).getString("API_KEY", null)
        }
    }
}