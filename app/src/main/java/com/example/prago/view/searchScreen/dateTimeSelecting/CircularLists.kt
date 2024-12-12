package com.example.prago.view.searchScreen.dateTimeSelecting

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

// The following code was taken from https://gist.github.com/slaviboy/50e8d852f3e46543aad061c4141af87a and slightly modified to fit this project
@Composable
fun InfiniteCircularList(
    width: Dp,
    itemHeight: Dp,
    numberOfDisplayedItems: Int = 3,
    items: List<Int>,
    initialItem: Int,
    itemScaleFact: Float = 1.5f,
    textStyle: TextStyle,
    textColor: Color,
    selectedTextColor: Color,
    onItemSelected: (index: Int, item: Int) -> Unit = { _, _ -> },
    zeroPad: Boolean
) {
    val itemHalfHeight = LocalDensity.current.run { itemHeight.toPx() / 2f }
    val scrollState = rememberLazyListState(0)
    var lastSelectedIndex by remember {
        mutableStateOf(0)
    }
    var itemsState by remember {
        mutableStateOf(items)
    }
    LaunchedEffect(items) {
        var targetIndex = items.indexOf(initialItem) - 1
        targetIndex += ((Int.MAX_VALUE / 2) / items.size) * items.size
        itemsState = items
        lastSelectedIndex = targetIndex
        scrollState.scrollToItem(targetIndex)
    }
    LazyColumn(
        modifier = Modifier
            .width(width)
            .height(itemHeight * numberOfDisplayedItems),
        state = scrollState,
        flingBehavior = rememberSnapFlingBehavior(
            lazyListState = scrollState
        )
    ) {
        items(
            count = Int.MAX_VALUE,
            itemContent = { i ->
                val item = itemsState[i % itemsState.size]
                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            val y = coordinates.positionInParent().y - itemHalfHeight
                            val parentHalfHeight = (coordinates.parentCoordinates?.size?.height ?: 0) / 2f
                            val isSelected = (y > parentHalfHeight - itemHalfHeight && y < parentHalfHeight + itemHalfHeight)
                            if (isSelected && lastSelectedIndex != i) {
                                onItemSelected(i % itemsState.size, item)
                                lastSelectedIndex = i
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if(zeroPad) String.format("%02d", item) else item.toString(),
                        style = textStyle,
                        color = if (lastSelectedIndex == i) {
                            selectedTextColor
                        } else {
                            textColor
                        },
                        fontSize = if (lastSelectedIndex == i) {
                            textStyle.fontSize * itemScaleFact
                        } else {
                            textStyle.fontSize
                        }
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> NonInfiniteCircularList(
    width: Dp,
    itemHeight: Dp,
    numberOfDisplayedItems: Int = 3,
    items: List<T>,
    initialItem: T,
    itemScaleFact: Float = 1.5f,
    textStyle: TextStyle,
    textColor: Color,
    selectedTextColor: Color,
    onItemSelected: (index: Int, item: T) -> Unit = { _, _ -> }
) {
    val itemHalfHeight = LocalDensity.current.run { itemHeight.toPx() / 2f }
    val scrollState = rememberLazyListState()
    var lastSelectedIndex by remember { mutableStateOf(0) }

    val initialIndex = items.indexOf(initialItem).coerceAtLeast(0)

    LaunchedEffect(initialItem) {
        scrollState.scrollToItem(initialIndex)
        lastSelectedIndex = initialIndex
    }

    LazyColumn(
        modifier = Modifier
            .width(width)
            .height(itemHeight * numberOfDisplayedItems)
            .scrollable(
                state = ScrollState(0), // To be independent on parent scrolling
                orientation = Orientation.Vertical,
                enabled = true
            ),
        state = scrollState,
        flingBehavior = rememberSnapFlingBehavior(lazyListState = scrollState)
    ) {
        item {
            Spacer(modifier = Modifier.height(itemHeight))
        }
        itemsIndexed(items) { index, item ->
            Box(
                modifier = Modifier
                    .height(itemHeight)
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        val y = coordinates.positionInParent().y - itemHalfHeight
                        val parentHalfHeight =
                            (coordinates.parentCoordinates?.size?.height ?: 0) / 2f
                        val isSelected =
                            (y > parentHalfHeight - itemHalfHeight && y < parentHalfHeight + itemHalfHeight)
                        if (isSelected && lastSelectedIndex != index) {
                            onItemSelected(index, item)
                            lastSelectedIndex = index
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.toString(),
                    style = textStyle,
                    color = if (lastSelectedIndex == index) selectedTextColor else textColor,
                    fontSize = if (lastSelectedIndex == index) {
                        textStyle.fontSize * itemScaleFact
                    } else {
                        textStyle.fontSize
                    }
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(itemHeight))
        }
    }
}

// End of non-original code
