package com.example.prago.composables.resultScreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
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
import com.example.prago.model.dataClasses.ConnectionSearchResult
import com.example.prago.formatters.formatDurationTime
import com.example.prago.formatters.formatTime
import com.example.prago.model.dataClasses.TripAlternatives
import com.example.prago.model.dataClasses.UsedTrip
import com.example.prago.viewModel.AppViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import java.time.Duration
import java.time.LocalDateTime


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
fun CountDown(
    departureTime: LocalDateTime,
    modifier: Modifier = Modifier
) {
    var timeLeft by remember { mutableStateOf(Duration.ZERO) }
    var displaySeconds by remember { mutableStateOf(false) }
    var departed by remember { mutableStateOf(false) }

    LaunchedEffect(departureTime) { // Trigger LaunchedEffect when departureTime changes
        flow {
            while (true) {
                val currentTime = LocalDateTime.now()
                timeLeft = Duration.between(currentTime, departureTime)
                displaySeconds = timeLeft.seconds < 600
                departed = timeLeft <= Duration.ZERO
                emit(Unit)
                delay(1000)
            }
        }.collect { }
    }

    val formattedTime = if (departed) {
        stringResource(R.string.departed)
    }
    else {
        stringResource(R.string.in_time) + " " + formatTime(timeLeft.seconds)
    }

    Text(
        text = formattedTime,
        modifier = modifier,
        style = TextStyle(
            color = if (departed) Color(0xFFE0E0E0) else MaterialTheme.colorScheme.onBackground,
            fontWeight = if (departed) FontWeight.Normal else FontWeight.Bold,
            fontSize = headerTextSize.sp
        )
    )
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
            CountDown(LocalDateTime.now().plusMinutes(5))
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
            val firstTrip = result.usedTripAlternatives.first().alternatives[firstTripAltIndex]
            val firstTripDepartureTime = firstTrip.stopPasses[firstTrip.getOnStopIndex].departureTime

            val lastTrip = result.usedTripAlternatives.last().alternatives[lastTripAltIndex]
            val lastTripArrivalTime = lastTrip.stopPasses[lastTrip.getOffStopIndex].arrivalTime

            val departureTime = firstTripDepartureTime.minusSeconds(result.secondsBeforeFirstTrip.toLong())
            val arrivalTime = lastTripArrivalTime.plusSeconds(result.secondsAfterLastTrip.toLong())

            CountDown(firstTripDepartureTime, Modifier.weight(1f))
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

