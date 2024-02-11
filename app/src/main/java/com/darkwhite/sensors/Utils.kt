package com.darkwhite.sensors

import kotlin.math.abs
import kotlin.math.atan2

object Utils {
    
    
    fun isSurfaceLeveled(pitch: Float, roll: Float): Boolean {
        """Checks if the surface is leveled.

  Args:
    pitch: The pitch of the phone in degrees.
    roll: The roll of the phone in degrees.

  Returns:
    True if the surface is leveled, False otherwise.
  """
        
        return abs(pitch) < 1 && abs(roll) < 1
    }
    
    fun calculatePitchAndRoll(accelerometerSensorOutput: FloatArray): FloatArray {
        """Calculates the pitch and roll of the phone using the accelerometer sensor output.

  Args:
    accelerometerSensorOutput: A FloatArray containing the accelerometer sensor output.

  Returns:
    A FloatArray containing the pitch and roll of the phone in degrees.
  """
        
        val pitch = atan2(
            accelerometerSensorOutput[1].toDouble(),
            accelerometerSensorOutput[2].toDouble()
        ) * 180 /
            Math.PI
        val roll = atan2(
            accelerometerSensorOutput[0].toDouble(),
            accelerometerSensorOutput[2].toDouble()
        ) * 180 / Math.PI
        
        return floatArrayOf(pitch.toFloat(), roll.toFloat())
    }
    
    fun Float.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return kotlin.math.round(this * multiplier) / multiplier
    }
    
    // Function to calculate distance based on altitude
    fun calculateDistanceFromAltitude(altitude: Float): Float {
        // Your distance calculation logic goes here
        // This might involve using some standard atmospheric models
        // or other methods to convert altitude to distance
        // For simplicity, you can use a linear approximation or lookup table
        
        // Example: Linear approximation for demonstration purposes
        // You may need to adjust these coefficients based on your specific use case
        val slope = 0.0065f  // Standard lapse rate for troposphere
        return altitude * slope
    }
}