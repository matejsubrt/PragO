package com.example.prago.view.searchScreen

import android.content.Context
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chargemap.compose.numberpicker.Hours
import com.example.prago.R
import com.example.prago.viewModel.AppViewModel
//import com.example.prago.viewModel.SharedViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


fun generateDateSequence(context: Context, startDate: LocalDate, days: Int): List<String> {
    val formatter = DateTimeFormatter.ofPattern("E d.M.") // Format: Mo/Tue/... DD.MM.
    val sequence = mutableListOf<String>()

    for (i in 0 until days) {
        val currentDate = startDate.plusDays(i.toLong())
        when (i) {
            0 -> sequence.add(context.getString(R.string.today))
            1 -> sequence.add(context.getString(R.string.tomorrow))
            else -> sequence.add(currentDate.format(formatter))
        }
    }

    return sequence
}


fun getLocalTimeFromHours(hours: Hours): LocalTime {
    return LocalTime.of(hours.hours, hours.minutes)
}


// The following code was taken from https://gist.github.com/slaviboy/50e8d852f3e46543aad061c4141af87a and slightly modified
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


@Composable
fun DatePicker(viewModel: AppViewModel, onDateChanged: (LocalDate) -> Unit){
    val startDate = LocalDate.now()
    val days = 14

    val dateLabelSequence = generateDateSequence(LocalContext.current, startDate, days)
    val dateMap = dateLabelSequence.mapIndexed { index, label -> label to startDate.plusDays(index.toLong()) }.toMap()

    val selectedDate by viewModel.selectedDate.collectAsState()

    Log.i("DEBUG", "Selected date: $selectedDate")


    NonInfiniteCircularList(
        width = 120.dp,
        itemHeight = 50.dp,
        items = dateLabelSequence,
        initialItem = dateMap.keys.first { dateMap[it] == selectedDate },
        textStyle = TextStyle(fontSize = 14.sp),
        textColor = Color.LightGray,
        selectedTextColor = Color.White,
        onItemSelected = { index, item ->
            val date = dateMap[item] ?: LocalDate.MIN
            Log.i("DEBUG", "Selected date: $date")
            onDateChanged(date)
        }
    )
}

@Composable
fun TimePicker(viewModel: AppViewModel, onTimeChanged: (LocalTime) -> Unit){
    val selectedTime by viewModel.selectedTime.collectAsState()

    Row(
        verticalAlignment = Alignment.CenterVertically
    ){
        InfiniteCircularList(
            width = 80.dp,
            itemHeight = 50.dp,
            items = (0..23).toList(),
            initialItem = selectedTime.hour,
            textStyle = TextStyle(fontSize = 14.sp),
            textColor = Color.LightGray,
            selectedTextColor = Color.White,
            onItemSelected = { index, item ->
                val time = selectedTime.withHour(item)
                onTimeChanged(time)
            },
            zeroPad = false
        )
        Text(
            text = ":",
            style = TextStyle(fontSize = 14.sp, ),
            color = Color.White
        )
        InfiniteCircularList(
            width = 80.dp,
            itemHeight = 50.dp,
            items = (0..55 step 5).toList(),
            initialItem = selectedTime.minute,
            textStyle = TextStyle(fontSize = 14.sp),
            textColor = Color.LightGray,
            selectedTextColor = Color.White,
            onItemSelected = { index, item ->
                val time = selectedTime.withMinute(item)
                onTimeChanged(time)
            },
            zeroPad = true
        )
    }
}

