package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class MainSetting : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_setting)

        val btBackMainMenu = findViewById<Toolbar>(R.id.tb_back_setting)
        val btShare = findViewById<Button>(R.id.bt_share)
        val btSupport = findViewById<Button>(R.id.bt_support)
        val btUserAgreement = findViewById<Button>(R.id.bt_user_agreement)

        btBackMainMenu.setOnClickListener {
            finish()
        }

        btShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_TEXT,"https://practicum.yandex.ru/android-developer/?from=catalog" )
            startActivity(shareIntent)
        }


        btSupport.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("abdel.eid@yandex.ru"))
            supportIntent.putExtra(Intent.EXTRA_TITLE, "Спасибо разработчикам и разработчицам за крутое приложение!")
            supportIntent.putExtra(Intent.EXTRA_TEXT, "Сообщение разработчикам и разработчицам приложения Playlist Maker")
            startActivity(supportIntent)
        }

        btUserAgreement.setOnClickListener {
            val agreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://yandex.ru/legal/practicum_offer/"))
            startActivity(agreementIntent)
        }

    }
}