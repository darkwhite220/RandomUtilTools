package com.darkwhite.sensors

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener

class MySensorEventListener : SensorEventListener {
    
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            println(
                "onSensorChanged: x=${event.values[0]}, y=${event.values[1]}, z=${event.values[2]}"
            )
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        println("onAccuracyChanged: $accuracy, $sensor")
    }
}