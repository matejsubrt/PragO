package com.example.prago.view.resultScreen.segmentCards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prago.R
import com.example.prago.model.dataClasses.searchResult.UsedBikeTrip
import com.example.prago.model.dataClasses.searchResult.UsedTrip
import com.example.prago.utils.formatting.formatTime
import com.example.prago.view.resultScreen.colorNextbike
import com.example.prago.view.resultScreen.delayTextStyle
import com.example.prago.view.resultScreen.lineNameSize
import com.example.prago.view.resultScreen.stopNameStyle
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs


@Composable
fun TripNameRow(
    text: String,
    color: Color,
    hasDelayData: Boolean,
    currentTripDelay: Int,
    hasBikeCountData: Boolean,
    bikeCount: Int
){
    val delayPositive = currentTripDelay >= 0
    val delayTextFormatted = formatTime(abs(currentTripDelay).toLong())
    val sign = if (delayPositive) "+" else "-"
    val delayText = sign + delayTextFormatted

    val delayBoxColor =
        if (currentTripDelay > 30) Color.Red
        else if (currentTripDelay >= 0) Color.Green
        else Color.Yellow

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
        if (hasDelayData) {
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
        } else if (hasBikeCountData) {
            Box(
                modifier = Modifier
                    .background(Color(0xFF888888), RoundedCornerShape(16.dp))
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ){
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(
                        painter = painterResource(id = R.drawable.bike),
                        contentDescription = null,
                        tint = colorNextbike,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(all = 4.dp)
                    )
                    Text(
                        text = "${bikeCount}",
                        color = colorNextbike,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun LineRow(trip: UsedTrip){
    val lineName = trip.routeName
    val lineNameFormatted = stringResource(R.string.line) + " " + lineName

    val lineColor = when(trip.vehicleType){
        0 -> Color(0x7a, 0x06, 0x03) // Tram
        1 -> { // Subway
            when(trip.routeName){
                "A" -> Color(0, 165, 98)
                "B" -> Color(248, 179, 34)
                "C" -> Color(207, 0, 61)
                else -> Color(0, 0, 0)
            }
        }
        2 -> Color(37, 30, 98) // Train
        3 -> Color(0x00, 0x7d, 0xa8) // Bus
        4 -> Color(0x00, 0xb3, 0xcb) // Ferry
        5 -> Color(122, 6, 3) // Cable tram
        6 -> Color(0x00, 0x00, 0xff) // Aerial lift
        7 -> Color(0x00, 0x00, 0xff) // Funicular
        11 -> Color(0x00, 0x7d, 0xa8) // Trolleybus
        12 -> Color(37, 30, 98) // Monorail
        else -> Color(0x00, 0x7d, 0xa8) // Shouldn't happen
    }


    //val lineColor = Color(trip.color.r, trip.color.g, trip.color.b)
    val hasDelayData = trip.hasDelayInfo.value
    val currentTripDelay = trip.currentDelay.value




    TripNameRow(
        text = lineNameFormatted,
        color = lineColor,
        hasDelayData = hasDelayData,
        currentTripDelay = currentTripDelay,
        hasBikeCountData = false,
        bikeCount = 0
    )
}

@Composable
fun BikeServiceRow(trip: UsedBikeTrip){
    val bikeCount = trip.remainingBikes

    TripNameRow(
        text = stringResource(R.string.nextbike), // TODO: get bike service name from API
        color = colorNextbike,
        hasDelayData = false,
        currentTripDelay = 0,
        hasBikeCountData = true,
        bikeCount = bikeCount
    )

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
            color = Color.White
        )
        Text(
            text = formattedTime,
            style = stopNameStyle,
            color = Color.White
        )
    }
}