package com.example.hw04

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private val viewModel: StudentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)

        val rootLayout = findViewById<View>(R.id.rootLayout)
        val appBarLayout = findViewById<View>(R.id.appBarLayout)
        val tvSelectedBanner = findViewById<TextView>(R.id.tvSelectedBanner)

        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { _, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            // Apply top padding to the app bar layout so it sits safely below status bar / camera cutout
            appBarLayout.setPadding(0, systemBars.top, 0, 0)
            // Apply bottom padding to root layout for gesture navigation bar
            rootLayout.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel.selectedIndex.observe(this) { index ->
            val student = viewModel.students.getOrNull(index)
            if (student != null) {
                tvSelectedBanner.text = getString(R.string.fmt_selected_student, student.id)
            } else {
                tvSelectedBanner.text = getString(R.string.selected_student_none)
            }
        }
    }
}