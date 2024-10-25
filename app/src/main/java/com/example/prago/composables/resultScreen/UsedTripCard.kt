package com.example.prago.composables.resultScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.prago.R
import com.example.prago.dataClasses.ColorStruct
import com.example.prago.dataClasses.StopPass
import com.example.prago.dataClasses.UsedTrip
import java.time.LocalDateTime

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
            LineRow(trip.routeName, trip.color, trip.hasDelayInfo, trip.currentDelay)
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


@Preview(showBackground = true)
@Composable
fun PreviewUsedTripCard() {
    val sampleTrip = UsedTrip(
        getOnStopIndex = 0,
        getOffStopIndex = 1,
        routeName = "123",
        color = ColorStruct(98, 0, 238),
        stopPasses = listOf(
            StopPass(
                name = "Stop A",
                id = "1",
                arrivalTime = LocalDateTime.of(2024, 10, 23, 8, 0),
                departureTime = LocalDateTime.of(2024, 10, 23, 8, 5)
            ),
            StopPass(
                name = "Stop B",
                id = "2",
                arrivalTime = LocalDateTime.of(2024, 10, 23, 8, 30),
                departureTime = LocalDateTime.of(2024, 10, 23, 8, 35)
            )
        ),
        vehicleType = 3,
        hasDelayInfo = true,
        delayWhenBoarded = 0,
        currentDelay = -168,
        tripId = "trip123"
    )

    UsedTripCard(trip = sampleTrip)
}
