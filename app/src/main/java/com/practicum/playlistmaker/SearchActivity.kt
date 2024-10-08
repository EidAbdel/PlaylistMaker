package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.viewHolder.TrackAdapter

class SearchActivity : AppCompatActivity() {
    private var textEditTextValue: String = INPUT_EDIT_TEXT_VALUE



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
//        enableEdgeToEdge()
        setContentView(R.layout.activity_search)


        val btBackMainMenu = findViewById<Toolbar>(R.id.tb_back_search)
        val imClear = findViewById<ImageView>(R.id.im_clear_search)
        val edSearch = findViewById<EditText>(R.id.ed_search)

        edSearch.setText(textEditTextValue)

        btBackMainMenu.setOnClickListener {
            finish()
        }

        imClear.setOnClickListener {
            edSearch.setText("")
            edSearch.clearFocus()
            hideSoftKeyboard(it)
        }

        val textWatcher = object : TextWatcher{
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
                // TODO: Перехват события до изменения мы можем посмотреть на старый текс
            }
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int){
                // TODO: перехват самого события изменения текст не увидим но сможем посмотреть что изменилось
                imClear.visibility = buttonVisibility(charSequence)
            }
            override fun afterTextChanged(editable: Editable?) {
                // TODO: Перехват текста сразу после изменения, изменения не увидим, но получим изменённый текст
                textEditTextValue = editable.toString()
            }


        }

        edSearch.addTextChangedListener(textWatcher)
        val rvTrackList = findViewById<RecyclerView>(R.id.rv_track_list)
        rvTrackList.layoutManager = LinearLayoutManager(this)
        rvTrackList.adapter = TrackAdapter(Track.mockTrackLIST)

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
    companion object {
        const val EDIT_TEXT_VALUE = "EDIT_TEXT_VALUE"
        const val INPUT_EDIT_TEXT_VALUE = ""
    }

}