package com.darkwhite.sensors.sensors

import android.hardware.Sensor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.darkwhite.sensors.MAX_VALUE
import com.darkwhite.sensors.Utils
import com.darkwhite.sensors.Utils.round


@Composable
fun SensorsUi(
    x: Float,
    y: Float,
    z: Float,
    accelerometer: Sensor?,
    lightSensor: Sensor?,
    lightLevel: Float,
    sensorsList: List<String>
) {
    val sBuilder by remember(x, y, z) {
        derivedStateOf {
            val (pitch, roll) = Utils.calculatePitchAndRoll(floatArrayOf(x, y, z))
            val (pitch1, roll1) = Utils.calculatePitchAndRoll(floatArrayOf(x, z, y))
            val (pitch2, roll2) = Utils.calculatePitchAndRoll(floatArrayOf(z, y, x))
            """
            X=$x
            Y=$y
            Z=$z
            
            ${(x / MAX_VALUE * 100).round(2)}%, ${(y / MAX_VALUE * 100).round(2)}%, ${
                (z /
                    MAX_VALUE * 100).round(2)
            }%
            
            Flat Leveled: ${
                Utils.isSurfaceLeveled(
                    pitch,
                    roll
                )
            }, ${pitch.round(2)}, ${roll.round(2)}
            Up Leveled  : ${
                Utils.isSurfaceLeveled(
                    pitch1,
                    roll1
                )
            }, ${pitch1.round(2)}, ${roll1.round(2)}
            Side Leveled: ${
                Utils.isSurfaceLeveled(
                    pitch2,
                    roll2
                )
            }, ${pitch2.round(2)}, ${roll2.round(2)}
        """.trimIndent()
        }
    }
    
    if (accelerometer != null) {
        Text(text = sBuilder, style = MaterialTheme.typography.titleLarge)
    } else {
        Text(text = "No Accelerometer Available")
    }
    if (lightSensor != null) {
        Text(
            text = "LightSensor available = $lightLevel",
            style = MaterialTheme.typography.titleLarge
        )
    } else {
        Text(text = "No LightSensor Available")
    }
    sensorsList.forEach {
        Text(text = it)
    }
}
