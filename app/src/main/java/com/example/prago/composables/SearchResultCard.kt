package com.example.prago.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prago.R
import com.example.prago.dataClasses.ColorStruct
import com.example.prago.dataClasses.ConnectionSearchResult
import com.example.prago.dataClasses.StopInfo
import com.example.prago.dataClasses.StopPass
import com.example.prago.dataClasses.UsedBikeTrip
import com.example.prago.dataClasses.UsedTransfer
import com.example.prago.dataClasses.UsedTrip
import com.example.prago.ui.theme.PragOTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val lineNameSize = 17
val stopNameSize = 14
val tripIconSize = 42
val transferIconSize = 32
val transferTextSize = 14
val headerTextSize = 16


val stopNameStyle = TextStyle(
    color = Color.Black,
    fontSize = stopNameSize.sp,
    fontWeight = FontWeight.Medium
)


val colorNextbike = Color(0, 0, 128)

val distanceBoxWidth = 46.dp
val timeBoxWidth = 60.dp
val boxHeight = 24.dp
val boxTextSize = 12.sp

val boxTextStyle = TextStyle(
    fontSize = boxTextSize,
    fontWeight = FontWeight.Medium
)



@Composable
private fun formatTime(time: Long): String {
    return if(time >= 60 * 60 * 24){
        val days = time / (60*60*24)
        "$days days"
    } else if (time >= 60 * 60){
        val hours = time / (60 * 60)
        val minutes = (time % (60 * 60)) / 60
        val formattedMinutes = String.format("%02d", minutes)
        "$hours:$formattedMinutes h"
    } else if (time >= 60) {
        val minutes = time / 60
        val seconds = time % 60
        val formattedSeconds = String.format("%02d", seconds)
        "$minutes:$formattedSeconds min"
    } else {
        "$time s"
    }
}

@Composable
private fun formatDurationTime(time: Long): String {
    return if(time >= 60 * 60){
        val hours = time / (60 * 60)
        val minutes = (time % (60 * 60)) / 60
        val formattedMinutes = String.format("%02d", minutes)
        "$hours:$formattedMinutes h"
    } else{
        val minutes = time / 60
        "$minutes min"
    }
}

@Composable
private fun formatDistance(distance: Int): String {
    return if (distance < 1000) {
        "$distance m"
    } else {
        val km = distance / 1000.0
        "%.1f km".format(km)
    }
}



@Composable
fun TripNameLine(text: String, color: Color){
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

    LaunchedEffect(true) {
        flow {
            while (true) {
                val currentTime = LocalDateTime.now()
                timeLeft = Duration.between(currentTime, departureTime)
                displaySeconds = timeLeft.seconds < 600 // Check if less than 10 minutes left
                departed = timeLeft <= Duration.ZERO // Check if departure time has passed
                emit(Unit)
                delay(1000)
            }
        }.collect { } // Provide an empty collector
    }

    val formattedTime = if (departed) {
        "Departed"
    } //else if (displaySeconds) {
        //formatTime(timeLeft.seconds)
    //}
    else {
        "In " + formatTime(timeLeft.seconds)
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
    departureTime: LocalDateTime,
    arrivalTime: LocalDateTime
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)) // Dark grey background for the header
            .padding(vertical = 8.dp, horizontal = 16.dp), // Adjust padding as needed
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CountDown(departureTime, Modifier.weight(1f))
        TotalTime(departureTime, arrivalTime)
    }
}


@Composable
fun LineRow(lineName: String, colorStruct: ColorStruct) {
    val text = "Line $lineName"
    val color = Color(colorStruct.r, colorStruct.g, colorStruct.b)
    TripNameLine(text = text, color = color)
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


@Composable
fun UsedTripCard(trip: UsedTrip) {
    Row(
        verticalAlignment = Alignment.CenterVertically, // Align items vertically in the center
        modifier = Modifier
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .padding(start = 2.dp, top = 0.dp, end = 2.dp, bottom = 4.dp)
    ) {
        val icon = when (trip.vehicleType) {
            0 -> R.drawable.tram
            1 -> R.drawable.subway
            2 -> R.drawable.train
            3 -> R.drawable.bus
            4 -> R.drawable.boat
            5 -> R.drawable.tram
            6 -> R.drawable.funicular
            7 -> R.drawable.funicular
            11 -> R.drawable.trolleybus
            12 -> R.drawable.train
            else -> R.drawable.bus
        }
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier
                .size(tripIconSize.dp)
                .padding(all = 4.dp),
            tint = MaterialTheme.colorScheme.onTertiaryContainer
        )
        Column(
            modifier = Modifier.padding(start = 4.dp, top = 2.dp, end = 8.dp, bottom = 4.dp)
        ) {
            LineRow(trip.routeName, trip.color)
            StopRow(
                stopName = trip.stopPasses[trip.getOnStopIndex].name,
                time = trip.stopPasses[trip.getOnStopIndex].departureTime
            )
            StopRow(
                stopName = trip.stopPasses[trip.getOffStopIndex].name,
                time = trip.stopPasses[trip.getOffStopIndex].arrivalTime
            )
        }
    }
}



