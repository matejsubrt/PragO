package com.example.prago.view.resultScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.prago.view.LocalAppViewModel
import com.example.prago.view.ResultTopBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun ResultScreen(){
    val viewModel = LocalAppViewModel.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var lastDelayUpdateTime = remember { System.currentTimeMillis() }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
        ){
            ResultTopBar()
            PullToRefreshLazyColumn(
                content = { searchResult ->
                    key(searchResult.usedTripAlternatives.map { item -> item.alternatives.map{ item2 -> item2.tripId }}.joinToString()) {
                        ResultCard(searchResult, viewModel)
                    }
                },
                onRefresh = { toPast ->
                    scope.launch {
                        viewModel.expandSearch(toPast, context)
                    }
                },
                viewModel = viewModel
            )
        }

        LaunchedEffect(Unit) {
            while (true) {
                if (System.currentTimeMillis() - lastDelayUpdateTime > 20 * 1000L) {
                    lastDelayUpdateTime = System.currentTimeMillis()
                    viewModel.updateDelays(context)
                }
                delay(20 * 1000L)
            }
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