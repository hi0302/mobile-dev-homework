package com.example.hw02

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class resultform : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultform)
        configureGlassWindow(findViewById(R.id.resultRoot))

        val extras = intent.extras
        showValue(R.id.txtUsername, extras?.getString("username"))
        val password = extras?.getString("password").orEmpty()
        showValue(R.id.txtPassword, "*".repeat(password.length))
        showValue(R.id.txtBirthdate, extras?.getString("birthdate"))
        showValue(R.id.txtGender, extras?.getString("gender"))
        val hobbies = extras?.getString("hobbies")
        findViewById<TextView>(R.id.txtHobbies).text =
            if (hobbies.isNullOrBlank()) getString(R.string.hobbies_none) else hobbies

        findViewById<Button>(R.id.btnExit).setOnClickListener { finishAffinity() }
    }

    private fun showValue(viewId: Int, value: String?) {
        findViewById<TextView>(viewId).text =
            if (value.isNullOrBlank()) getString(R.string.value_unavailable) else value
    }
}
