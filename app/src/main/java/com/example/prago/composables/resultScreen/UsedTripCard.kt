package com.example.prago.composables.resultScreen

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.prago.R
import com.example.prago.model.dataClasses.ColorStruct
import com.example.prago.model.dataClasses.StopPass
import com.example.prago.model.dataClasses.TripAlternatives
import com.example.prago.model.dataClasses.UsedTrip
import java.time.LocalDateTime
import java.util.UUID

//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun UsedTripAlternativesRow(
//    tripAlternatives: TripAlternatives,
//    onExpand: (Boolean) -> Unit,
//    onIndexChanged: (Int) -> Unit,  // New callback for updating index
//    modifier: Modifier = Modifier
//) {
//    val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = tripAlternatives.currIndex + 1)
//    val snapBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState)
//
//
//
//    Box(
//        modifier = modifier.fillMaxWidth(),
//        contentAlignment = Alignment.Center
//    ) {
//        LazyRow(
//            modifier = modifier.fillMaxWidth(),
//            state = lazyListState,
//            flingBehavior = snapBehavior
//        ) {
//            val itemHeight = 68.dp
//            item {
//                Box(
//                    modifier = Modifier
//                        .fillParentMaxWidth()
//                        .height(itemHeight)
//                        .background(MaterialTheme.colorScheme.tertiaryContainer),
//                    contentAlignment = Alignment.Center
//                ) {
//                    CircularProgressIndicator()
//                }
//            }
//            items(tripAlternatives.alternatives) { trip: UsedTrip ->
//                UsedTripCard(trip = trip, modifier = Modifier.fillParentMaxWidth().height(itemHeight))
//            }
//            item {
//                Box(
//                    modifier = Modifier
//                        .fillParentMaxWidth()
//                        .height(itemHeight)
//                        .background(MaterialTheme.colorScheme.tertiaryContainer),
//                    contentAlignment = Alignment.Center
//                ) {
//                    CircularProgressIndicator()
//                }
//            }
//        }
//
//        // Scroll to new currIndex when it changes
//        LaunchedEffect(tripAlternatives.currIndex to UUID.randomUUID()) {
//            lazyListState.scrollToItem(tripAlternatives.currIndex + 1)
//        }
//
//
//        // Observe scrolling state and update currIndex on snapping
//        LaunchedEffect(lazyListState, tripAlternatives.alternatives) {
//            snapshotFlow { lazyListState.isScrollInProgress }
//                .collect { isScrolling ->
//                    if (!isScrolling) {
//                        val firstVisibleItemIndex = lazyListState.firstVisibleItemIndex
//                        val firstVisibleItemScrollOffset = lazyListState.firstVisibleItemScrollOffset
//                        val itemCount = tripAlternatives.alternatives.size // Current number of alternatives
//
//                        when {
//                            firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset == 0 && tripAlternatives.currIndex == 0 -> {
//                                Log.i("DEBUG", "First item snapped")
//                                onExpand(true)
//                            }
//                            firstVisibleItemIndex == itemCount + 1 -> {
//                                Log.i("DEBUG", "Last item snapped")
//                                onExpand(false)
//                            }
//                            firstVisibleItemIndex in 1..itemCount -> {
//                                onIndexChanged(firstVisibleItemIndex - 1)
//                            }
//                        }
//                    }
//                }
//        }
//    }
//}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UsedTripAlternativesRow(
    tripAlternatives: TripAlternatives,
    onExpand: (Boolean) -> Unit,
    onIndexChanged: (Int) -> Unit,  // New callback for updating index
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(initialPage = tripAlternatives.currIndex + 1, pageCount = {tripAlternatives.alternatives.size + 2})

    val expanding = remember { mutableStateOf(false) }

    LaunchedEffect(pagerState.currentPage) {
//        if (pagerState.currentPage == tripAlternatives.count + 1 || pagerState.currentPage == 0) {
//            expanding.value = false
//        }
//        else{
//            onIndexChanged(pagerState.currentPage - 1)
//        }
        if(!expanding.value){
            if(pagerState.currentPage == 0){
                onExpand(true)
            }
            else if(pagerState.currentPage == tripAlternatives.alternatives.size + 1){
                val currIndex = pagerState.currentPage - 3
                Log.i("DEBUG", "CURRENT index: $currIndex")
                expanding.value = true
                onExpand(false)
                onIndexChanged(currIndex + 1)
                Log.i("DEBUG", "SETTING index to ${currIndex + 1}")
                expanding.value = false
            }
            else {
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeight)
                            .background(MaterialTheme.colorScheme.tertiaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
//                        if(!expanding.value){
//                            expanding.value = true
//                            onExpand(true)
//                            Log.i("DEBUG", "Expanding")
//                        }
                    }
                }
                tripAlternatives.alternatives.size + 1 -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeight)
                            .background(MaterialTheme.colorScheme.tertiaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
//                        if(!expanding.value){
//                            expanding.value = true
//                            onExpand(false)
//                            Log.i("DEBUG", "Expanding")
//                        }
                    }
                }
                else -> {
                    val trip = tripAlternatives.alternatives[page - 1]
                    UsedTripCard(trip = trip, modifier = Modifier.fillMaxWidth().height(itemHeight))
                }
            }
        }

        // Scroll to new currIndex when it changes
        LaunchedEffect(tripAlternatives.currIndex) {
            Log.i("DEBUG", "Scrolling to ${tripAlternatives.currIndex + 1} out of ${tripAlternatives.alternatives.size + 2}")
            pagerState.scrollToPage(tripAlternatives.currIndex + 1)
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
            .fillMaxWidth() // Ensure the Card fills the parent width
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
