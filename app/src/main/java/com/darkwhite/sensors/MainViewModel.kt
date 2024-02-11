package com.darkwhite.sensors

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {
    
    private val audioRecorder = MyAudioRecorder()
    
    init {
        Log.d(TAG, "init: ")
    }
    
    private val _isRecording = MutableStateFlow(false)
    val isRecording get() = _isRecording.asStateFlow()
    
    private fun startRecording() {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _isRecording.value = true
            }
            audioRecorder.startRecording()
            _isRecording.value = false
        }
    }
    
    fun stopRecording() {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _isRecording.value = false
            }
            audioRecorder.stopRecording()
        }
    }
    
    fun onClick() {
        if (_isRecording.value)
            stopRecording()
        else
            startRecording()
    }
    
    companion object {
        private const val TAG = "MainViewModel"
    }
}