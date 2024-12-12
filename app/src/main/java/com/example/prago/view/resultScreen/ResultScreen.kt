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
import com.example.prago.view.common.GoBackTopBar
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
            GoBackTopBar()

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