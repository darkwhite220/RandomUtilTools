package com.darkwhite.sensors.audio

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat


@Composable
fun AudioUi(
    isRecording: Boolean,
    onClick: () -> Unit,
) {
    var showRequestAudioPermission by remember { mutableStateOf(false) }
    var initRequestAudioPermission by remember { mutableStateOf(false) }
    val requestAudioPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        showRequestAudioPermission = !it
        println("requestAudioPermission response: $it")
    }
    showRequestAudioPermission = ActivityCompat.checkSelfPermission(
        LocalContext.current,
        Manifest.permission.RECORD_AUDIO
    ) != PackageManager.PERMISSION_GRANTED
    
    Text(text = "Audio")
    if (showRequestAudioPermission) {
        Button(onClick = { initRequestAudioPermission = true }) {
            Text(text = "Request audio permission")
        }
    } else {
        Button(onClick = onClick) {
            Text(text = if (isRecording) "Stop" else "Start")
        }
    }
    
    if (initRequestAudioPermission) {
        requestAudioPermission.launch(Manifest.permission.RECORD_AUDIO)
        initRequestAudioPermission = false
    }
}
