package com.example.stopwatch

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.stopwatch.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var timer: java.util.Timer
    private var timerRes: ((Long) -> Unit)? = null
    private var timerCount = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        timerRes = {
            Log.d(TAG, "onCreate: $it")
            CoroutineScope(Dispatchers.Main).launch {
                binding.textView.text = it.toTime()
            }
        }
        binding.start.setOnClickListener { startTimer()
            binding.resume.text = "Pause"}
        binding.resume.setOnClickListener {
            if (binding.resume.text == "Pause") {
                PauseTimer()
                binding.resume.text = "Resume"
            } else {
                startTimer()
                binding.resume.text = "Pause"
            }
        }
        binding.reset.setOnClickListener { ResetTimer() }
    }

    fun startTimer() {
        timer = timer("timer", initialDelay = 10, period = 10, action = {
            timerCount += 10
            timerRes?.invoke(timerCount)
        })
    }

    fun PauseTimer() {
        timer.cancel()
    }

    fun ResetTimer() {
        timer.cancel()
        timerCount = 0L
        binding.textView.text = "Hello, World!"
    }
}

private fun Long.toTime(): CharSequence {
    val milli = this % 60
    val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(this) % 60
    val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(this) % 60
    val hours: Long = TimeUnit.MILLISECONDS.toHours(this)

    return String.format("%02d:%02d:%02d:%02d", hours, minutes, seconds, milli)
}
