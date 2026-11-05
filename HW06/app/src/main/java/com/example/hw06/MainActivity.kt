package com.example.hw06

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private val mainHandler = Handler(Looper.getMainLooper())
    private var progressThread: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)

        val rootLayout = findViewById<View>(R.id.rootLayout)
        val inputStepCount = findViewById<EditText>(R.id.inputStepCount)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val textProgress = findViewById<TextView>(R.id.textProgress)
        val buttonRunAgain = findViewById<Button>(R.id.buttonRunAgain)

        val initialLeft = rootLayout.paddingLeft
        val initialTop = rootLayout.paddingTop
        val initialRight = rootLayout.paddingRight
        val initialBottom = rootLayout.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { view, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            view.setPadding(
                initialLeft + systemBars.left,
                initialTop + systemBars.top,
                initialRight + systemBars.right,
                initialBottom + systemBars.bottom
            )
            insets
        }

        buttonRunAgain.setOnClickListener {
            val totalSteps = inputStepCount.text.toString().trim().toLongOrNull()
            if (totalSteps == null || totalSteps <= 0L) {
                inputStepCount.error = getString(R.string.error_invalid_step_count)
                return@setOnClickListener
            }

            inputStepCount.error = null
            inputStepCount.isEnabled = false
            buttonRunAgain.isEnabled = false
            progressBar.progress = 0
            textProgress.setText(R.string.progress_zero)

            progressThread = Thread({
                var completedSteps = 0L
                var lastPercent = 0

                while (completedSteps < totalSteps && !Thread.currentThread().isInterrupted) {
                    // Mỗi vòng lặp chỉ hoàn thành thêm đúng một progress step.
                    completedSteps += 1
                    val percent = (completedSteps.toDouble() * 100 / totalSteps).toInt()

                    if (percent != lastPercent) {
                        lastPercent = percent
                        mainHandler.post {
                            progressBar.progress = percent
                            textProgress.text = getString(R.string.progress_percent, percent)

                            if (percent == 100) {
                                inputStepCount.isEnabled = true
                                buttonRunAgain.isEnabled = true
                            }
                        }

                        // Tạo nhịp hiển thị để người dùng quan sát được thanh tiến trình.
                        if (percent < 100) {
                            try {
                                Thread.sleep(10)
                            } catch (_: InterruptedException) {
                                Thread.currentThread().interrupt()
                            }
                        }
                    }
                }
            }, "hw06-progress-worker").also { it.start() }
        }
    }

    override fun onDestroy() {
        progressThread?.interrupt()
        mainHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}
