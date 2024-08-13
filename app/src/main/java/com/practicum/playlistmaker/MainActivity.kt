package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val btSearch = findViewById<Button>(R.id.bt_find)
        val btMedia = findViewById<Button>(R.id.bt_media)
        val btSetting = findViewById<Button>(R.id.bt_setting)

        btSetting.setOnClickListener{
            val displayIntent = Intent(this, MainSetting::class.java)
            startActivity(displayIntent)
        }
        btMedia.setOnClickListener{
            val displayIntent = Intent(this, MediaActivity::class.java)
            startActivity(displayIntent)
        }
        btSearch.setOnClickListener{
            val displayIntent = Intent(this, SearchActivity::class.java)
            startActivity(displayIntent)
        }
    }
}