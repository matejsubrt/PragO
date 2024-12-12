package com.example.prago.view.resultScreen.segmentCards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.prago.R
import com.example.prago.model.dataClasses.searchResult.TripAlternatives
import com.example.prago.model.dataClasses.searchResult.UsedTrip
import com.example.prago.view.resultScreen.tripIconSize

@Composable
fun ProgressIndicatorBox(){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .background(MaterialTheme.colorScheme.tertiaryContainer),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }

}


@Composable
fun UsedTripAlternativesRow(
    tripAlternatives: TripAlternatives,
    onExpand: (Boolean, Int) -> Unit,
    onIndexChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(initialPage = tripAlternatives.currIndex + 1, pageCount = {tripAlternatives.alternatives.size + 2})

    LaunchedEffect(pagerState.currentPage) {
        when(pagerState.currentPage){
            0 -> {
                onExpand(true, tripAlternatives.alternatives.size)
            }
            tripAlternatives.alternatives.size + 1 -> {
                onExpand(false, tripAlternatives.alternatives.size)
            }
            else -> {
                onIndexChanged(pagerState.currentPage - 1)
            }
        }
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        HorizontalPager(
            modifier = modifier.fillMaxWidth(),
            state = pagerState,
            key = { page ->
                when (page) {
                    0 -> "start-placeholder"
                    tripAlternatives.alternatives.size + 1 -> "end-placeholder"
                    else -> tripAlternatives.alternatives[page - 1].hashCode()
                }
            }
        ) { page ->
            val itemHeight = 68.dp
            when (page) {
                0 -> {
                    ProgressIndicatorBox()
                }
                tripAlternatives.alternatives.size + 1 -> {
                    ProgressIndicatorBox()
                }
                else -> {
                    val trip = tripAlternatives.alternatives[page - 1]
                    UsedTripCard(trip = trip, modifier = Modifier.fillMaxWidth().height(itemHeight))
                }
            }
        }
    }
}




@Composable
fun UsedTripCard(trip: UsedTrip, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .padding(start = 2.dp, top = 0.dp, end = 2.dp, bottom = 4.dp)
            .fillMaxWidth()
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
            LineRow(trip)
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



//@Preview(showBackground = true)
//@Composable
//fun PreviewUsedTripCard() {
//    val sampleTrip = UsedTrip(
//        getOnStopIndex = 0,
//        getOffStopIndex = 1,
//        routeName = "123",
//        color = ColorStruct(98, 0, 238),
//        stopPasses = listOf(
//            StopPass(
//                name = "Stop A",
//                id = "1",
//                arrivalTime = LocalDateTime.of(2024, 10, 23, 8, 0),
//                departureTime = LocalDateTime.of(2024, 10, 23, 8, 5)
//            ),
//            StopPass(
//                name = "Stop B",
//                id = "2",
//                arrivalTime = LocalDateTime.of(2024, 10, 23, 8, 30),
//                departureTime = LocalDateTime.of(2024, 10, 23, 8, 35)
//            )
//        ),
//        vehicleType = 3,
//        hasDelayInfo = true,
//        delayWhenBoarded = 0,
//        currentDelay = -168,
//        tripId = "trip123"
//    )
//
//    UsedTripCard(trip = sampleTrip)
//}
