package com.example.prago.composables.searchScreen

import android.content.Context
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.chargemap.compose.numberpicker.FullHours
import com.chargemap.compose.numberpicker.Hours
import com.chargemap.compose.numberpicker.HoursNumberPicker
import com.chargemap.compose.numberpicker.ListItemPicker
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



@Composable
fun DatePicker(viewModel: AppViewModel, onDateChanged: (LocalDate) -> Unit){
    val startDate = LocalDate.now()
    val days = 14

    val dateLabelSequence = generateDateSequence(LocalContext.current, startDate, days)
    val dateMap = dateLabelSequence.mapIndexed { index, label -> label to startDate.plusDays(index.toLong()) }.toMap()

    val selectedDate by viewModel.selectedDate.collectAsState()


    ListItemPicker(
        modifier = Modifier,
        label = { it },
        value = dateMap.keys.first { dateMap[it] == selectedDate },
        onValueChange = { label ->
            onDateChanged(selectedDate)
        },
        list = dateLabelSequence,
        dividersColor = MaterialTheme.colorScheme.primary,
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
            fontFamily = MaterialTheme.typography.bodyMedium.fontFamily
        )
    )
}

@Composable
fun TimePicker(viewModel: AppViewModel, onTimeChanged: (LocalTime) -> Unit){
    val selectedTime by viewModel.selectedTime.collectAsState()

    var pickerValue by remember { mutableStateOf<Hours>(FullHours(viewModel.selectedTime.value.hour, viewModel.selectedTime.value.minute)) }

    HoursNumberPicker(
        modifier = Modifier.width(160.dp),
        dividersColor = MaterialTheme.colorScheme.primary,
        leadingZero = true,
        value = pickerValue,
        onValueChange = {
            pickerValue = it
            onTimeChanged(selectedTime)
        },
        hoursDivider = {
            Text(
                modifier = Modifier.size(24.dp),
                textAlign = TextAlign.Center,
                text = ":"
            )
        },
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
            fontFamily = MaterialTheme.typography.bodyMedium.fontFamily
        ),
        // minutes in intervals of 5, infinite loop
        minutesRange = 0..55 step 5
    )
}

