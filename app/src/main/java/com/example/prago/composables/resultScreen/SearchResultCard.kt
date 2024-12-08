package com.example.prago.composables.resultScreen
import android.util.Log
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
import androidx.compose.ui.unit.dp
import com.example.prago.model.dataClasses.ConnectionSearchResult
import com.example.prago.viewModel.AppViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun ResultCard(result: ConnectionSearchResult, viewModel: AppViewModel){
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current


    //val firstTripCurrAltIndex = viewModel.getCurrIndexFlow(result, 0).collectAsState()

    var currFirstIndex = remember { mutableStateOf(0) }
    var currLastIndex = remember { mutableStateOf(0) }


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
                    )
            ){
                //ResultHeader(result.usedTripAlternatives.first(), result.usedTripAlternatives.last())
                ResultHeader(result, currFirstIndex.value, currLastIndex.value)

                if(result.usedSegmentTypes[0] == 0 && result.usedTripAlternatives.isNotEmpty() && (result.usedTripAlternatives[0].alternatives.size > currFirstIndex.value) && currFirstIndex.value >= 0){
                    val firstTrip = result.usedTripAlternatives[0].alternatives[currFirstIndex.value]
                    Log.i("DEBUG", "First Trip: $firstTrip")
                    val firstTripBoardingTime = firstTrip.stopPasses[firstTrip.getOnStopIndex].departureTime
                    val startTime = firstTripBoardingTime.minusSeconds(result.secondsBeforeFirstTrip.toLong())
                    UsedFirstLastStopCard(result.usedTransfers[0].srcStopInfo.name, startTime)
                    Log.i("DEBUG", "FIRST TRANSFER UPDATED. NEW TIME: $startTime, curr first index: ${currFirstIndex.value}, alt size: ${result.usedTripAlternatives[0].alternatives.size}")
                }


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
                                onExpand = { toPast ->
                                    coroutineScope.launch(Dispatchers.IO) {
                                        viewModel.fetchAlternatives(result, currTripIndex, toPast, context)
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


                if(result.usedSegmentTypes[result.usedSegmentTypes.size - 1] == 0){
                    UsedFirstLastStopCard(result.usedTransfers[result.usedTransfers.size - 1].destStopInfo.name, result.arrivalDateTime)
                }
            }
        }
}


//@Composable
//@Preview
//fun ResultCardPreview() {
//    val result = ConnectionSearchResult(
//        usedTrips = listOf(
//            UsedTrip(
//                getOnStopIndex = 0,
//                getOffStopIndex = 1,
//                routeName = "Example Route",
//                color = ColorStruct(255, 0, 0),
//                vehicleType = 2,
//                stopPasses = listOf(
//                    StopPass("Stop 1", "1", LocalDateTime.parse("2024-04-15T12:00:00"), LocalDateTime.parse("2024-04-15T12:05:00")),
//                    StopPass("Stop 2", "2", LocalDateTime.parse("2024-04-15T12:10:00"), LocalDateTime.parse("2024-04-15T12:15:00"))
//                )
//            )
//        ),
//        usedTransfers = listOf(
//            UsedTransfer(
//                srcStopInfo = StopInfo("Src Stop", "src_id", 0.0, 0.0),
//                destStopInfo = StopInfo("Dest Stop", "dest_id", 0.0, 0.0),
//                time = 10,
//                distance = 20
//            )
//        ),
//        usedBikeTrips = listOf(
//            UsedBikeTrip(
//                srcStopInfo = StopInfo("Src Stop", "src_id", 0.0, 0.0),
//                destStopInfo = StopInfo("Dest Stop", "dest_id", 0.0, 0.0),
//                distance = 100,
//                time = 30,
//                remainingBikes = 3
//            )
//        ),
//        usedSegmentTypes = listOf(1,0,2),
//        transferCount = 1,
//        tripCount = 1,
//        bikeTripCount = 1,
//        departureDateTime = LocalDateTime.now(),
//        arrivalDateTime = LocalDateTime.now().plusHours(1)
//    )
//    PragOTheme(darkTheme = true){
//        ResultCard(result = result)
//    }
//}
