package com.example.prago.composables.resultScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.prago.dataClasses.ColorStruct
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun TripNameRow(text: String, color: Color){
    Text(
        text = text,
        style = TextStyle(
            color = color, // Apply the color here
            fontWeight = FontWeight.Bold,
            fontSize = lineNameSize.sp
        )
    )
}

@Composable
fun LineRow(lineName: String, colorStruct: ColorStruct) {
    val text = "Line $lineName"
    val color = Color(colorStruct.r, colorStruct.g, colorStruct.b)
    TripNameRow(text = text, color = color)
}

@Composable
fun StopRow(stopName: String, time: LocalDateTime){
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss") // Define the desired format
    val formattedTime = time.format(formatter) // Format the LocalDateTime using the formatter

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = stopName,
            style = stopNameStyle,
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
        Text(
            text = formattedTime,
            style = stopNameStyle,
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}