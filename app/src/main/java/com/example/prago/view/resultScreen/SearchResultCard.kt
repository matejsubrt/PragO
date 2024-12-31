package com.example.prago.view.resultScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.prago.R
import com.example.prago.model.dataClasses.searchResult.ConnectionSearchResult
import com.example.prago.view.resultScreen.segmentCards.UsedBikeTripCard
import com.example.prago.view.resultScreen.segmentCards.UsedFirstLastStopCard
import com.example.prago.view.resultScreen.segmentCards.UsedTransferCard
import com.example.prago.view.resultScreen.segmentCards.UsedTripAlternativesRow
import com.example.prago.viewModel.AppViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime


@Composable
fun ResultCard(result: ConnectionSearchResult, viewModel: AppViewModel){
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current


    val currFirstIndex = remember { mutableStateOf(0) }
    val currLastIndex = remember { mutableStateOf(0) }

    val startByCoordinates by viewModel.startByCoordinates.collectAsState()


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
                )
        ){
            ResultHeader(result, currFirstIndex.value, currLastIndex.value)

            // Prevents crash when there are no trips
            if(result.usedSegmentTypes[0] == 0 && result.usedTripAlternatives.isNotEmpty()){
                val startTime: LocalDateTime

                // it can come to situations where the index is out of bounds - we check for that here
                if(result.usedTripAlternatives[0].alternatives.size > currFirstIndex.value && currFirstIndex.value >= 0) {
                    val firstTrip =
                        result.usedTripAlternatives[0].alternatives[currFirstIndex.value]
                    val firstTripBoardingTime =
                        firstTrip.stopPasses[firstTrip.getOnStopIndex].departureTime
                    startTime =
                        firstTripBoardingTime
                            .minusSeconds(result.secondsBeforeFirstTrip.toLong())
                            .plusSeconds(firstTrip.delayWhenBoarded.value.toLong())

                // if the index is out of bounds, we use the last alternative
                } else if(result.usedTripAlternatives[0].alternatives.isNotEmpty()){
                    val firstTrip = result.usedTripAlternatives[0].alternatives.last()
                    val firstTripBoardingTime = firstTrip.stopPasses[firstTrip.getOnStopIndex].departureTime
                    startTime = firstTripBoardingTime
                        .minusSeconds(result.secondsBeforeFirstTrip.toLong())
                        .plusSeconds(firstTrip.delayWhenBoarded.value.toLong())
                } else {
                    throw Exception("First trip has no alternatives")
                }

                val startPointName = if (startByCoordinates) stringResource(id = R.string.current_location) else result.usedTransfers[0].srcStopInfo.name

                UsedFirstLastStopCard(startPointName, startTime)
            }


            val usedTripCount = result.usedTripAlternatives.size
            var transferIndex = 0
            var tripIndex = 0
            var bikeTripIndex = 0
            result.usedSegmentTypes.forEach{segmentType ->
                when(segmentType){
                    0 -> UsedTransferCard(result.usedTransfers[transferIndex++])
                    1 -> {
                        val currTripIndex = tripIndex
                        UsedTripAlternativesRow(
                            tripAlternatives = result.usedTripAlternatives[currTripIndex],
                            onExpand = { toPast, altCountBeforeFetch ->
                                coroutineScope.launch(Dispatchers.Main) {
                                    withContext(Dispatchers.IO) {
                                        viewModel.fetchAlternatives(result, currTripIndex, toPast, context)
                                    }
                                    if(currTripIndex == 0){
                                        currFirstIndex.value = altCountBeforeFetch
                                    }
                                    if(currTripIndex == usedTripCount - 1){
                                        currLastIndex.value = altCountBeforeFetch
                                    }
                                }
                            },
                            onIndexChanged = { newIndex ->
                                viewModel.updateCurrIndex(result, currTripIndex, newIndex)
                                if(currTripIndex == 0){
                                    currFirstIndex.value = newIndex
                                }
                                if(currTripIndex == result.usedTripAlternatives.size - 1){
                                    currLastIndex.value = newIndex
                                }
                            }
                        )
                        tripIndex++
                    }
                    2 -> UsedBikeTripCard(result.usedBikeTrips[bikeTripIndex++])
                }
            }

            // Prevents crash when there are no trips
            if(result.usedSegmentTypes.last() == 0 && result.usedTripAlternatives.isNotEmpty()){
                val endTime: LocalDateTime

                // it can come to situations where the index is out of bounds - we check for that here
                if(result.usedTripAlternatives.last().alternatives.size > currLastIndex.value && currLastIndex.value >= 0) {
                    val lastTrip =
                        result.usedTripAlternatives.last().alternatives[currLastIndex.value]
                    val lastTripDisembarkTime =
                        lastTrip.stopPasses[lastTrip.getOffStopIndex].arrivalTime
                    endTime =
                        lastTripDisembarkTime
                            .plusSeconds(result.secondsAfterLastTrip.toLong())
                            .plusSeconds(lastTrip.currentDelay.value.toLong())

                // if the index is out of bounds, we use the first alternative
                } else if(result.usedTripAlternatives[0].alternatives.isNotEmpty()){
                    val lastTrip = result.usedTripAlternatives.last().alternatives.first()
                    val lastTripDisembarkTime = lastTrip.stopPasses[lastTrip.getOnStopIndex].departureTime
                    endTime = lastTripDisembarkTime
                        .plusSeconds(result.secondsAfterLastTrip.toLong())
                        .plusSeconds(lastTrip.currentDelay.value.toLong())
                } else {
                    throw Exception("First trip has no alternatives")
                }

                val endPointName = result.usedTransfers.last().destStopInfo.name

                UsedFirstLastStopCard(endPointName, endTime)
            }
        }
    }
}
