package com.example.prago.composables.resultScreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prago.R
import com.example.prago.model.dataClasses.ConnectionSearchResult
import com.example.prago.formatters.formatDurationTime
import com.example.prago.formatters.formatTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.min


@Composable
fun TotalTime(
    departureTime: LocalDateTime,
    arrivalTime: LocalDateTime,
    modifier: Modifier = Modifier
) {
    val duration = Duration.between(departureTime, arrivalTime)
    Text(
        text = formatDurationTime(time = duration.seconds),
        modifier = modifier,
        style = TextStyle(
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = headerTextSize.sp
        )
    )
}

@Composable
fun IconCountDownBlock(
    size: Int,
    fontWeight: FontWeight,
    color: Color,
    text: String,
    iconId: Int
){
    Row(
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = null,
            modifier = Modifier.size(size.dp),
            tint = color
        )
        Text(
            text = text,
            style = TextStyle(
                color = color,
                fontWeight = fontWeight,
                fontSize = size.sp
            )
        )
    }
}


@Composable
fun CountDown(
    firstTripDepartureTime: LocalDateTime,
    connectionDepartureTime: LocalDateTime,
    modifier: Modifier = Modifier
) {
    var timeLeftTillFirstTrip by remember { mutableStateOf(Duration.ZERO) }
    var displaySecondsTillFirstTrip by remember { mutableStateOf(false) }
    var departedFirstTrip by remember { mutableStateOf(false) }

    var timeLeftTillConnectionStart by remember { mutableStateOf(Duration.ZERO) }
    var displaySecondsTillConnectionStart by remember { mutableStateOf(false) }
    var departedConnectionStart by remember { mutableStateOf(false) }

    val startsWithTrip: Boolean
    if(firstTripDepartureTime == connectionDepartureTime){
        startsWithTrip = true
    } else {
        startsWithTrip = false
    }

    LaunchedEffect(firstTripDepartureTime) { // Trigger LaunchedEffect when departureTime changes
        flow {
            while (true) {
                val currentTime = LocalDateTime.now()
                timeLeftTillFirstTrip = Duration.between(currentTime, firstTripDepartureTime)
                displaySecondsTillFirstTrip = timeLeftTillFirstTrip.seconds < 600
                departedFirstTrip = timeLeftTillFirstTrip <= Duration.ZERO
                emit(Unit)
                delay(1000)
            }
        }.collect { }
    }


    LaunchedEffect(connectionDepartureTime){
        flow {
            while (true) {
                val currentTime = LocalDateTime.now()
                timeLeftTillConnectionStart = Duration.between(currentTime, connectionDepartureTime)
                displaySecondsTillConnectionStart = timeLeftTillConnectionStart.seconds < 600
                departedConnectionStart = timeLeftTillConnectionStart <= Duration.ZERO
                emit(Unit)
                delay(1000)
            }
        }.collect { }
    }

    val formattedTimeTillFirstTrip = if (departedFirstTrip) {
        stringResource(R.string.departed)
    }
    else {
        stringResource(R.string.in_time) + " " + formatTime(timeLeftTillFirstTrip.seconds)
    }

    val formattedTimeTillConnectionStart = if (departedConnectionStart) {
        "-" + formatTime(timeLeftTillConnectionStart.abs().seconds)
    }
    else {
        stringResource(R.string.in_time) + " " + formatTime(timeLeftTillConnectionStart.seconds)
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom
    ){
        val walkStartSize = if (departedConnectionStart) headerTextSizeSmaller else headerTextSize
        val tripStartSize = if (departedConnectionStart) headerTextSize else headerTextSizeSmaller
        val walkStartColor = if (departedConnectionStart) Color(0xFFE0E0E0) else MaterialTheme.colorScheme.onBackground
        val tripStartColor = if (departedConnectionStart){
            if(departedFirstTrip){
                Color(0xFFE0E0E0)
            } else {
                Color(0xFFF28C28)
            }
        } else {
            if(departedFirstTrip){
                Log.e("CountDown", "Impossible for the first trip to be departed, but connection not")
                Color.Red
            } else {
                MaterialTheme.colorScheme.onBackground
            }
        }
        val walkStartFontWeight = if (departedConnectionStart) FontWeight.Normal else FontWeight.Bold
        val tripStartFontWeight = if (departedFirstTrip || !departedConnectionStart) FontWeight.Normal else FontWeight.Bold

        if(!startsWithTrip && !departedFirstTrip){
            if(!departedConnectionStart){
                IconCountDownBlock(
                    size = walkStartSize,
                    fontWeight = walkStartFontWeight,
                    color = walkStartColor,
                    text = " " + formattedTimeTillConnectionStart,
                    iconId = R.drawable.walk
                )

                Spacer(modifier = Modifier.size(8.dp))

                IconCountDownBlock(
                    size = tripStartSize,
                    fontWeight = tripStartFontWeight,
                    color = tripStartColor,
                    text = " " + formattedTimeTillFirstTrip,
                    iconId = R.drawable.bus
                )
            } else {
                IconCountDownBlock(
                    size = tripStartSize,
                    fontWeight = tripStartFontWeight,
                    color = tripStartColor,
                    text = " " + formattedTimeTillFirstTrip,
                    iconId = R.drawable.bus
                )
                Spacer(modifier = Modifier.size(8.dp))
                IconCountDownBlock(
                    size = walkStartSize,
                    fontWeight = walkStartFontWeight,
                    color = walkStartColor,
                    text = " " + formattedTimeTillConnectionStart,
                    iconId = R.drawable.walk
                )
            }
        } else {
            val fontWeight = if (departedFirstTrip) FontWeight.Normal else FontWeight.Bold
            val color = if (departedFirstTrip) Color(0xFFE0E0E0) else MaterialTheme.colorScheme.onBackground

            IconCountDownBlock(
                size = headerTextSize,
                fontWeight = fontWeight,
                color = color,
                text = " " + formattedTimeTillFirstTrip,
                iconId = R.drawable.bus
            )
        }
    }
}

