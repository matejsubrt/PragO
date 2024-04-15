package com.example.prago

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val lineNameSize = 16
val stopNameSize = 12
val tripIconSize = 48
val transferIconSize = 32
val transferTextSize = 14
val headerTextSize = 16


val stopNameStyle = TextStyle(
    color = Color.Black,
    fontSize = stopNameSize.sp,
    fontWeight = FontWeight.Medium
)


val colorNextbike = Color(0, 0, 128)


@Composable
private fun formatTime(time: Int): String {
    return if (time >= 60) {
        val minutes = time / 60
        val seconds = time % 60
        "$minutes:${seconds.toString().padStart(2, '0')} min"
    } else {
        "$time s"
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
        text = "${duration.toMinutes()} min",
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
    } else if (displaySeconds) {
        "${timeLeft.toMinutes()}:${timeLeft.seconds % 60} min"
    } else {
        "${timeLeft.toMinutes()} min"
    }

    Text(
        text = formattedTime,
        modifier = modifier,
        style = TextStyle(
            color = if (departed) Color.LightGray else Color.White,
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
    departureTime: LocalDateTime,
    arrivalTime: LocalDateTime
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.DarkGray) // Dark grey background for the header
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
            style = stopNameStyle
        )
        Text(
            text = formattedTime,
            style = stopNameStyle
        )
    }
}


@Composable
fun UsedTripCard(trip: UsedTrip) {
    Row(
        verticalAlignment = Alignment.CenterVertically, // Align items vertically in the center
        modifier = Modifier.background(Color.Gray)
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
                .padding(all = 4.dp)
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
            .background(Color.LightGray)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(2f)) {
            Spacer(modifier = Modifier.width((tripIconSize.dp - transferIconSize.dp) / 2))
            Icon(
                painter = painterResource(id = R.drawable.walk),
                contentDescription = null,
                modifier = Modifier
                    .size(transferIconSize.dp)
                    .padding(all = 4.dp)
            )
            Spacer(modifier = Modifier.width((tripIconSize.dp - transferIconSize.dp) / 2))
            Text(
                text = "Transfer",
                style = TextStyle(
                    fontWeight = FontWeight.Medium
                ),
                color = Color.DarkGray
            )
        }

        Row(
            modifier = Modifier.padding(end = 8.dp).weight(1f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = formatDistance(transfer.distance),
                    style = TextStyle(color = Color.Black),
                    modifier = Modifier.padding(4.dp) // Add padding to the text
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .background(Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = formatTime(transfer.time),
                    style = TextStyle(color = Color.Black),
                    modifier = Modifier.padding(4.dp) // Add padding to the text
                )
            }
        }
    }
}




@Composable
fun UsedBikeTripCard(bikeTrip: UsedBikeTrip){
    Row(
        modifier = Modifier
            .background(Color.Gray)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(
            painter = painterResource(id = R.drawable.bike),
            contentDescription = null,
            modifier = Modifier
                .size(tripIconSize.dp)
                .padding(all = 4.dp)
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
                        color = colorNextbike
                    )
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
                        style = stopNameStyle
                    )
                    Text(
                        text = bikeTrip.destStopInfo.name,
                        style = stopNameStyle
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Box(
                        modifier = Modifier
                            .background(Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = formatDistance(bikeTrip.distance),
                            style = TextStyle(color = Color.Black),
                            modifier = Modifier.padding(4.dp) // Add padding to the text
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier
                            .background(Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = formatTime(bikeTrip.time),
                            style = TextStyle(color = Color.Black),
                            modifier = Modifier.padding(4.dp) // Add padding to the text
                        )
                    }
                }
            }
        }
    }
}





@Composable
fun ResultCard(result: ConnectionSearchResult){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 0.dp)
            .background(Color.LightGray),
        shape = RoundedCornerShape(16.dp) // Applying the shape modifier directly
    ) {
        Column(
            modifier = Modifier
                .padding(0.dp)
                .background(Color.LightGray)
        ){
            ResultHeader(result.departureDateTime, result.arrivalDateTime)
            result.usedSegmentTypes.forEach{segmentType ->
                var transferIndex = 0
                var tripIndex = 0
                var bikeTripIndex = 0
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
    Surface(){
        ResultCard(result = result)
    }
}
