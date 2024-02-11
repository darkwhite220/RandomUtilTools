package com.darkwhite.sensors

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder

class MyAudioRecorder {
    
    private var audioRecord: AudioRecord? = null
    private val sampleRate = 44100
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
    private var minSize: Int = bufferSize
    private var buffer: ShortArray = ShortArray(minSize)
    private var isRecording: Boolean = false
    
    @SuppressLint("MissingPermission")
    private fun initRecorder() {
        isRecording = true
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            8000,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )
    }
    
    fun startRecording() {
        println("startRecording")
        initRecorder()
        audioRecord?.let {
            it.startRecording()
            while (isRecording) {
                it.read(buffer, 0, minSize)
                val rms = Utils.calculateRMS(buffer)
                // Normalize RMS to a 0-100% range
                val normalizedRMS = (rms / Short.MAX_VALUE) * 100f
                val isLoud = normalizedRMS > 10f // Adjust threshold as needed
                println("$rms, $normalizedRMS")
                
                if (isLoud) {
                    isRecording = false
                    stopRecording()
                }
            }
        }
    }
    
    fun stopRecording() {
        println("stopRecording")
        isRecording = false
        audioRecord?.let {
            it.stop()
            it.release()
        }
        audioRecord = null
    }
}