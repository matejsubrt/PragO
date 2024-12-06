@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.prago.composables.resultScreen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.prago.R
import com.example.prago.dataClasses.ConnectionSearchResult

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun <T> PullToRefreshLazyColumn(
//    items: List<T>,
//    content: @Composable (T) -> Unit,
//    isExpandingToPast: Boolean,
//    isExpandingToFuture: Boolean,
//    onRefresh: (Boolean) -> Unit,
//    modifier: Modifier = Modifier,
//    lazyListState: LazyListState = rememberLazyListState()
//) {
//    val pullToRefreshState = rememberPullToRefreshState()
//
//    val reachedBottom: Boolean by remember {
//        derivedStateOf {
//            val lastVisibleItem = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()
//            lastVisibleItem?.index != 0 && lastVisibleItem?.index == lazyListState.layoutInfo.totalItemsCount - 1
//        }
//    }
//
//    // load more if scrolled to bottom
//    LaunchedEffect(reachedBottom) {
//        if (reachedBottom){
//            onRefresh(false)
//        }
//    }
//
//
//    Box(
//        modifier = modifier
//            .nestedScroll(pullToRefreshState.nestedScrollConnection)
//    ){
//        LazyColumn(
//            state = lazyListState,
//            contentPadding = PaddingValues(8.dp),
//            modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.spacedBy(8.dp)
//        ){
//            items(items){
//                content(it)
//            }
//
//            if (isExpandingToFuture) {
//                item {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(16.dp),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        CircularProgressIndicator()
//                    }
//                }
//            }
//        }
//
//        if(pullToRefreshState.isRefreshing){
//            LaunchedEffect(true){
//                onRefresh(true)
//            }
//        }
//
//        LaunchedEffect(isExpandingToPast){
//            if(isExpandingToPast){
//                pullToRefreshState.startRefresh()
//            }else{
//                pullToRefreshState.endRefresh()
//            }
//        }
//
//
//        if(pullToRefreshState.progress > 0.5f || isExpandingToPast) {
//            PullToRefreshContainer(
//                state = pullToRefreshState,
//                modifier = Modifier.align(Alignment.TopCenter)
//            )
//        }
//    }
//}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun <T> PullToRefreshLazyColumn(
//    items: List<T>,
//    content: @Composable (T) -> Unit,
//    isExpandingToPast: Boolean,
//    isExpandingToFuture: Boolean,
//    onRefresh: (Boolean) -> Unit,
//    modifier: Modifier = Modifier,
//    lazyListState: LazyListState = rememberLazyListState()
//) {
//    val pullToRefreshState = rememberPullToRefreshState()
//    var isLoading by remember { mutableStateOf(false) }
//
//    val reachedBottom: Boolean by remember {
//        derivedStateOf {
//            val lastVisibleItem = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()
//            lastVisibleItem?.index != 0 && lastVisibleItem?.index == lazyListState.layoutInfo.totalItemsCount - 1
//        }
//    }
//
//    // load more if scrolled to bottom
//    LaunchedEffect(reachedBottom, isLoading) {
//        if (reachedBottom && !isLoading) {
//            isLoading = true
//            onRefresh(false)
//            isLoading = false
//        }
//    }
//
//    Box(
//        modifier = modifier
//            .nestedScroll(pullToRefreshState.nestedScrollConnection)
//    ) {
//        LazyColumn(
//            state = lazyListState,
//            contentPadding = PaddingValues(8.dp),
//            modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            items(items) {
//                content(it)
//            }
//
//            if (isExpandingToFuture) {
//                item {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(16.dp),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        CircularProgressIndicator()
//                    }
//                }
//            }
//        }
//
//        if (pullToRefreshState.isRefreshing) {
//            LaunchedEffect(true) {
//                onRefresh(true)
//            }
//        }
//
//        LaunchedEffect(isExpandingToPast) {
//            if (isExpandingToPast) {
//                pullToRefreshState.startRefresh()
//            } else {
//                pullToRefreshState.endRefresh()
//            }
//        }
//
//        if (pullToRefreshState.progress > 0.5f || isExpandingToPast) {
//            PullToRefreshContainer(
//                state = pullToRefreshState,
//                modifier = Modifier.align(Alignment.TopCenter)
//            )
//        }
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun  PullToRefreshLazyColumn(
    items: List<ConnectionSearchResult>,
    content: @Composable (ConnectionSearchResult) -> Unit,
    isExpandingToPast: Boolean,
    isExpandingToFuture: Boolean,
    //expansionToPastItems: Int,
    onRefresh: suspend (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState()
) {
    val pullToRefreshState = rememberPullToRefreshState()
    var isLoading by remember { mutableStateOf(false) }
    var hasLoadedItems by remember { mutableStateOf(false) }

    val reachedBottom: Boolean by remember {
        derivedStateOf {
            val lastVisibleItem = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem?.index == lazyListState.layoutInfo.totalItemsCount - 1 &&
                    lazyListState.canScrollForward.not()
        }
    }

    LaunchedEffect(reachedBottom, isLoading) {
        if (reachedBottom && hasLoadedItems && !isLoading && isExpandingToFuture.not()){
            isLoading = true
            onRefresh(false)
            isLoading = false
        }
    }


    LaunchedEffect(items) {
        hasLoadedItems = items.isNotEmpty()
    }

    Box(
        modifier = modifier
            .nestedScroll(pullToRefreshState.nestedScrollConnection)
    ) {
        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items/*, key = {it.arrivalDateTime.toString() + it.usedSegmentTypes.toString()}*/) {
                content(it)
            }

            if (isExpandingToFuture) {
                Log.i("DEBUG", "Displaying loading indicator")
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }

        if (pullToRefreshState.isRefreshing) {
            LaunchedEffect(Unit) {
                onRefresh(true)
            }
        }

        LaunchedEffect(isExpandingToFuture) {
            if (isExpandingToFuture) {
                lazyListState.scrollToItem(items.size) // Scroll to bottom to ensure visibility of the indicator
            }
        }


        LaunchedEffect(isExpandingToPast) {
            if (isExpandingToPast) {
                pullToRefreshState.startRefresh()
            } else {
                pullToRefreshState.endRefresh()
            }
        }

        if (pullToRefreshState.progress > 0.5f || isExpandingToPast) {
            PullToRefreshContainer(
                state = pullToRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                indicator = { state ->
                    Box(
                        modifier = Modifier
                            .size(40.dp) // Set the size of the box for centering
                            .wrapContentSize(Alignment.Center) // Center the content
                    ) {
                        if (state.isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(30.dp) // Adjust the size as needed
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.arrow_back),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(30.dp) // Adjust the size as needed
                                    .rotate(90f)
                            )
                        }
                    }
                }
            )
        }

    }
}

