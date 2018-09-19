package com.enpassio.reactiveway

import android.app.SearchManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.*
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray


/**
 * Example of Note app following this tutorial (Java):
 * https://www.androidhive.info/2017/11/android-recyclerview-with-search-filter-functionality/
 * Big work here - > conversion to Kotlin :)
 */

 class MainActivity : AppCompatActivity(), ContactsAdapter.ContactsAdapterListener {

        private var recyclerView: RecyclerView? = null
        private var contactList: MutableList<Contact>? = null
        private var mAdapter: ContactsAdapter? = null
        private var searchView: SearchView? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
            val toolbar = findViewById<Toolbar>(R.id.toolbar)
            setSupportActionBar(toolbar)

            // toolbar fancy stuff
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setTitle(R.string.toolbar_title)

            recyclerView = findViewById(R.id.recycler_view)
            contactList = ArrayList()
            mAdapter = ContactsAdapter()

            // white background notification bar
            whiteNotificationBar(recyclerView)

            val mLayoutManager = LinearLayoutManager(applicationContext)
            recyclerView!!.layoutManager = mLayoutManager
            recyclerView!!.itemAnimator = DefaultItemAnimator()
            recyclerView!!.addItemDecoration(MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36))
            recyclerView!!.adapter = mAdapter

            fetchContacts()
        }

        /**
         * fetches json by making http calls
         */
        private fun fetchContacts() {
            val request = JsonArrayRequest(URL,
                    object : Response.Listener<JSONArray> {
                        override fun onResponse(response: JSONArray?) {
                            if (response == null) {
                                Toast.makeText(applicationContext, "Couldn't fetch the contacts! Pleas try again.", Toast.LENGTH_LONG).show()
                                return
                            }

                            val items = Gson().fromJson<List<Contact>>(response.toString(), object : TypeToken<List<Contact>>() {

                            }.type)

                            // adding contacts to contacts list
                            contactList!!.clear()
                            contactList!!.addAll(items)

                            // refreshing recycler view
                            mAdapter!!.notifyDataSetChanged()
                        }
                    }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError) {
                    // error in getting json
                    Log.e(TAG, "Error: " + error.message)
                    Toast.makeText(applicationContext, "Error: " + error.message, Toast.LENGTH_SHORT).show()
                }
            })

            MyApplication.instance!!.addToRequestQueue(request)
        }

       override fun onCreateOptionsMenu(menu: Menu): Boolean {
            menuInflater.inflate(R.menu.menu_main, menu)

            // Associate searchable configuration with the SearchView
            val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
            searchView = menu.findItem(R.id.action_search)
                    .getActionView() as SearchView
            searchView!!.setSearchableInfo(searchManager
                    .getSearchableInfo(componentName))
            searchView!!.setMaxWidth(Integer.MAX_VALUE)

            // listening to search query text change
            searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
               override fun onQueryTextSubmit(query: String): Boolean {
                    // filter recycler view when query submitted
                    mAdapter!!.filter.filter(query)
                    return false
                }

               override fun onQueryTextChange(query: String): Boolean {
                    // filter recycler view when text is changed
                    mAdapter!!.filter.filter(query)
                    return false
                }
            })
            return true
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            val id = item.getItemId()


            return if (id == R.id.action_search) {
                true
            } else super.onOptionsItemSelected(item)

        }

        override fun onBackPressed() {
            // close search view on back button pressed
            if (!searchView!!.isIconified()) {
                searchView!!.setIconified(true)
                return
            }
            super.onBackPressed()
        }

        private fun whiteNotificationBar(view: View?) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                var flags = view!!.systemUiVisibility
                flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                view.systemUiVisibility = flags
                window.statusBarColor = Color.WHITE
            }
        }

        override fun onContactSelected(contact: Contact) {
            Toast.makeText(applicationContext, "Selected: " + contact.name + ", " + contact.phone, Toast.LENGTH_LONG).show()
        }

        companion object {
            private val TAG = MainActivity::class.java.simpleName

            // url to fetch contacts json
            private val URL = "https://api.androidhive.info/json/contacts.json"
        }
    }

