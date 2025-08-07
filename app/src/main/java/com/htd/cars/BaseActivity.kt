package com.htd.cars

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: android.content.Context) {
        val contextWithLang = LanguageHelper.createContextWithLanguage(newBase)
        super.attachBaseContext(contextWithLang)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Note: setContentView dipanggil di subclass
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        // Atur logic tombol header universal
        val burgerButton = findViewById<TextView>(R.id.burgerButton)
        val homeButton = findViewById<TextView>(R.id.homeButton)
        // Set warna burgerButton putih dan shadow agar kontras
        burgerButton?.setTextColor(android.graphics.Color.parseColor("#FFFFFF"))
        burgerButton?.setShadowLayer(0f, 0f, 0f, 0)
        // Tampilkan kedua tombol di semua activity
        burgerButton?.visibility = View.VISIBLE
        if (this is MainActivity) {
            homeButton?.visibility = View.GONE
        } else {
            homeButton?.visibility = View.VISIBLE
            homeButton?.setOnClickListener { finish() }
        }
    }
}