@Composable
fun UsedTransferCard(transfer: UsedTransfer) {
    Row(
        modifier = Modifier
            .background(Color(0xFF686868))
            .fillMaxWidth()
            .padding(start = 2.dp, top = 2.dp, end = 2.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width((tripIconSize.dp - transferIconSize.dp) / 2))
            Icon(
                painter = painterResource(id = R.drawable.walk),
                contentDescription = null,
                modifier = Modifier
                    .size(transferIconSize.dp)
                    .padding(all = 4.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.width((tripIconSize.dp - transferIconSize.dp) / 2))
            Text(
                text = "Transfer",
                style = TextStyle(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        Row(
            modifier = Modifier.padding(end = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0xFF888888), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp)
                    .width(distanceBoxWidth)
                    .height(boxHeight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = formatDistance(transfer.distance),
                    style = boxTextStyle,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(4.dp),
                    fontSize = boxTextSize
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .background(Color(0xFF888888), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp)
                    .width(timeBoxWidth)
                    .height(boxHeight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = formatTime(transfer.time.toLong()),
                    style = boxTextStyle,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(4.dp),
                    fontSize = boxTextSize
                )
            }

        }
    }
}




@Composable
fun UsedBikeTripCard(bikeTrip: UsedBikeTrip){
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .fillMaxWidth()
            .padding(start = 2.dp, top = 0.dp, end = 2.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(
            painter = painterResource(id = R.drawable.bike),
            contentDescription = null,
            modifier = Modifier
                .size(tripIconSize.dp)
                .padding(all = 4.dp),
            tint = MaterialTheme.colorScheme.onTertiaryContainer
        )
        Column(
            modifier = Modifier.padding(start = 4.dp, top = 2.dp, end = 8.dp, bottom = 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                TripNameLine("Nextbike", colorNextbike)
                Box(
                    modifier = Modifier
                        .background(Color(0xFF888888), RoundedCornerShape(16.dp))
                        .padding(horizontal = 2.dp),
                    contentAlignment = Alignment.Center
                ){
                    Row(
                        horizontalArrangement = Arrangement.End, // Align items to the end (right) of the row
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
                            text = "${bikeTrip.remainingBikes}",
                            color = colorNextbike,
                            fontSize = 15.sp
                        )
                    }
                }
            }


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Column(){
                    Text(
                        text = bikeTrip.srcStopInfo.name,
                        style = stopNameStyle,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = bikeTrip.destStopInfo.name,
                        style = stopNameStyle,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }

            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ){
                Box(
                    modifier = Modifier
                        .background(Color(0xFF888888), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp)
                        .width(distanceBoxWidth)
                        .height(boxHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = formatDistance(bikeTrip.distance),
                        style = boxTextStyle,
                        modifier = Modifier.padding(4.dp),
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        textAlign = TextAlign.Center,
                        fontSize = boxTextSize
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .background(Color(0xFF888888), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp)
                        .width(timeBoxWidth)
                        .height(boxHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = formatTime(bikeTrip.time.toLong()),
                        style = boxTextStyle,
                        modifier = Modifier.padding(4.dp),
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        textAlign = TextAlign.Center,
                        fontSize = boxTextSize
                    )
                }
            }
        }
    }
}





@Composable
fun ResultCard(result: ConnectionSearchResult?){
    if(result != null)
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 0.dp)
                .border(
                    width = 4.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(16.dp)
                    ) // Apply rounded corner shape here
            ){
                ResultHeader(result.departureDateTime, result.arrivalDateTime)
                var transferIndex = 0
                var tripIndex = 0
                var bikeTripIndex = 0
                result.usedSegmentTypes.forEach{segmentType ->
                    when(segmentType){
                        0 -> UsedTransferCard(result.usedTransfers[transferIndex++])
                        1 -> UsedTripCard(result.usedTrips[tripIndex++])
                        2 -> UsedBikeTripCard(result.usedBikeTrips[bikeTripIndex++])
                    }
                }
            }
        }
}


@Composable
@Preview
fun ResultCardPreview() {
    val result = ConnectionSearchResult(
        usedTrips = listOf(
            UsedTrip(
                getOnStopIndex = 0,
                getOffStopIndex = 1,
                routeName = "Example Route",
                color = ColorStruct(255, 0, 0),
                vehicleType = 2,
                stopPasses = listOf(
                    StopPass("Stop 1", "1", LocalDateTime.parse("2024-04-15T12:00:00"), LocalDateTime.parse("2024-04-15T12:05:00")),
                    StopPass("Stop 2", "2", LocalDateTime.parse("2024-04-15T12:10:00"), LocalDateTime.parse("2024-04-15T12:15:00"))
                )
            )
        ),
        usedTransfers = listOf(
            UsedTransfer(
                srcStopInfo = StopInfo("Src Stop", "src_id", 0.0, 0.0),
                destStopInfo = StopInfo("Dest Stop", "dest_id", 0.0, 0.0),
                time = 10,
                distance = 20
            )
        ),
        usedBikeTrips = listOf(
            UsedBikeTrip(
                srcStopInfo = StopInfo("Src Stop", "src_id", 0.0, 0.0),
                destStopInfo = StopInfo("Dest Stop", "dest_id", 0.0, 0.0),
                distance = 100,
                time = 30,
                remainingBikes = 3
            )
        ),
        usedSegmentTypes = listOf(1,0,2),
        transferCount = 1,
        tripCount = 1,
        bikeTripCount = 1,
        departureDateTime = LocalDateTime.now(),
        arrivalDateTime = LocalDateTime.now().plusHours(1)
    )
    PragOTheme(darkTheme = true){
        ResultCard(result = result)
    }
}
