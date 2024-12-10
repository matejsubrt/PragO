package com.example.prago.composables.resultScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prago.R
import com.example.prago.model.dataClasses.ColorStruct
import com.example.prago.formatters.formatTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs


@Composable
fun TripNameRow(text: String, color: Color, hasDelayData: Boolean, currentTripDelay: Int){
    val delayPositive = currentTripDelay >= 0
    val delayTextFormatted = formatTime(abs(currentTripDelay).toLong())
    val sign = if (delayPositive) "+" else "-"
    val delayText = sign + delayTextFormatted

    val delayBoxColor = if(currentTripDelay > 30) Color.Red else if(currentTripDelay >= 0) Color.Green else Color.Yellow

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = text,
            style = TextStyle(
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = lineNameSize.sp
            )
        )
        if(hasDelayData){
            Box(
                modifier = Modifier
                    .background(delayBoxColor, shape = RoundedCornerShape(4.dp))
                    .padding(start = 2.dp, end = 2.dp, top = 1.dp, bottom = 1.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = delayText,
                    style = delayTextStyle
                )
            }

        }
    }
}

@Composable
fun LineRow(lineName: String, colorStruct: ColorStruct, hasDelayData: Boolean, currentTripDelay: Int) {
    val text = stringResource(R.string.line) + " " + lineName
    val color = Color(colorStruct.r, colorStruct.g, colorStruct.b)
    TripNameRow(text = text, color = color, hasDelayData = hasDelayData, currentTripDelay = currentTripDelay)
}

@Composable
fun StopRow(stopName: String, time: LocalDateTime){
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    val formattedTime = time.format(formatter)

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

@Preview
@Composable
fun PreviewLineRow() {
    LineRow(
        lineName = "123",
        colorStruct = ColorStruct(98, 0, 238),
        hasDelayData = true,
        currentTripDelay = 10
    )
}