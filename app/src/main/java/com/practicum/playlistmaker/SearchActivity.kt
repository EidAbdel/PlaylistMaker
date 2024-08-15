package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        val btBackMainMenu = findViewById<Toolbar>(R.id.tb_back_search)
        val btClear = findViewById<Button>(R.id.bt_clear_search)
        val edSearch = findViewById<EditText>(R.id.ed_search)

        btBackMainMenu.setOnClickListener {
            finish()
        }

        btClear.setOnClickListener {
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
                btClear.visibility = buttonVisibility(charSequence)
            }
            override fun afterTextChanged(editable: Editable?) {
                // TODO: Перехват текста сразу после изменения, изменения не увидим, но получим изменённый текст
            }


        }

        edSearch.addTextChangedListener(textWatcher)
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


}