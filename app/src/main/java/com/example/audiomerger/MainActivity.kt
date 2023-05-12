package com.example.audiomerger

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.audiomerger.databinding.ActivityMainBinding
import java.io.File


@SuppressLint("MissingPermission")
class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private var isRecording = false

    private val REQUEST_PERMISSION_CODE = 1

    // Initialize the MediaRecorder objects
    private lateinit var mediaRecorderFromDeviceMic: MediaRecorder
    private lateinit var mediaRecorderFromHeadphoneMic: MediaRecorder

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.start.setOnClickListener {
            if (!isRecording) {
                if (checkPermissions()) {
                    startRecording()
                    Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show()
                } else {
                    requestPermissions()
                }
            }
        }

        binding.end.setOnClickListener {
            if (isRecording) {
                stopRecording()
                Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermissions(): Boolean {
        val recordPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.RECORD_AUDIO
        )
        val modifySettingsPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.MODIFY_AUDIO_SETTINGS
        )
        return recordPermission == PackageManager.PERMISSION_GRANTED &&
                modifySettingsPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.MODIFY_AUDIO_SETTINGS
            ),
            REQUEST_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording()
                Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Create instances of MediaRecorder to handle recording from the device microphone and the headphone microphone simultaneously
    private fun setupAudioRecording() {
        // Create the MediaRecorder for recording from the device microphone
        mediaRecorderFromDeviceMic = MediaRecorder()
        mediaRecorderFromDeviceMic.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorderFromDeviceMic.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorderFromDeviceMic.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorderFromDeviceMic.setOutputFile(getAudioFilePath("device_mic_audio.mp3"))

        // Create the MediaRecorder for recording from the headphone microphone
        mediaRecorderFromHeadphoneMic = MediaRecorder()
        mediaRecorderFromHeadphoneMic.setAudioSource(MediaRecorder.AudioSource.DEFAULT)
        mediaRecorderFromHeadphoneMic.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorderFromHeadphoneMic.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorderFromHeadphoneMic.setOutputFile(getAudioFilePath("headphone_mic_audio.mp3"))
    }

    // Save the recorded audio to a file
    private fun getAudioFilePath(fileName: String): String {
        val audioFile = File(getExternalFilesDir(null), fileName)
        return audioFile.absolutePath
    }

    // Start recording
    private fun startRecording() {
        setupAudioRecording()

        isRecording = true
        // Start recording from the device microphone
        mediaRecorderFromDeviceMic.prepare()
        mediaRecorderFromDeviceMic.start()

        // Start recording from the headphone microphone
        mediaRecorderFromHeadphoneMic.prepare()
        mediaRecorderFromHeadphoneMic.start()
    }

    // Stop recording
    private fun stopRecording() {
        // Stop recording from the device microphone
        isRecording = false
        mediaRecorderFromDeviceMic.stop()
        mediaRecorderFromDeviceMic.release()

        // Stop recording from the headphone microphone
        mediaRecorderFromHeadphoneMic.stop()
        mediaRecorderFromHeadphoneMic.release()
    }

}
