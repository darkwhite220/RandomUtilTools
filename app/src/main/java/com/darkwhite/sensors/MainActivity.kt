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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darkwhite.sensors.ui.theme.SensorsTheme
import kotlin.math.round

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
    private var x: Float by mutableFloatStateOf(0f)
    private var y: Float by mutableFloatStateOf(0f)
    private var z: Float by mutableFloatStateOf(0f)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        
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
                        x = x, y = y, z = z
                    )
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        accelerometer?.let { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }
    
    override fun onPause() {
        super.onPause()
        accelerometer?.let { accelerometer ->
            sensorManager.unregisterListener(
                this,
                accelerometer
            )
        }
    }
    
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            x = event.values[0]
            y = event.values[1]
            z = event.values[2]
            println("onSensorChanged: $x, $y, $z")
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        println("onAccuracyChanged: $accuracy, $sensor")
    }
    
}

fun isSurfaceLeveled(pitch: Float, roll: Float): Boolean {
    """Checks if the surface is leveled.

  Args:
    pitch: The pitch of the phone in degrees.
    roll: The roll of the phone in degrees.

  Returns:
    True if the surface is leveled, False otherwise.
  """
    
    return Math.abs(pitch) < 1 && Math.abs(roll) < 1
}

fun calculatePitchAndRoll(accelerometerSensorOutput: FloatArray): FloatArray {
    """Calculates the pitch and roll of the phone using the accelerometer sensor output.

  Args:
    accelerometerSensorOutput: A FloatArray containing the accelerometer sensor output.

  Returns:
    A FloatArray containing the pitch and roll of the phone in degrees.
  """
    
    val pitch = Math.atan2(
        accelerometerSensorOutput[1].toDouble(),
        accelerometerSensorOutput[2].toDouble()
    ) * 180 /
        Math.PI
    val roll = Math.atan2(
        accelerometerSensorOutput[0].toDouble(),
        accelerometerSensorOutput[2].toDouble()
    ) * 180 / Math.PI
    
    return floatArrayOf(pitch.toFloat(), roll.toFloat())
}

@Composable
fun MainScreen(
    sensorManager: SensorManager,
    accelerometer: Sensor?,
    x: Float,
    y: Float,
    z: Float,
    modifier: Modifier = Modifier,
) {
//    val deviceSensors = remember { mutableListOf(sensorManager.getSensorList(Sensor.TYPE_ALL)) }
    val sBuilder by remember(x,y,z) { derivedStateOf {
        val (pitch, roll) = calculatePitchAndRoll(floatArrayOf(x, y, z))
        val (pitch1, roll1) = calculatePitchAndRoll(floatArrayOf(x, z, y))
        val (pitch2, roll2) = calculatePitchAndRoll(floatArrayOf(z, y, x))
        """
            X=$x
            Y=$y
            Z=$z
            
            ${(x / MAX_VALUE * 100).round(2)}%, ${(y / MAX_VALUE * 100).round(2)}%, ${(z /
            MAX_VALUE * 100).round(2)}%
            
            Flat Leveled: ${isSurfaceLeveled(pitch, roll)}, ${pitch.round(2)}, ${roll.round(2)}
            Up Leveled  : ${isSurfaceLeveled(pitch1, roll1)}, ${pitch1.round(2)}, ${roll1.round(2)}
            Side Leveled: ${isSurfaceLeveled(pitch2, roll2)}, ${pitch2.round(2)}, ${roll2.round(2)}
        """.trimIndent()
    } }
    
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center
    ) {
        if (accelerometer != null) {
            Text(text = sBuilder, style = MaterialTheme.typography.titleLarge)
        } else {
            Text(text = "No Accelerometer Available")
        }

//        Text(text = "Available Sensors", style = MaterialTheme.typography.titleLarge)
//        deviceSensors[0].forEach { sensor ->
//            Text(text = "$sensor")
//        }
    }
}

fun Float.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}