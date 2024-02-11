package com.darkwhite.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.darkwhite.sensors.Utils.calculateDistanceFromAltitude
import com.darkwhite.sensors.Utils.calculatePitchAndRoll
import com.darkwhite.sensors.Utils.isSurfaceLeveled
import com.darkwhite.sensors.Utils.round
import com.darkwhite.sensors.ui.theme.SensorsTheme

const val MAX_VALUE = 9.81f

// https://github.com/josejuansanchez/android-sensors-overview/blob/master/README.old.md
class MainActivity : ComponentActivity(), SensorEventListener {
    /**
     * my custom sensor listener wont unregister :(
     * Have to use this.
     * Tried with: this.getSystemService...
     */
    
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lightSensor: Sensor? = null
    private var x: Float by mutableFloatStateOf(0f)
    private var y: Float by mutableFloatStateOf(0f)
    private var z: Float by mutableFloatStateOf(0f)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        // None reliable range 0-5 no light or any light
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        
        
        // Get a list of all available sensors
        val sensorList: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
        
        // Print the names of all sensors
        for (sensor in sensorList) {
            println("Sensor Name: ${sensor.name}, Type: ${sensor.type}")
        }
        
        setContent {
            SensorsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        sensorManager = sensorManager,
                        accelerometer = accelerometer,
                        x = x, y = y, z = z,
                        lightSensor = lightSensor,
                    )
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
//        accelerometer?.let { accelerometer ->
//            sensorManager.registerListener(
//                this,
//                accelerometer,
//                SensorManager.SENSOR_DELAY_NORMAL
//            )
//        }
//        lightSensor?.let { lightSensor ->
//            sensorManager.registerListener(
//                this,
//                lightSensor,
//                SensorManager.SENSOR_DELAY_NORMAL
//            )
//        }
    }
    
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
    
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            println("event.values[0] ${event.sensor.type} ${event.values[0]}")
//            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
//                x = event.values[0]
//                y = event.values[1]
//                z = event.values[2]
//                println("onSensorChanged: $x, $y, $z")
//            }
//            if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
//                // Handle light sensor data
//                val lightLevel = event.values[0]
//                println("lightLevel $lightLevel")
//            }
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        println("onAccuracyChanged: $accuracy, $sensor")
    }
    
}

@Composable
fun MainScreen(
    sensorManager: SensorManager,
    accelerometer: Sensor?,
    x: Float,
    y: Float,
    z: Float,
    lightSensor: Sensor?,
    modifier: Modifier = Modifier,
) {
    val sBuilder by remember(x, y, z) {
        derivedStateOf {
            val (pitch, roll) = calculatePitchAndRoll(floatArrayOf(x, y, z))
            val (pitch1, roll1) = calculatePitchAndRoll(floatArrayOf(x, z, y))
            val (pitch2, roll2) = calculatePitchAndRoll(floatArrayOf(z, y, x))
            """
            X=$x
            Y=$y
            Z=$z
            
            ${(x / MAX_VALUE * 100).round(2)}%, ${(y / MAX_VALUE * 100).round(2)}%, ${
                (z /
                    MAX_VALUE * 100).round(2)
            }%
            
            Flat Leveled: ${isSurfaceLeveled(pitch, roll)}, ${pitch.round(2)}, ${roll.round(2)}
            Up Leveled  : ${isSurfaceLeveled(pitch1, roll1)}, ${pitch1.round(2)}, ${roll1.round(2)}
            Side Leveled: ${isSurfaceLeveled(pitch2, roll2)}, ${pitch2.round(2)}, ${roll2.round(2)}
        """.trimIndent()
        }
    }
    
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center
    ) {
        if (accelerometer != null) {
            Text(text = sBuilder, style = MaterialTheme.typography.titleLarge)
        } else {
            Text(text = "No Accelerometer Available")
        }
        if (lightSensor != null) {
            Text(text = "LightSensor available", style = MaterialTheme.typography.titleLarge)
        } else {
            Text(text = "No LightSensor Available")
        }
    }
}