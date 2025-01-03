package com.example.prago.view.searchScreen.dateTimeSelecting

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prago.R
import com.example.prago.viewModel.AppViewModel
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




@Composable
fun DatePicker(viewModel: AppViewModel, onDateChanged: (LocalDate) -> Unit){
    val startDate = LocalDate.now()
    val days = 14

    val dateLabelSequence = generateDateSequence(LocalContext.current, startDate, days)
    val dateMap = dateLabelSequence.mapIndexed { index, label -> label to startDate.plusDays(index.toLong()) }.toMap()

    val selectedDate by viewModel.selectedDate.collectAsState()


    NonInfiniteCircularList(
        width = 120.dp,
        itemHeight = 50.dp,
        items = dateLabelSequence,
        initialItem = dateMap.keys.first { dateMap[it] == selectedDate },
        textStyle = TextStyle(fontSize = 14.sp),
        textColor = Color.LightGray,
        selectedTextColor = MaterialTheme.colorScheme.onSurface,
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
    //val currentlyDepartureNow by viewModel.departureNow.collectAsState()



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
            selectedTextColor = MaterialTheme.colorScheme.onSurface,
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
            initialItem = selectedTime.minute / 5 * 5,
            textStyle = TextStyle(fontSize = 14.sp),
            textColor = Color.LightGray,
            selectedTextColor = MaterialTheme.colorScheme.onSurface,
            onItemSelected = { index, item ->
                val time = selectedTime.withMinute(item)
                onTimeChanged(time)
            },
            zeroPad = true
        )
    }
}

