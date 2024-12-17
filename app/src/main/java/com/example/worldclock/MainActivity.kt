package com.example.worldclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.drawText
import com.example.worldclock.ui.theme.WorldClockTheme
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime
import androidx.compose.runtime.*
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem

import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.platform.LocalContext



import androidx.compose.material3.Text
import androidx.compose.runtime.*


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas


import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalConfiguration
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.ui.Alignment
import java.time.LocalTime
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.PI


private val timeZones = listOf("America/Los_Angeles", "America/New_York", "Asia/Shanghai", "Europe/London")
private val timeZoneNames = listOf("Los Angeles", "New York", "Beijing", "London")


@Composable
fun AnalogClock(modifier: Modifier = Modifier, zoneId: ZoneId = ZoneId.systemDefault()) {
    var currentTime by remember { mutableStateOf(LocalTime.now(zoneId)) }

    LaunchedEffect(zoneId) {
        while (true) {
            currentTime = LocalTime.now(zoneId)
            kotlinx.coroutines.delay(1000)
        }
    }

    Canvas(modifier = modifier.size(200.dp)) { // Ensure the canvas is circular
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = minOf(centerX, centerY) - 10.dp.toPx()

        // Draw clock face
        drawCircle(
            color = Color.Black,
            center = Offset(centerX, centerY),
            radius = radius,
            style = Stroke(width = 5.dp.toPx())
        )


        drawClockNumbers(centerX,centerY, radius)

        // Hour hand
        val hourAngle = Math.PI * ((currentTime.hour % 12 + currentTime.minute / 60.0) / 6) - Math.PI / 2
        drawLine(
            color = Color.Black,
            start = Offset(centerX, centerY),
            end = Offset(
                centerX + radius * 0.5f * cos(hourAngle).toFloat(),
                centerY + radius * 0.5f * sin(hourAngle).toFloat()
            ),
            strokeWidth = 8.dp.toPx()
        )

        // Minute hand
        val minuteAngle = Math.PI * (currentTime.minute / 30.0) - Math.PI / 2
        drawLine(
            color = Color.Black,
            start = Offset(centerX, centerY),
            end = Offset(
                centerX + radius * 0.7f * cos(minuteAngle).toFloat(),
                centerY + radius * 0.7f * sin(minuteAngle).toFloat()
            ),
            strokeWidth = 5.dp.toPx()
        )

        // Second hand
        val secondAngle = Math.PI * (currentTime.second / 30.0) - Math.PI / 2
        drawLine(
            color = Color.Red,
            start = Offset(centerX, centerY),
            end = Offset(
                centerX + radius * 0.8f * cos(secondAngle).toFloat(),
                centerY + radius * 0.8f * sin(secondAngle).toFloat()
            ),
            strokeWidth = 2.dp.toPx()
        )
    }
}

fun DrawScope.drawClockNumbers(centerX: Float, centerY: Float, radius: Float) {
    val numberRadius = radius * 0.85f  // Numbers are drawn inside the clock face
    val paint = Paint().apply {
        color = android.graphics.Color.BLACK
        textSize = 40f  // Text size in pixels
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    val textMetrics = paint.getFontMetrics()  // Get the FontMetrics object
    val textHeight = (textMetrics.ascent + textMetrics.descent) / 2  // Calculate the height to adjust

    drawIntoCanvas { canvas ->
        for (number in 1..12) {
            val angle = PI / 6 * (number - 3)  // Calculate the angle for each number
            val x = (centerX + cos(angle) * numberRadius).toFloat()
            val y = (centerY + sin(angle) * numberRadius).toFloat()

            canvas.nativeCanvas.drawText(
                number.toString(),
                x,
                y - textHeight,  // Adjust for vertical centering using text metrics
                paint
            )
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WorldClockTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    // Applying innerPadding to the content inside Scaffold
                    Box(modifier = Modifier.padding(innerPadding)) {
                        ResponsiveClocksLayout()
                    }
                }
            }
        }
    }
}

@Composable
fun ResponsiveClocksLayout() {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ClockAndTimeZone("Clock 1", "America/Los_Angeles")
            ClockAndTimeZone("Clock 2", "Asia/Shanghai")
        }
    } else {
        Column(
            modifier = Modifier.fillMaxHeight().fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            ClockAndTimeZone("Clock 1", "America/Los_Angeles")
            ClockAndTimeZone("Clock 2", "Asia/Shanghai")
        }
    }
}

