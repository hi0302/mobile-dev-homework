package com.group1.hw08

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.group1.hw08.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val viewModel: ScoreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.plusOneButtonA.setOnClickListener {
            viewModel.incrementScore(true)
        }

        binding.plusOneButtonB.setOnClickListener {
            viewModel.incrementScore(false)
        }

        binding.selectTeamButton.setOnClickListener {
            viewModel.selectTeam()
        }

        binding.resetButton.setOnClickListener {
            viewModel.resetScores()
        }
    }
}