@Composable
fun Anytime(modifier: Modifier){
    Text(
        text = stringResource(id = R.string.departure_anytime),
        modifier = modifier,
        style = TextStyle(
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            fontSize = headerTextSize.sp
        )
    )
}


@Composable
@Preview
fun CountDownPreview() {
    Surface {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CountDown(LocalDateTime.now().plusMinutes(5), LocalDateTime.now().plusMinutes(2))
        }
    }
}


@Composable
fun ResultHeader(
    result: ConnectionSearchResult,
    firstTripAltIndex: Int,
    lastTripAltIndex: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if(result.usedSegmentTypes.contains(1)){
            val firstIndex = min(firstTripAltIndex, result.usedTripAlternatives.first().alternatives.size - 1)
            val firstTrip = result.usedTripAlternatives.first().alternatives[firstIndex]
            val firstTripDepartureTime = firstTrip.stopPasses[firstTrip.getOnStopIndex].departureTime

            val lastIndex = min(lastTripAltIndex, result.usedTripAlternatives.last().alternatives.size - 1)
            val lastTrip = result.usedTripAlternatives.last().alternatives[lastIndex]
            val lastTripArrivalTime = lastTrip.stopPasses[lastTrip.getOffStopIndex].arrivalTime

            val departureTime = firstTripDepartureTime.minusSeconds(result.secondsBeforeFirstTrip.toLong())
            val arrivalTime = lastTripArrivalTime.plusSeconds(result.secondsAfterLastTrip.toLong())

            CountDown(firstTripDepartureTime, departureTime, Modifier.weight(1f))
            TotalTime(departureTime = departureTime, arrivalTime = arrivalTime)
        }
        else{
            Anytime(Modifier.weight(1f))
            TotalTime(departureTime = result.departureDateTime, arrivalTime = result.arrivalDateTime)
        }
    }
}

@Composable
fun ResultHeaderTest(
    index: Int
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Index: $index", style = TextStyle(color = Color.White))
    }
}

