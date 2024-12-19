package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.api.ITunesApi
import com.practicum.playlistmaker.api.ITunesResponse
import com.practicum.playlistmaker.preferences.HistoryTrackPreferences
import com.practicum.playlistmaker.utils.HISTORY_PREFERENCES
import com.practicum.playlistmaker.viewHolder.TrackAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {

    private var textEditTextValue: String = INPUT_EDIT_TEXT_VALUE

    private var clientRequest: String = ""
    private var tracks = mutableListOf<Track>()
    private val tracksAdapter = TrackAdapter(tracks)

    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesApi = retrofit.create(ITunesApi::class.java)

    private lateinit var txError: TextView
    private lateinit var imError: ImageView
    private lateinit var btErrorRefresh: Button

    private lateinit var txHist: TextView
    private lateinit var btHistclear: Button

    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var edSearch: EditText

    private val handler = Handler(Looper.getMainLooper())

    private val searchRunnable = Runnable { searchTrack() }




    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT_VALUE, EDIT_TEXT_VALUE)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        textEditTextValue = savedInstanceState.getString(EDIT_TEXT_VALUE, INPUT_EDIT_TEXT_VALUE)

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)


        val btBackMainMenu = findViewById<Toolbar>(R.id.tb_back_search)
        val imClear = findViewById<ImageView>(R.id.im_clear_search)
        edSearch = findViewById(R.id.ed_search)

        edSearch.setText(textEditTextValue)

        txError = findViewById(R.id.error_message)
        imError = findViewById(R.id.error_image)
        btErrorRefresh = findViewById(R.id.bt_refresh)


        txHist = findViewById(R.id.tx_clear_history)
        btHistclear = findViewById(R.id.bt_clear_history)


       recyclerView = findViewById(R.id.rv_track_list)
        progressBar = findViewById(R.id.progressBar)
        recyclerView.adapter = tracksAdapter

        histTrack()

        btHistclear.setOnClickListener {
            getSharedPreferences(HISTORY_PREFERENCES, MODE_PRIVATE).edit()
                .clear()
                .commit()
            tracks.clear()
            tracksAdapter.notifyDataSetChanged()
            viewState(HIST_STATE)
        }

        btBackMainMenu.setOnClickListener {
            finish()
        }

        btErrorRefresh.setOnClickListener {
            searchDebounce()

        }

        imClear.setOnClickListener {
            edSearch.setText("")
            edSearch.clearFocus()
            hideSoftKeyboard(it)
            tracks.clear()
            tracksAdapter.notifyDataSetChanged()
            histTrack()

        }


        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // TODO: Перехват события до изменения мы можем посмотреть на старый текс
            }

            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                // TODO: перехват самого события изменения текст не увидим но сможем посмотреть что изменилось
                clientRequest = charSequence.toString()
                imClear.visibility = buttonVisibility(charSequence)


                if (edSearch.hasFocus() && charSequence?.isEmpty() == true) {

                    histTrack()

                } else {
                    viewState(SEARCH_WAIT)
                    searchDebounce()
                }

            }

            override fun afterTextChanged(editable: Editable?) {
                // TODO: Перехват текста сразу после изменения, изменения не увидим, но получим изменённый текст
                textEditTextValue = editable.toString()
                recyclerView.adapter = tracksAdapter
                Log.d("histTrack", "afterTextChanged = ${editable.toString()}")
            }


        }

        edSearch.addTextChangedListener(textWatcher)


    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)

    }



    private fun histTrack() {
        val sharedPrefs = getSharedPreferences(HISTORY_PREFERENCES, MODE_PRIVATE)
        val tracksHist = HistoryTrackPreferences().readAll(sharedPrefs)
        if (tracksHist.isEmpty() != true) {
            tracks.clear()
            tracks.addAll(tracksHist)
            tracksAdapter.notifyDataSetChanged()
        }
        Log.d("histTrack", "статус истории ${!tracksHist.isEmpty()}")
        viewState(HIST_STATE)

    }


    private fun isVisible(): Int {
        val sharedPrefs = getSharedPreferences(HISTORY_PREFERENCES, MODE_PRIVATE)
        val tracksHist = HistoryTrackPreferences().readAll(sharedPrefs)
        if (tracksHist.size != 0) {
            return View.VISIBLE
        } else (
                return View.GONE
                )
    }

    private fun searchTrack() {
        viewState(SEARCH_WAIT)
        if (clientRequest.isNotEmpty()) {
            iTunesApi.findSong(clientRequest).enqueue(object : Callback<ITunesResponse> {
                override fun onResponse(
                    call: Call<ITunesResponse>,
                    response: Response<ITunesResponse>
                ) {
                    viewState(SEARCH_END)
                    if (response.isSuccessful) {
                        Log.d("search", response.isSuccessful.toString())
                        tracks.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            Log.d("search", response.body()?.results!!.toString())
                            tracks.addAll(response.body()?.results!!)
                            tracksAdapter.notifyDataSetChanged()
                        }
                        if (tracks.isEmpty()) {
                            viewState(ERROR_NOT_FOUND)
                        } else {
                            viewState(SUCCESSFUL)
                        }
                    } else {
                        viewState(ERROR_NETWORK_CONECTION)
                    }
                }

                override fun onFailure(call: Call<ITunesResponse>, t: Throwable) {
                    viewState(ERROR_NETWORK_CONECTION)
                }
            })

        } else {
            viewState(SEARCH_END)
        }
    }


    private fun viewState(status: Int){

        when (status) {
            ERROR_NETWORK_CONECTION -> {
                txError.text = getString(R.string.something_went_wrong)
                imError.setBackgroundResource(R.drawable.something_went_wrong)
                txError.visibility = View.VISIBLE
                imError.visibility = View.VISIBLE
                btErrorRefresh.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                txHist.visibility = View.GONE
                btHistclear.visibility = View.GONE
            }
            ERROR_NOT_FOUND -> {
                txError.text = getString(R.string.nothing_found)
                imError.setBackgroundResource(R.drawable.nothing_found)
                txError.visibility = View.VISIBLE
                imError.visibility = View.VISIBLE
                btErrorRefresh.visibility = View.GONE
                progressBar.visibility = View.GONE
                txHist.visibility = View.GONE
                btHistclear.visibility = View.GONE
            }
            SUCCESSFUL -> {
                recyclerView.visibility = View.VISIBLE
                txError.visibility = View.GONE
                imError.visibility = View.GONE
                btErrorRefresh.visibility = View.GONE
                progressBar.visibility = View.GONE
            }

            HIST_STATE -> {
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                txHist.visibility = isVisible()
                btHistclear.visibility = isVisible()
                txError.visibility = View.GONE
                imError.visibility = View.GONE
                btErrorRefresh.visibility = View.GONE
            }

            SEARCH_WAIT -> {
                recyclerView.visibility = View.GONE
                txError.visibility = View.GONE
                imError.visibility = View.GONE
                btErrorRefresh.visibility = View.GONE
                txHist.visibility = View.GONE
                btHistclear.visibility = View.GONE


                progressBar.visibility = View.VISIBLE
            }

            SEARCH_END -> progressBar.visibility = View.GONE



        }
    }




        private fun buttonVisibility(charSequence: CharSequence?): Int {
            return if (charSequence.isNullOrEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        private fun hideSoftKeyboard(view: View) {
            val softKeyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            softKeyboard.hideSoftInputFromWindow(view.windowToken, 0)
        }


        private fun searchDebounce() {
            handler.removeCallbacks(searchRunnable)
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        }




        companion object {
            private const val SEARCH_DEBOUNCE_DELAY = 2000L


            private const val SUCCESSFUL = 0
            private const val ERROR_NOT_FOUND = 1
            private const val ERROR_NETWORK_CONECTION = 2
            private const val HIST_STATE = 3
            private const val SEARCH_WAIT = 4
            private const val SEARCH_END = 5

            const val EDIT_TEXT_VALUE = "EDIT_TEXT_VALUE"
            const val INPUT_EDIT_TEXT_VALUE = ""
        }

    }