@Composable
fun ClockAndTimeZone(label: String, zoneId: String) {
    var currentZone by remember { mutableStateOf(ZoneId.of(zoneId)) }

    Row(modifier = Modifier
        .width(300.dp)  // Sets the width of the Row to 300.dp
        .padding(horizontal = 16.dp), // Adds horizontal padding
        verticalAlignment = Alignment.CenterVertically) {
        TimeZoneDisplay(
            label = label,
            zoneId = currentZone,
            onZoneChanged = { newZone -> currentZone = newZone },
            modifier = Modifier.weight(1f) // Takes 1/3 of the space
        )

    }

}

@Composable
fun TimeZoneDisplay(label: String, zoneId: ZoneId, onZoneChanged: (ZoneId) -> Unit, modifier: Modifier = Modifier) {
    var selectedZone by remember { mutableStateOf(zoneId) }
    Column(modifier = modifier.padding(8.dp)) {
//    Text(label, style = MaterialTheme.typography.titleLarge)

        TimeZoneSelector(
            modifier = Modifier.fillMaxWidth(),
            initialZone = selectedZone,
            onZoneSelected = { newZone ->
                selectedZone = newZone
                onZoneChanged(newZone)  // Notify the parent composable of the change
            }
        )

        AnalogClock(modifier = Modifier.fillMaxWidth(), zoneId = selectedZone)


        ShowTime(Modifier.padding(top = 8.dp), selectedZone)
    }
}

@Composable
fun ShowTime(modifier: Modifier = Modifier, zoneId: ZoneId = ZoneId.systemDefault()) {
    var time by remember { mutableStateOf(LocalDateTime.now(zoneId)) }

    LaunchedEffect(zoneId) {
        while (true) {
            time = LocalDateTime.now(zoneId)
            kotlinx.coroutines.delay(1000)
        }
    }

    val timeStr = time.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier  = Modifier.fillMaxWidth()
    ) {
        Text(
            text = timeStr,
            modifier = modifier,  // Apply the modifier passed to showTime
            style = TextStyle(
                color = Color.Black,
                fontSize = 30.sp,  // Increase the font size here
                fontWeight = FontWeight.Bold  // Make the font weight bold for better visibility
            )
        )
        Text(
            text = time.format(DateTimeFormatter.ofPattern("M/d/yyyy"))
        )
        Text(
            text = "GMT ${time.atZone(zoneId).offset.id.replace("Z", "+00:00")}"
        )
    }
}

@Composable
fun TimeZoneSelector(modifier: Modifier = Modifier, initialZone: ZoneId, onZoneSelected: (ZoneId) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedZone by remember { mutableStateOf(initialZone) }
    var selectedZoneName by remember { mutableStateOf(timeZoneNames[timeZones.indexOf(initialZone.id)]) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = selectedZoneName,
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(16.dp)
                .fillMaxWidth(),  // Ensures the Text and DropdownMenu are aligned
            style = TextStyle(
                color = Color.Black,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(200.dp)  // Match the width of the Text
        ) {
            timeZones.zip(timeZoneNames).forEach { (zoneId, name) ->
                DropdownMenuItem(
                    onClick = {
                        selectedZoneName = name
                        selectedZone = ZoneId.of(zoneId)
                        onZoneSelected(selectedZone)
                        expanded = false
                    },
                    text = { Text(name) }
                )
            }
        }
    }
}


@Preview(showBackground = true, name = "showTime Preview - Paris Time")
@Composable
fun ShowTimePreviewParis() {
    WorldClockTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            // Applying innerPadding to the content inside Scaffold
            Box(modifier = Modifier.padding(innerPadding)) {
                ResponsiveClocksLayout()
            }
        }
    }
}

val sampleTimeZones = listOf("America/New_York", "Europe/Berlin", "Asia/Tokyo")

@Preview(showBackground = true, widthDp = 320, heightDp = 480)
@Composable
fun PreviewDropdownMenu() {
    // Context is necessary for real implementations, not used directly here
    val context = LocalContext.current

    // Always expanded for preview purposes
    val expanded = remember { true }

    Box(modifier = Modifier.padding(16.dp)) {
        Column {
            Text(text = "Selected Time Zone", modifier = Modifier.padding(bottom = 8.dp))
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {},
                modifier = Modifier
            ) {
                sampleTimeZones.forEach { zone ->
                    DropdownMenuItem(
                        onClick = { /* Handle selection */ },
                        text = { Text(zone) }
                    )
                }
            }
        }
    }
}