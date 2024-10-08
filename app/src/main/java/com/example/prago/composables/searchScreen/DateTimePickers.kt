package com.example.prago.composables.searchScreen

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.chargemap.compose.numberpicker.FullHours
import com.chargemap.compose.numberpicker.Hours
import com.chargemap.compose.numberpicker.HoursNumberPicker
import com.chargemap.compose.numberpicker.ListItemPicker
import com.example.prago.viewModels.SharedViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


fun generateDateSequence(startDate: LocalDate, days: Int): List<String> {
    val formatter = DateTimeFormatter.ofPattern("E d.M.") // Format: Mo/Tue/... DD.MM.
    val sequence = mutableListOf<String>()

    for (i in 0 until days) {
        val currentDate = startDate.plusDays(i.toLong())
        when (i) {
            0 -> sequence.add("Today")
            1 -> sequence.add("Tomorrow")
            else -> sequence.add(currentDate.format(formatter))
        }
    }

    return sequence
}


fun getLocalTimeFromHours(hours: Hours): LocalTime {
    return LocalTime.of(hours.hours, hours.minutes)
}



/*@Composable
fun DatePicker(onDateSelected: (LocalDate) -> Unit) {
    WheelDatePicker(
        startDate = LocalDate.now(),
        minDate = LocalDate.now(),
        maxDate = LocalDate.now().plusDays(13),
        size = DpSize(132.dp, 80.dp),
        rowCount = 3,
        textStyle = MaterialTheme.typography.titleSmall,
        textColor = MaterialTheme.colorScheme.secondary,
        selectorProperties = WheelPickerDefaults.selectorProperties(
            enabled = true,
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        ),
        onSnappedDate = onDateSelected
    )
}*/

@Composable
fun DatePicker(viewModel: SharedViewModel, onDateChanged: (LocalDate) -> Unit){
    val startDate = LocalDate.now()
    val days = 14

    val dateLabelSequence = generateDateSequence(startDate, days)
    val dateMap = dateLabelSequence.mapIndexed { index, label -> label to startDate.plusDays(index.toLong()) }.toMap()

    var selectedDate by remember { mutableStateOf(viewModel.selectedDate.value) }


    ListItemPicker(
        modifier = Modifier,
        label = { it },
        value = dateMap.keys.first { dateMap[it] == selectedDate },
        onValueChange = { label ->
            selectedDate = dateMap[label] ?: startDate
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

/*@Composable
fun TimePicker(onTimeSelected: (LocalTime) -> Unit) {
    WheelTimePicker(
        startTime = LocalTime.now(),
        timeFormat = TimeFormat.HOUR_24,
        size = DpSize(86.dp, 80.dp),
        rowCount = 3,
        textStyle = MaterialTheme.typography.titleSmall,
        textColor = MaterialTheme.colorScheme.secondary,
        selectorProperties = WheelPickerDefaults.selectorProperties(
            enabled = true,
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        ),
        onSnappedTime = onTimeSelected
    )
}*/

@Composable
fun TimePicker(viewModel: SharedViewModel, onTimeChanged: (LocalTime) -> Unit){
    var selectedTime by remember { mutableStateOf(viewModel.selectedTime.value) }

    var pickerValue by remember { mutableStateOf<Hours>(FullHours(viewModel.selectedTime.value.hour, viewModel.selectedTime.value.minute)) }

    HoursNumberPicker(
        modifier = Modifier.width(160.dp),
        dividersColor = MaterialTheme.colorScheme.primary,
        leadingZero = true,
        value = pickerValue,
        onValueChange = {
            pickerValue = it
            selectedTime = getLocalTimeFromHours(it)
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
        )
    )
}

