package com.enpassio.reactiveway.view

import android.content.DialogInterface
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.enpassio.reactiveway.R
import com.enpassio.reactiveway.network.ApiClient
import com.enpassio.reactiveway.network.ApiService
import com.enpassio.reactiveway.network.model.model.Note
import com.enpassio.reactiveway.network.model.model.User
import com.enpassio.reactiveway.utils.MyDividerItemDecoration
import com.enpassio.reactiveway.utils.PrefUtils
import com.enpassio.reactiveway.utils.RecyclerTouchListener
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.note_dialog.view.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*


/**
 * Example of Note app following this tutorial (Java):
 * https://www.androidhive.info/RxJava/android-rxjava-networking-with-retrofit-gson-notes-app/
 * Big work here - > conversion to Kotlin :)
 */

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

    private var apiService: ApiService? = null
    private val disposable = CompositeDisposable()
    private var mAdapter: NotesAdapter? = null
    private val notesList = ArrayList<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_main)

        toolbar.setTitle(getString(R.string.activity_title_home))
        setSupportActionBar(toolbar)

        fab.setOnClickListener { showNoteDialog(false, null, -1) }

        // white background notification bar
        whiteNotificationBar(fab)

        apiService = ApiClient.getClient(applicationContext)!!.create(ApiService::class.java)

        mAdapter = NotesAdapter(this, notesList)
        val mLayoutManager = LinearLayoutManager(applicationContext)
        recycler_view.setLayoutManager(mLayoutManager)
        recycler_view.setItemAnimator(DefaultItemAnimator())
        recycler_view.addItemDecoration(MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16))
        recycler_view.setAdapter(mAdapter)

        /**
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete
         * */
        recycler_view.addOnItemTouchListener(RecyclerTouchListener(this,
                recycler_view, object : RecyclerTouchListener.ClickListener {
            override fun onClick(view: View, position: Int) {}

            override fun onLongClick(view: View?, position: Int) {
                showActionsDialog(position)
            }
        }))

        /**
         * Check for stored Api Key in shared preferences
         * If not present, make api call to register the user
         * This will be executed when app is installed for the first time
         * or data is cleared from settings
         * */
        if (TextUtils.isEmpty(PrefUtils.getApiKey(this))) {
            registerUser();
        } else {
            // user is already registered, fetch all notes
            fetchAllNotes();
        }
    }
    //registerUser() – Makes /register call to register the device.
    // Every device is uniquely identified by randomUUID(), so the notes will be bound to
    // a device.

    /**
     * Registering new user
     * sending unique id as device identification
     * https://developer.android.com/training/articles/user-data-ids.html
     */
    private fun registerUser() {
        // unique id to identify the device
        val uniqueId = UUID.randomUUID().toString()


        disposable.add(
                apiService!!
                        .register(uniqueId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<User>() {
                            override fun onSuccess(user: User) {
                                // Storing user API Key in preferences
                                PrefUtils.storeApiKey(applicationContext, user.apiKey!!)

                                Toast.makeText(applicationContext,
                                        "Device is registered successfully! ApiKey: " + PrefUtils.getApiKey(applicationContext),
                                        Toast.LENGTH_LONG).show()
                            }

                            override fun onError(e: Throwable) {
                                Log.e(TAG, "onError: " + e.message)
                                showError(e)
                            }
                        }))
    }


    //fetchAllNotes() – Fetches all the notes created from the device and displays them in
    // RecyclerView. The API returns the notes in random order, so the map() operator is used
    // to sort the notes in descending order by note id.
    /**
     * Fetching all notes from api
     * The received items will be in random order
     * map() operator is used to sort the items in descending order by Id
     */
    private fun fetchAllNotes() {
        disposable.add(
                apiService!!.fetchAllNotes()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map { listOfNotes -> listOfNotes.sortedWith(compareBy({ it.id })) }
                        .subscribeWith(object : DisposableSingleObserver<List<Note>>() {
                            override fun onSuccess(noteses: List<Note>) {
                                runOnUiThread {
                                    notesList.clear()
                                    notesList.addAll(noteses)
                                    mAdapter!!.notifyDataSetChanged()

                                    toggleEmptyNotes()
                                }
                            }

                            override fun onError(e: Throwable) {
                                Log.e(TAG, "onError: " + e.message)
                                showError(e)
                            }
                        })
        )
    }

    //createNote() – Creates new note and adds it to RecyclerView. The newly created note is
    // added to 0 position and notifyItemInserted(0) is called to refresh the list.
    /**
     * Creating new note
     */
    private fun createNote(note: String) {
        disposable.add(
                apiService!!.createNote(note)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<Note>() {

                            override fun onSuccess(note: Note) {
                                if (!TextUtils.isEmpty(note.error)) {
                                    Toast.makeText(applicationContext, note.error, Toast.LENGTH_LONG).show()
                                    return
                                }

                                Log.d(TAG, "new note created: " + note.id + ", " + note.note + ", " + note.timestamp)

                                // Add new item and notify adapter
                                notesList.add(0, note)
                                mAdapter!!.notifyItemInserted(0)

                                toggleEmptyNotes()
                            }

                            override fun onError(e: Throwable) {
                                Log.e(TAG, "onError: " + e.message)
                                showError(e)
                            }
                        }))
    }

    //updateNote() – Updates existing note and notifies the adapter by calling notifyItemChanged()
    /**
     * Updating a note
     */
    private fun updateNote(noteId: Int, note: String, position: Int) {
        disposable.add(
                apiService!!.updateNote(noteId, note)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableCompletableObserver() {
                            override fun onComplete() {
                                Log.d(TAG, "Note updated!")

                                val n = notesList[position]
                                n.note = note

                                // Update item and notify adapter
                                notesList[position] = n
                                mAdapter!!.notifyItemChanged(position)
                            }

                            override fun onError(e: Throwable) {
                                Log.e(TAG, "onError: " + e.message)
                                showError(e)
                            }
                        }))
    }

    //deleteNote() – Deletes existing note and notifies the adapter by calling
    // notifyItemRemoved() method.
    /**
     * Deleting a note
     */
    private fun deleteNote(noteId: Int, position: Int) {
        Log.e(TAG, "deleteNote: $noteId, $position")
        disposable.add(
                apiService!!.deleteNote(noteId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableCompletableObserver() {
                            override fun onComplete() {
                                Log.d(TAG, "Note deleted! $noteId")

                                // Remove and notify adapter about item deletion
                                notesList.removeAt(position)
                                mAdapter!!.notifyItemRemoved(position)

                                Toast.makeText(this@MainActivity, "Note deleted!", Toast.LENGTH_SHORT).show()

                                toggleEmptyNotes()
                            }

                            override fun onError(e: Throwable) {
                                Log.e(TAG, "onError: " + e.message)
                                showError(e)
                            }
                        })
        )
    }

