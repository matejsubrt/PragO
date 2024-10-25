package com.example.prago.composables.resultScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.prago.activities.LocalSharedViewModel
import com.example.prago.composables.ResultTopBar
import kotlinx.coroutines.launch


@Composable
fun ResultScreen(){
    val viewModel = LocalSharedViewModel.current
    val searchResultList by viewModel.searchResultList.observeAsState()
    val scope = rememberCoroutineScope()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
        ){
            ResultTopBar()

            PullToRefreshLazyColumn(
                items = searchResultList ?: emptyList(),
                content = { searchResult ->
                    ResultCard(searchResult)
                },
                isExpandingToPast = viewModel.expandingSearchToPast.value,
                isExpandingToFuture = viewModel.expandingSearchToFuture.value,
                //expansionToPastItems = viewModel.expansionToPastItems.value,
                onRefresh = {toPast ->
                    scope.launch{
                        viewModel.expandSearch(toPast)
                    }
                })



//            LazyColumn(
//                modifier = Modifier.padding(8.dp)
//            ) {
//                searchResultList?.let { results ->
//                    items(results) { result ->
//                        Column {
//                            //Log.i("DEBUG", "Curr delay: ${result.usedTrips[0].currentDelay}")
//                            ResultCard(result)
//                            Spacer(modifier = Modifier.height(16.dp)) // Adjust the height as needed
//                        }
//                    }
//                }
//            }

        }
    }
}



//@Preview(showBackground = true)
//@Composable
//fun ResultScreenPreview() {
//    PragOTheme(darkTheme = true) {
//        val result = ConnectionSearchResult(
//            usedTrips = listOf(
//                UsedTrip(
//                    getOnStopIndex = 0,
//                    getOffStopIndex = 1,
//                    routeName = "Example Route",
//                    color = ColorStruct(255, 0, 0),
//                    vehicleType = 2,
//                    stopPasses = listOf(
//                        StopPass("Stop 1", "1", LocalDateTime.parse("2024-04-15T12:00:00"), LocalDateTime.parse("2024-04-15T12:05:00")),
//                        StopPass("Stop 2", "2", LocalDateTime.parse("2024-04-15T12:10:00"), LocalDateTime.parse("2024-04-15T12:15:00"))
//                    )
//                )
//            ),
//            usedTransfers = listOf(
//                UsedTransfer(
//                    srcStopInfo = StopInfo("Src Stop", "src_id", 0.0, 0.0),
//                    destStopInfo = StopInfo("Dest Stop", "dest_id", 0.0, 0.0),
//                    time = 10,
//                    distance = 20
//                )
//            ),
//            usedBikeTrips = listOf(
//                UsedBikeTrip(
//                    srcStopInfo = StopInfo("Src Stop", "src_id", 0.0, 0.0),
//                    destStopInfo = StopInfo("Dest Stop", "dest_id", 0.0, 0.0),
//                    distance = 100,
//                    time = 30,
//                    remainingBikes = 3
//                )
//            ),
//            usedSegmentTypes = listOf(1,0,2),
//            transferCount = 1,
//            tripCount = 1,
//            bikeTripCount = 1,
//            departureDateTime = LocalDateTime.now(),
//            arrivalDateTime = LocalDateTime.now().plusHours(1)
//        )
//        ResultScreen(result)
//    }
//}