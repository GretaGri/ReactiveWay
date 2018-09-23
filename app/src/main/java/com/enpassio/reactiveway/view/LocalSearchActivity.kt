package com.enpassio.reactiveway.view

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.enpassio.reactiveway.R
import com.enpassio.reactiveway.adapter.ContactsAdapterFilterable
import com.enpassio.reactiveway.network.ApiClient
import com.enpassio.reactiveway.network.ApiService
import com.enpassio.reactiveway.network.model.Contact
import com.jakewharton.rxbinding2.widget.RxTextView
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_local_search.*
import java.util.concurrent.TimeUnit


/**
 * Created by Greta Grigutė on 2018-09-23.
 */
class LocalSearchActivity : AppCompatActivity(), ContactsAdapterFilterable.ContactsAdapterListener {

    private var disposable = CompositeDisposable()
    private var apiService: ApiService? = null
    private var mAdapter: ContactsAdapterFilterable? = null
    private val contactsList = ArrayList <Contact>()

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_search)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mAdapter = ContactsAdapterFilterable(this, contactsList, this)

        val mLayoutManager = LinearLayoutManager(applicationContext)
        recycler_view.layoutManager = mLayoutManager
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recycler_view.adapter = mAdapter

        whiteNotificationBar(recycler_view)
        apiService = ApiClient.client()!!.create(ApiService::class.java)

        disposable.add(RxTextView.textChangeEvents(input_search)
                .skipInitialValue()
                .debounce(300, TimeUnit.MILLISECONDS)
                /*.filter(new Predicate<TextViewTextChangeEvent>() {
                    @Override
                    public boolean test(TextViewTextChangeEvent textViewTextChangeEvent) throws Exception {
                        return TextUtils.isEmpty(textViewTextChangeEvent.text().toString()) || textViewTextChangeEvent.text().toString().length() > 2;
                    }
                })*/
                .distinctUntilChanged()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(searchContacts()))


        // source: `gmail` or `linkedin`
        // fetching all contacts on app launch
        // only gmail will be fetched
        fetchContacts("gmail")
    }

    private fun searchContacts(): DisposableObserver<TextViewTextChangeEvent> {
        return object : DisposableObserver<TextViewTextChangeEvent>() {
            override fun onNext(textViewTextChangeEvent: TextViewTextChangeEvent) {
                Log.d(TAG, "Search query: " + textViewTextChangeEvent.text())
                mAdapter!!.filter.filter(textViewTextChangeEvent.text())
            }

            override fun onError(e: Throwable) {
                Log.e(TAG, "onError: " + e.message)
            }

            override fun onComplete() {

            }
        }
    }

    /**
     * Fetching all contacts
     */
    private fun fetchContacts(source: String?) {
        disposable.add(apiService!!
                .getContacts(source!!,null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<Contact>>() {
                    override fun onSuccess(contacts: List<Contact>) {
                        contactsList.clear()
                        contactsList.addAll(contacts)
                        mAdapter!!.notifyDataSetChanged()
                    }

                    override fun onError(e: Throwable) {

                    }
                }))
    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }

    override fun onContactSelected(contact: Contact) {

    }

    private fun whiteNotificationBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.getSystemUiVisibility()
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.setSystemUiVisibility(flags)
            window.statusBarColor = Color.WHITE
        }
    }

   override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId().equals(android.R.id.home)) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {

        private val TAG = LocalSearchActivity::class.java.simpleName
    }
}