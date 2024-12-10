@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.prago.composables.resultScreen

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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import com.example.prago.model.dataClasses.ConnectionSearchResult
import com.example.prago.viewModel.AppViewModel
import kotlinx.coroutines.launch

/*
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

    val canScrollForward by remember {
        derivedStateOf {
            lazyListState.canScrollForward
        }
    }

    LaunchedEffect(canScrollForward, isLoading, hasLoadedItems) {
        Log.i("DEBUG", "Can scroll forward: ${canScrollForward}, hasLoadedItems: $hasLoadedItems, isLoading: ${!isLoading}, isExpandingToFuture: ${isExpandingToFuture.not()}")
        if(hasLoadedItems && !isLoading && !isExpandingToFuture){
            if(!canScrollForward){
                Log.i("DEBUG", "Loading more items")
                isLoading = true
                onRefresh(false)
                isLoading = false
            }
            if(!canScrollForward){
                Log.i("DEBUG", "Loading more items")
                isLoading = true
                onRefresh(false)
                isLoading = false
            }
            if(!canScrollForward){
                Log.i("DEBUG", "Loading more items")
                isLoading = true
                onRefresh(false)
                isLoading = false
            }
        }
    }


    LaunchedEffect(items) {
        hasLoadedItems = items.isNotEmpty()
        Log.i("DEBUG", "Has loaded items: $hasLoadedItems")
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
            items(items) {
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

        //if (pullToRefreshState.progress > 0.5f || isExpandingToPast) {
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
        //}

    }
}*/



@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
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

    val pullToRefreshState = rememberPullRefreshState(
        refreshing = isExpandingToPast,
        onRefresh = {
            scope.launch{
                onRefresh(true)
            }

        }
    )

    val canScrollForward by remember {
        derivedStateOf {
            lazyListState.canScrollForward
        }
    }
    var isLoading by remember { mutableStateOf(false) }






    LaunchedEffect(canScrollForward, isLoading, items) {
        if((!isLoading && !isExpandingToFuture) || items.size < 5){
            if(!canScrollForward){
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
            if (isExpandingToFuture) {
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
            item{
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ){
                    CircularProgressIndicator()
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