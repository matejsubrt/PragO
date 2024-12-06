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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.prago.R
import com.example.prago.dataClasses.ColorStruct
import com.example.prago.dataClasses.StopPass
import com.example.prago.dataClasses.TripAlternatives
import com.example.prago.dataClasses.UsedTrip
import java.time.LocalDateTime
import java.util.UUID

//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun UsedTripAlternativesRow(tripAlternatives: List<UsedTrip>, onIndexChange: (Int) -> Unit,  currIndex: Int, modifier: Modifier = Modifier) {
//    val lazyListState = rememberLazyListState()
//    val snapBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState)
//
//    Log.i("DEBUG", "Curr index: $currIndex, tripAlternatives size: ${tripAlternatives.size}")
//    //LaunchedEffect(key1 = currIndex) { lazyListState.scrollToItem(currIndex + 1) }
//    LaunchedEffect(key1 = currIndex) {
//        // If tripAlternatives is non-empty and currIndex is valid, scroll to the current index
//        if (tripAlternatives.isNotEmpty()) {
//            lazyListState.scrollToItem((currIndex+1).coerceIn(0, tripAlternatives.size))
//        }
//    }
//
//    Box(
//        modifier = modifier
//            .fillMaxWidth(),
//        contentAlignment = Alignment.Center
//    ) {
//        LazyRow(
//            modifier = modifier
//                .fillMaxWidth(),
//            state = lazyListState,
//            flingBehavior = snapBehavior
//        ) {
//            item {
//                Box(
//                    modifier = Modifier
//                        .fillParentMaxWidth()
//                        .padding(16.dp),
//                    contentAlignment = Alignment.Center
//                ) {
//                    CircularProgressIndicator()
//                }
//            }
//            items(
//                tripAlternatives
//            ) { trip: UsedTrip ->
//                UsedTripCard(trip = trip, modifier = Modifier.fillParentMaxWidth())
//            }
//            item {
//                Box(
//                    modifier = Modifier
//                        .fillParentMaxWidth()
//                        .padding(16.dp),
//                    contentAlignment = Alignment.Center
//                ) {
//                    CircularProgressIndicator()
//                }
//            }
//        }
//
////        LaunchedEffect(lazyListState) {
////            snapshotFlow { lazyListState.firstVisibleItemIndex }
////                .collect {
////                    firstVisibleItemIndex -> Log.i("DEBUG", "First visible index: $firstVisibleItemIndex"); if(firstVisibleItemIndex == 0) onIndexChange(firstVisibleItemIndex) }
////        }
//        LaunchedEffect(lazyListState) {
//            snapshotFlow {
//                Pair(lazyListState.firstVisibleItemIndex, lazyListState.firstVisibleItemScrollOffset)
//            }.collect { (firstVisibleItemIndex, firstVisibleItemScrollOffset) ->
//                if (firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset == 0) {
//                    Log.i("DEBUG", "First item snapped")
//                    onIndexChange(firstVisibleItemIndex)
//                }
//            }
//        }
//    }
//}
/*@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UsedTripAlternativesRow(
    tripAlternatives: List<UsedTrip>,
    onExpand: (Boolean) -> Unit,
    expandedToPast: MutableState<Boolean>,
    expandedToFuture: MutableState<Boolean>,
    currIndex: Int,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = 1)//(currIndex + 1).coerceIn(0, tripAlternatives.size))
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState)

    //Log.i("DEBUG", "Curr index: $currIndex, tripAlternatives size: ${tripAlternatives.size}")

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        LazyRow(
            modifier = modifier.fillMaxWidth(),
            state = lazyListState,
            flingBehavior = snapBehavior
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            items(tripAlternatives) { trip: UsedTrip ->
                UsedTripCard(trip = trip, modifier = Modifier.fillParentMaxWidth())
            }
            item {
                Box(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

//        LaunchedEffect(expandedToPast) {
//            if (expandedToPast.value && tripAlternatives.isNotEmpty()) {
//                //TODO: remove absolute value
//                lazyListState.animateScrollToItem(5)
//                Log.i("DEBUG", "Scrolled to index: 5")
//                expandedToPast.value = false
//            }
//        }

//        LaunchedEffect(expandedToFuture) {
//            if(expandedToFuture.value && tripAlternatives.size > 5){
//                Log.i("DEBUG", "WTF")
//                lazyListState.animateScrollToItem(tripAlternatives.size - 5 + 1)
//                //Log.i("DEBUG", "Scrolled to index: ${tripAlternatives.size - 5}")
//                expandedToFuture.value = false
//            }
//        }

        LaunchedEffect(currIndex){
            if(currIndex != 0){
                lazyListState.animateScrollToItem(currIndex + 1)
            }
        }

        // Observing scrolling state and item snapping
        LaunchedEffect(lazyListState) {
            snapshotFlow { lazyListState.isScrollInProgress }
                .collect { isScrolling ->
                    if (!isScrolling) {
                        val firstVisibleItemIndex = lazyListState.firstVisibleItemIndex
                        val firstVisibleItemScrollOffset = lazyListState.firstVisibleItemScrollOffset

                        if (firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset == 0/* && !expandedToPast.value*/) {
                            Log.i("DEBUG", "First item snapped")
                            onExpand(true)
                        }
                        else if(firstVisibleItemIndex == tripAlternatives.size + 1/* && !expandedToFuture.value*/){
                            Log.i("DEBUG", "Last item snapped, firstvisindex = $firstVisibleItemIndex, alternatives size = ${tripAlternatives.size}")
                            onExpand(false)

                        }
                        else if(firstVisibleItemIndex != 0 && firstVisibleItemIndex != tripAlternatives.size + 1){
                            currIndex = firstVisibleItemIndex - 1

                        }
                    }
                }
        }
    }
}*/

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UsedTripAlternativesRow(
    tripAlternatives: TripAlternatives,
    onExpand: (Boolean) -> Unit,
    onIndexChanged: (Int) -> Unit,  // New callback for updating index
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = tripAlternatives.currIndex + 1)
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState)

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        LazyRow(
            modifier = modifier.fillMaxWidth(),
            state = lazyListState,
            flingBehavior = snapBehavior
        ) {
            val itemHeight = 68.dp
            item {
                Box(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .height(itemHeight)
                        .background(MaterialTheme.colorScheme.tertiaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            items(tripAlternatives.alternatives) { trip: UsedTrip ->
                UsedTripCard(trip = trip, modifier = Modifier.fillParentMaxWidth().height(itemHeight))
            }
            item {
                Box(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .height(itemHeight)
                        .background(MaterialTheme.colorScheme.tertiaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        // Scroll to new currIndex when it changes
        LaunchedEffect(tripAlternatives.currIndex to UUID.randomUUID()) {
            lazyListState.scrollToItem(tripAlternatives.currIndex + 1)
        }


        // Observe scrolling state and update currIndex on snapping
        LaunchedEffect(lazyListState, tripAlternatives.alternatives) {
            snapshotFlow { lazyListState.isScrollInProgress }
                .collect { isScrolling ->
                    if (!isScrolling) {
                        val firstVisibleItemIndex = lazyListState.firstVisibleItemIndex
                        val firstVisibleItemScrollOffset = lazyListState.firstVisibleItemScrollOffset
                        val itemCount = tripAlternatives.alternatives.size // Current number of alternatives

                        when {
                            firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset == 0 && tripAlternatives.currIndex == 0 -> {
                                Log.i("DEBUG", "First item snapped")
                                onExpand(true)
                            }
                            firstVisibleItemIndex == itemCount + 1 -> {
                                Log.i("DEBUG", "Last item snapped")
                                onExpand(false)
                            }
                            firstVisibleItemIndex in 1..itemCount -> {
                                onIndexChanged(firstVisibleItemIndex - 1)
                            }
                        }
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
