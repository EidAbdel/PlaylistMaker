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
//        enableEdgeToEdge()
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
            shareIntent.putExtra(Intent.EXTRA_TEXT,getString(R.string.share_link) )
            startActivity(Intent.createChooser(shareIntent, null))
        }


        btSupport.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.emai_from)))
            supportIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.email_title))
            supportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_body))
            startActivity(supportIntent)
        }

        btUserAgreement.setOnClickListener {
            val agreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.user_agreement_link)))
            startActivity(agreementIntent)
        }

    }
}