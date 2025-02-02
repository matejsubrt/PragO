package com.example.prago.view.resultScreen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.prago.R
import com.example.prago.model.dataClasses.searchResult.ConnectionSearchResult
import com.example.prago.viewModel.AppViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun  PullToRefreshLazyColumn(
    content: @Composable (ConnectionSearchResult) -> Unit,
    onRefresh: suspend (Boolean) -> Unit,
    viewModel: AppViewModel
) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    val isExpandingToPast by viewModel.expandingSearchToPast.collectAsState()
    val isExpandingToFuture by viewModel.expandingSearchToFuture.collectAsState()
    val items by viewModel.searchResultList.collectAsState()
    val expandingHalted by viewModel.expandingHalted.collectAsState()

    val pullToRefreshState = rememberPullRefreshState(
        refreshing = isExpandingToPast,
        onRefresh = {
            scope.launch{
                onRefresh(true)
            }

        }
    )

//    val canScrollForward by remember {
//        derivedStateOf {
//            lazyListState.canScrollForward
//        }
//    }

    val firstVisibleItem by remember {
        derivedStateOf {
            //lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            lazyListState.firstVisibleItemIndex
        }
    }

    var isLoading by remember { mutableStateOf(false) }




//    LaunchedEffect(canScrollForward, isLoading, items) {
//        if((!isLoading && !isExpandingToFuture) || items.size < 5){
//            if(!canScrollForward){
//                isLoading = true
//                onRefresh(false)
//                isLoading = false
//            }
//        }
//    }
    LaunchedEffect(firstVisibleItem, isLoading, items) {
        if((!isLoading && !isExpandingToFuture) || items.size < 5){
            val itemsAfterFirstVisible = items.size - firstVisibleItem
            if(itemsAfterFirstVisible < 8){
                Log.i("DEBUG", "Expanding to past, itemsAfterFirstVisible: $itemsAfterFirstVisible")
                isLoading = true
                onRefresh(false)
                isLoading = false
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullToRefreshState)
    ){
        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){
            items(items){
                content(it)
            }
            item{
                if(expandingHalted){
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ){
                        Text(
                            text = stringResource(id = R.string.no_other_results_msg),
                            color = Color.White,
                            modifier = Modifier.padding(8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else{
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ){
                        CircularProgressIndicator()
                    }
                }
            }
        }
        PullRefreshIndicator(
            refreshing = isExpandingToPast,
            state = pullToRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = Color.White,

        )
    }
}