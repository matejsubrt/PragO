package com.example.prago.composables.resultScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prago.dataClasses.ConnectionSearchResult
import com.example.prago.viewModels.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun ResultCard(result: ConnectionSearchResult?, viewModel: SharedViewModel){
    val coroutineScope = rememberCoroutineScope()


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
                ResultHeader(result)


                if(result.usedSegmentTypes[0] == 0){
                    UsedFirstLastStopCard(result.usedTransfers[0].srcStopInfo.name, result.departureDateTime)
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
                                        viewModel.getAlternatives(result, currTripIndex, toPast)
                                    }
                                },
                                onIndexChanged = { newIndex ->
                                    viewModel.updateCurrIndex(result, currTripIndex, newIndex)
                                }
                            )
                                //currIndex = result.usedTripsIndices[currTripIndex])
                            tripIndex++
                        }//UsedTripCard(result.usedTripsWithAlternatives[tripIndex][result.usedTripsIndices[tripIndex++]])//result.usedTrips[tripIndex++])
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