//showActionsDialog() – Open a Dialog with Edit or Delete options.
    // This dialog will be opened by long pressing Note row.
    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */
    private fun showActionsDialog(position: Int) {
        val colors = arrayOf<CharSequence>("Edit", "Delete")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose option")
        builder.setItems(colors, DialogInterface.OnClickListener { dialog, which ->
            if (which == 0) {
                showNoteDialog(true, notesList[position], position)
            } else {
                deleteNote(notesList[position].id, position)
            }
        })
        builder.show()
    }


    //showNoteDialog() – Opens a Dialog to Create or Update a note.
    // This dialog will be opened by tapping on FAB.

    /**
     * Shows alert dialog with EditText options to enter / edit
     * a note.
     * when shouldUpdate=true, it automatically displays old note and changes the
     * button text to UPDATE
     */
    private fun showNoteDialog(shouldUpdate: Boolean, note: Note?, position: Int) {
        val layoutInflaterAndroid = LayoutInflater.from(this@MainActivity)
        val view = layoutInflaterAndroid.inflate(R.layout.note_dialog, null)

        val alertDialogBuilderUserInput = AlertDialog.Builder(this@MainActivity)
        alertDialogBuilderUserInput.setView(view)

        view.dialog_title.setText(if (!shouldUpdate) getString(R.string.lbl_new_note_title) else getString(R.string.lbl_edit_note_title))

        if (shouldUpdate && note != null) {
            view.input_note.setText(note.note)
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(if (shouldUpdate) "update" else "save", DialogInterface.OnClickListener { dialogBox, id -> })
                .setNegativeButton("cancel",
                        DialogInterface.OnClickListener { dialogBox, id -> dialogBox.cancel() })

        val alertDialog = alertDialogBuilderUserInput.create()
        alertDialog.show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(view.input_note.getText().toString())) {
                    Toast.makeText(this@MainActivity, "Enter note!", Toast.LENGTH_SHORT).show()
                    return
                } else {
                    alertDialog.dismiss()
                }

                // check if user updating note
                if (shouldUpdate && note != null) {
                    // update note by it's id
                    updateNote(note.id, view.input_note.getText().toString(), position)
                } else {
                    // create new note
                    createNote(view.input_note.getText().toString())
                }
            }
        })
    }

    /**
     * Showing a Snackbar with error message
     * The error body will be in json format
     * {"error": "Error message!"}
     */
    private fun showError(e: Throwable) {
        var message = ""
        try {
            if (e is IOException) {
                message = "No internet connection!"
            } else if (e is HttpException) {
                val error = e
                val errorBody = error.response().errorBody()!!.string()
                val jObj = JSONObject(errorBody)

                message = jObj.getString("error")
            }
        } catch (e1: IOException) {
            e1.printStackTrace()
        } catch (e1: JSONException) {
            e1.printStackTrace()
        } catch (e1: Exception) {
            e1.printStackTrace()
        }

        if (TextUtils.isEmpty(message)) {
            message = "Unknown error occurred! Check LogCat."
        }

        val snackbar = Snackbar
                .make(coordinator_layout, message, Snackbar.LENGTH_LONG)

        val sbView = snackbar.view
        val textView = sbView.findViewById<TextView>(android.support.design.R.id.snackbar_text)
        textView.setTextColor(Color.YELLOW)
        snackbar.show()
    }

    private fun whiteNotificationBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
            window.statusBarColor = Color.WHITE
        }
    }


    //CompositeDisposable – Disposes all the subscriptions in onDestroy() method
    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    private fun toggleEmptyNotes() {
        if (notesList.size > 0) {
            txt_empty_notes_view.setVisibility(View.GONE)
        } else {
            txt_empty_notes_view.setVisibility(View.VISIBLE)
        }
    }

}
