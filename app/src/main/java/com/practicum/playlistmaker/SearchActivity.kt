package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.EditorInfo
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

    private lateinit var errorText: TextView
    private lateinit var errorNotFound: ImageView
    private lateinit var errorWentWrong: ImageView
    private lateinit var btRefresh: Button

    private lateinit var progressBar: ProgressBar

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
        val edSearch = findViewById<EditText>(R.id.ed_search)

        edSearch.setText(textEditTextValue)

        errorText = findViewById(R.id.errorMessage)
        errorNotFound = findViewById(R.id.nothing_found)
        errorWentWrong = findViewById(R.id.something_went_wrong)
        btRefresh = findViewById(R.id.bt_refresh)

        val txHist = findViewById<TextView>(R.id.tx_clear_history)
        val btHistclear = findViewById<Button>(R.id.bt_clear_history)


        val recyclerView = findViewById<RecyclerView>(R.id.rv_track_list)

        histTrack()

        progressBar = findViewById(R.id.progressBar)

        txHist.visibility = isVisible()
        btHistclear.visibility = isVisible()

        recyclerView.adapter = tracksAdapter


        btHistclear.setOnClickListener {
            getSharedPreferences(HISTORY_PREFERENCES, MODE_PRIVATE).edit()
                .clear()
                .commit()
            tracks.clear()
            tracksAdapter.notifyDataSetChanged()
            txHist.visibility = isVisible()
            btHistclear.visibility = isVisible()
        }

        btBackMainMenu.setOnClickListener {
            finish()
        }
        btRefresh.setOnClickListener {
            searchTrack()

        }

        imClear.setOnClickListener {
            edSearch.setText("")
            edSearch.clearFocus()
            hideSoftKeyboard(it)
            tracks.clear()
            tracksAdapter.notifyDataSetChanged()
            histTrack()
            errorText.visibility = View.GONE
            errorNotFound.visibility = View.GONE
            errorWentWrong.visibility = View.GONE
            btRefresh.visibility = View.GONE
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
                    txHist.visibility = View.VISIBLE
                    btHistclear.visibility = View.VISIBLE
                } else {
                    txHist.visibility = View.GONE
                    btHistclear.visibility = View.GONE
                    tracks.clear()
                    tracksAdapter.notifyDataSetChanged()
                    progressBar.visibility = View.VISIBLE
                    errorText.visibility = View.GONE
                    errorNotFound.visibility = View.GONE
                    errorWentWrong.visibility = View.GONE
                    btRefresh.visibility = View.GONE
                    searchDebounce()

                }


            }

            override fun afterTextChanged(editable: Editable?) {
                // TODO: Перехват текста сразу после изменения, изменения не увидим, но получим изменённый текст
                textEditTextValue = editable.toString()
                recyclerView.adapter = tracksAdapter
            }


        }

        edSearch.addTextChangedListener(textWatcher)


    }

    private fun histTrack() {
        val sharedPrefs = getSharedPreferences(HISTORY_PREFERENCES, MODE_PRIVATE)
        val tracksHist = HistoryTrackPreferences().readAll(sharedPrefs)
        tracks.clear()
        tracks.addAll(tracksHist)
        tracksAdapter.notifyDataSetChanged()

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
        if (clientRequest.isNotEmpty()) {
            iTunesApi.findSong(clientRequest).enqueue(object : Callback<ITunesResponse> {
                override fun onResponse(
                    call: Call<ITunesResponse>,
                    response: Response<ITunesResponse>
                ) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        tracks.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            tracks.addAll(response.body()?.results!!)
                            tracksAdapter.notifyDataSetChanged()
                        }
                        if (tracks.isEmpty()) {
                            showErrorMessage(getString(R.string.nothing_found), 1)
                        } else {
                            showErrorMessage("", 2)
                        }
                    } else {
                        showErrorMessage(getString(R.string.something_went_wrong), 2)
                    }
                }

                override fun onFailure(call: Call<ITunesResponse>, t: Throwable) {
                    showErrorMessage(getString(R.string.something_went_wrong), 2)
                }
            })

        } else {
            progressBar.visibility = View.GONE
        }
    }

        private fun showErrorMessage(text: String, type: Int) {
            if (text.isNotEmpty()) {
                errorText.visibility = View.VISIBLE
                errorNotFound.visibility = View.GONE
                errorWentWrong.visibility = View.GONE
                btRefresh.visibility = View.GONE
                tracks.clear()
                tracksAdapter.notifyDataSetChanged()
                errorText.text = text
                if (type == 1) {
                    errorNotFound.visibility = View.VISIBLE
                    errorText.visibility = View.VISIBLE
                    }
                else {
                    errorWentWrong.visibility = View.VISIBLE
                    btRefresh.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }
            } else {
                errorText.visibility = View.GONE
                errorNotFound.visibility = View.GONE
                errorWentWrong.visibility = View.GONE
                btRefresh.visibility = View.GONE

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
            const val EDIT_TEXT_VALUE = "EDIT_TEXT_VALUE"
            const val INPUT_EDIT_TEXT_VALUE = ""
        }

    }