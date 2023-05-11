package com.example.audiomerger

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.audiomerger.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@SuppressLint("MissingPermission")
class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private lateinit var micRecorder: MediaRecorder
    private lateinit var headphoneRecorder: MediaRecorder

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.start.setOnClickListener {
            // Check if the app has permission to record audio.
            if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                // Request permission.
                requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO), 100)
            } else {
                // Start recording.
                initRec()
            }
        }

        binding.end.setOnClickListener {
            headphoneRecorder.stop()
            micRecorder.stop()
            headphoneRecorder.release()
            micRecorder.release()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Start recording.
                initRec()
            } else {
                // Permission denied.
                Log.e(TAG, "Permission denied to record audio.")
            }
        }
    }

    private fun initRec() {
        micRecorder = MediaRecorder()
        headphoneRecorder = MediaRecorder()
        startRecording(micRecorder, MediaRecorder.AudioSource.MIC, "recording_mic.ogg")
        startRecording(headphoneRecorder, MediaRecorder.AudioSource.DEFAULT, "recording_headphone.ogg")
    }

    private fun startRecording(mediaRecorder: MediaRecorder, source: Int, fileName: String) {
        // Set the audio source to both the device mic.
        mediaRecorder.setAudioSource(source)

        // Set the output format to .wav.
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.OGG)

        // Set the output file path.
        val filePath = "${getExternalFilesDir(Environment.DIRECTORY_MUSIC)}/$fileName"
        mediaRecorder.setOutputFile(filePath)

        // Set the audio encoder.
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

        // Prepare the MediaRecorder object.
        try {
            mediaRecorder.prepare()
        } catch (e: Exception) {
            Log.e(TAG, "Error preparing MediaRecorder: $e")
        }

        // Start recording.
        CoroutineScope(Dispatchers.Default).launch {
            mediaRecorder.start()
        }
    }

}
