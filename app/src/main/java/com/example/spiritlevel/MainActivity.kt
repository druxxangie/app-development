package com.example.spiritlevel

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spiritlevel.ui.theme.BrightLila
import com.example.spiritlevel.ui.theme.SpiritLevelTheme

class MainActivity : ComponentActivity(), SensorEventListener {

    // Sensor manager to manage the device sensors
    private lateinit var sensorManager: SensorManager

    // Accelerometer sensor to measure the device's acceleration
    private var accelerometer: Sensor? = null

    // Mutable states to hold the x and y values of the accelerometer
    private var _x = mutableFloatStateOf(0f)
    private var _y = mutableFloatStateOf(0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the sensor manager and the accelerometer sensor
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)

        // Set the content of the activity
        setContent {
            SpiritLevelTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SpiritLevelView(_x.floatValue, _y.floatValue)
                }
            }
        }
    }

    // Called when sensor values have changed
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            // Update the x and y values based on the accelerometer readings
            _x.floatValue = it.values[0]
            _y.floatValue = it.values[1]
        }
    }

    // Called when the accuracy of the sensor has changed
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    // Unregister the sensor listener when the activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }
}

@Composable
fun SpiritLevelView(x: Float, y: Float) {
    // Define constants for the bubble radius, maximum offset, and level threshold
    val bubbleRadius = 20.dp
    val maxOffset = 100.dp
    val levelThreshold = 5.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw the background with alternating stripes
            val stripeHeight = 10.dp.toPx()
            val stripeColors = listOf(Color.LightGray.copy(alpha = 0.3f), BrightLila)
            for (i in 0 until size.height.toInt() step (stripeHeight * 2).toInt()) {
                drawRect(
                    color = stripeColors[0],
                    topLeft = Offset(0f, i.toFloat()),
                    size = size.copy(height = stripeHeight)
                )
                drawRect(
                    color = stripeColors[1],
                    topLeft = Offset(0f, i.toFloat() + stripeHeight),
                    size = size.copy(height = stripeHeight)
                )
            }

            // Draw the level zone
            drawRoundRect(
                color = Color.Green.copy(alpha = 0.3f),
                topLeft = Offset(this.center.x - levelThreshold.toPx(), this.center.y - levelThreshold.toPx()),
                size = this.size.copy(width = 2 * levelThreshold.toPx(), height = 2 * levelThreshold.toPx()),
                cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
            )

            // Draw the axes
            drawLine(
                color = Color.Black,
                start = Offset(this.center.x, 0f),
                end = Offset(this.center.x, this.size.height),
                strokeWidth = 4.dp.toPx()
            )
            drawLine(
                color = Color.Black,
                start = Offset(0f, this.center.y),
                end = Offset(this.size.width, this.center.y),
                strokeWidth = 4.dp.toPx()
            )

            // Calculate the offset for the bubble
            val offsetX = (x / 9.81f * maxOffset.toPx()).coerceIn(-maxOffset.toPx(), maxOffset.toPx())
            val offsetY = (y / 9.81f * maxOffset.toPx()).coerceIn(-maxOffset.toPx(), maxOffset.toPx())

            // Draw the bubble with ombre effect
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Cyan, Color.Blue),
                    center = Offset(this.center.x - offsetX, this.center.y + offsetY),
                    radius = bubbleRadius.toPx()
                ),
                radius = bubbleRadius.toPx(),
                center = Offset(this.center.x - offsetX, this.center.y + offsetY)
            )

            // Draw the white border around the bubble
            drawCircle(
                color = Color.White,
                radius = bubbleRadius.toPx(),
                center = Offset(this.center.x - offsetX, this.center.y + offsetY),
                style = Stroke(width = 4.dp.toPx())
            )
        }

        // Display the numerical values
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "X: %.2f°".format(x), fontSize = 24.sp, color = Color.Black, modifier = Modifier.padding(4.dp))
            Text(text = "Y: %.2f°".format(y), fontSize = 24.sp, color = Color.Black, modifier = Modifier.padding(4.dp))
        }

        // Add axis labels
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                text = "X",
                fontSize = 40.sp,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 24.dp, top = 48.dp)
            )
            Text(
                text = "Y",
                fontSize = 40.sp,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp, end = 36.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SpiritLevelPreview() {
    SpiritLevelTheme {
        SpiritLevelView(0f, 0f)
    }
}
