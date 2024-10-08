package com.example.prago.composables

import android.content.Context
import android.util.Log
import android.widget.TimePicker
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chargemap.compose.numberpicker.FullHours
import com.chargemap.compose.numberpicker.Hours
import com.chargemap.compose.numberpicker.HoursNumberPicker
import com.chargemap.compose.numberpicker.ListItemPicker
import com.commandiron.wheel_picker_compose.WheelDatePicker
import com.commandiron.wheel_picker_compose.WheelTimePicker
import com.commandiron.wheel_picker_compose.core.TimeFormat
import com.commandiron.wheel_picker_compose.core.WheelPickerDefaults
import com.example.prago.activities.LocalNavController
import com.example.prago.activities.LocalSharedViewModel
import com.example.prago.activities.LocalStopListDataStore
import com.example.prago.R
import com.example.prago.dataClasses.SearchSettings
import com.example.prago.dataClasses.StopToStopRequest
import com.example.prago.dataClasses.ConnectionSearchResult
import com.example.prago.activities.labelLists
import com.example.prago.activities.sliderLabels
import com.example.prago.activities.sliderMaxValues
import com.example.prago.dataClasses.toJsonObject
import com.example.prago.ui.theme.Gray33
import com.example.prago.ui.theme.PragOTheme
import com.example.prago.viewModels.SharedViewModel
import khttp.responses.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@Composable
fun SearchScreen(){
    Log.i("DEBUG", "SearchScreen")
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column {
            MainTopBar()
            Body()
            //AutoComplete(animals, "From:", "Source stop", "", {})
            //val viewModel = SearchViewModel()
            //SearchScreen(LocalSharedViewModel.current, LocalNavController.current, false)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AutoCompletePreview() {
    PragOTheme {
        //AutoComplete(animals, "From:", "Source stop", "", {})
    }
}


//@Composable
//fun ArrivalDepartureButton(
//    isDeparture: Boolean,
//    onValueChanged: (Boolean) -> Unit
//) {
//    val icon = if (isDeparture) {
//        R.drawable.departure // Icon for departure
//    } else {
//        R.drawable.arrival // Icon for arrival
//    }
//
//    Box(
//        modifier = Modifier
//            .size(48.dp)
//            .padding(all = 4.dp)
//            .clickable { onValueChanged(!isDeparture) }
//            .background(
//                color = MaterialTheme.colorScheme.surface,
//                shape = RoundedCornerShape(10.dp)
//            )
//            .border(
//                width = 2.dp,
//                color = MaterialTheme.colorScheme.primary,
//                shape = RoundedCornerShape(10.dp)
//            )
//    ) {
//        Icon(
//            painter = painterResource(id = icon),
//            contentDescription = null,
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(4.dp),
//            tint = MaterialTheme.colorScheme.secondary
//        )
//    }
//}

@Composable
fun ArrivalDepartureButton(
    modifier: Modifier,
    departureButton: Boolean,
    departureSelected: Boolean,
    onValueChanged: (Boolean) -> Unit
){
    val currentlySelected = departureButton && departureSelected || !departureButton && !departureSelected
    Button(
        onClick = {
            if (!currentlySelected) onValueChanged(departureButton)
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (currentlySelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = if (currentlySelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        ),
        modifier = modifier,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(text = if(departureButton) "Departure" else "Arrival")
    }
}

@Composable
fun ArrivalDepartureButtonPair(
    isDeparture: Boolean,
    onValueChanged: (Boolean) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        // Departure Button
//        Button(
//            onClick = { if (!isDeparture) onValueChanged(true) },
//            colors = ButtonDefaults.buttonColors(
//                containerColor = if (isDeparture) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiaryContainer,
//                contentColor = if (isDeparture) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
//            ),
//            modifier = Modifier.weight(1f).fillMaxHeight(),
//            shape = RoundedCornerShape(6.dp)
//        ) {
//            Text(text = "Departure")
//        }
        ArrivalDepartureButton(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            departureButton = true,
            departureSelected = isDeparture,
            onValueChanged = onValueChanged
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Arrival Button
//        Button(
//            onClick = { if (isDeparture) onValueChanged(false) },
//            colors = ButtonDefaults.buttonColors(
//                containerColor = if (!isDeparture) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiaryContainer,
//                contentColor = if (!isDeparture) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
//            ),
//            modifier = Modifier
//                .weight(1f)
//                .fillMaxHeight(),
//            shape = RoundedCornerShape(6.dp)
//        ) {
//            Text(text = "Arrival")
//        }
        ArrivalDepartureButton(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            departureButton = false,
            departureSelected = isDeparture,
            onValueChanged = onValueChanged
        )
    }
}


//@Composable
//fun DatePicker(onDateSelected: (LocalDate) -> Unit) {
//    WheelDatePicker(
//        startDate = LocalDate.now(),
//        minDate = LocalDate.now(),
//        maxDate = LocalDate.now().plusDays(13),
//        size = DpSize(132.dp, 80.dp),
//        rowCount = 3,
//        textStyle = MaterialTheme.typography.titleSmall,
//        textColor = MaterialTheme.colorScheme.secondary,
//        selectorProperties = WheelPickerDefaults.selectorProperties(
//            enabled = true,
//            shape = RoundedCornerShape(16.dp),
//            color = MaterialTheme.colorScheme.surface,
//            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
//        ),
//        onSnappedDate = onDateSelected
//    )
//}

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

//@Composable
//fun TimePicker(onTimeSelected: (LocalTime) -> Unit) {
//    WheelTimePicker(
//        startTime = LocalTime.now(),
//        timeFormat = TimeFormat.HOUR_24,
//        size = DpSize(86.dp, 80.dp),
//        rowCount = 3,
//        textStyle = MaterialTheme.typography.titleSmall,
//        textColor = MaterialTheme.colorScheme.secondary,
//        selectorProperties = WheelPickerDefaults.selectorProperties(
//            enabled = true,
//            shape = RoundedCornerShape(16.dp),
//            color = MaterialTheme.colorScheme.surface,
//            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
//        ),
//        onSnappedTime = onTimeSelected
//    )
//}

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

fun getLabelFromDate(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("E d.M.") // Format: Mo/Tue/... DD.MM.
    return when (date) {
        LocalDate.now() -> "Today"
        LocalDate.now().plusDays(1) -> "Tomorrow"
        else -> date.format(formatter)
    }
}

fun getLabelFromTime(time: LocalTime): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return time.format(formatter)
}

fun getLocalTimeFromHours(hours: Hours): LocalTime {
    return LocalTime.of(hours.hours, hours.minutes)
}

//@Composable
//fun DateTimePickerRow(
//    viewModel: SharedViewModel,
//    byEarliestDeparture: Boolean,
//    onArrDepChange: (Boolean) -> Unit,
//    onTimeChanged: (LocalTime) -> Unit,
//    onDateChanged: (LocalDate) -> Unit
//) {
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.SpaceBetween,
//        modifier = Modifier.fillMaxWidth().height(48.dp)
//    ) {
//        Box(
//            modifier = Modifier
//                .clip(RoundedCornerShape(6.dp))
//                .background(MaterialTheme.colorScheme.surface)
//                .padding(8.dp) // Padding to ensure content is not cut off
//        ) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                // Date Picker
//                //DatePicker(viewModel, onDateChanged)
//
//                DateTimeBottomSheet(viewModel = viewModel, byEarliestDeparture = byEarliestDeparture, onArrDepChange = onArrDepChange, onDateChanged = onDateChanged, onTimeChanged = onTimeChanged)
//
//                //Spacer(modifier = Modifier.width(4.dp)) // Spacing between Date and Time Pickers
//
//                // Time Picker
//                //TimePicker(viewModel, onTimeChanged)
//
//                // Spacer to push the button to the far right
//                //Spacer(modifier = Modifier.weight(1f))
//
//                // Arrival/Departure Button
////                ArrivalDepartureButton(
////                    isDeparture = byEarliestDeparture,
////                    onValueChanged = onArrDepChange
////                )
//            }
//        }
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimeBottomSheet(
    modifier: Modifier = Modifier,
    viewModel: SharedViewModel,
    byEarliestDeparture: Boolean,
    onArrDepChange: (Boolean) -> Unit,
    onTimeChanged: (LocalTime) -> Unit,
    onDateChanged: (LocalDate) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true // Prevent partial expansion
    )
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
            .height(36.dp)
            .fillMaxSize()
            .clickable(
                onClick = {
                    scope.launch {
                        // Move sheetState.show() inside coroutine to prevent UI thread blocking
                        sheetState.show()
                    }
                }
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = if (byEarliestDeparture) {
                R.drawable.departure // Icon for departure
            } else {
                R.drawable.arrival // Icon for arrival
            }

            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier
                    .height(24.dp)
                    .padding(start = 4.dp, end = 4.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.width(8.dp))

            val text = getLabelFromDate(viewModel.selectedDate.value) + " " + getLabelFromTime(viewModel.selectedTime.value)
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 17.sp
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Now",
                style = TextStyle(
                    fontSize = 17.sp,
                    color = Color.Gray
                ),
                modifier = Modifier.clickable(
                    onClick = {
                        onDateChanged(LocalDate.now())
                        onTimeChanged(LocalTime.now())
                    }
                )
            )
        }
    }

    if (sheetState.isVisible) {
        ModalBottomSheet(
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                }
            },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.background
        ) {
            Column(
                Modifier.padding(start = 12.dp, end = 12.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                ArrivalDepartureButtonPair(byEarliestDeparture, onArrDepChange)

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(start = 8.dp, top = 0.dp, end = 8.dp, bottom = 4.dp)
                    ) {
                        DatePicker(viewModel, onDateChanged)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(start = 8.dp, top = 0.dp, end = 8.dp, bottom = 4.dp)
                    ) {
                        TimePicker(viewModel, onTimeChanged)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .size(60.dp, 40.dp)
                            .clickable(
                                onClick = {
                                    scope.launch {
                                        sheetState.hide()
                                    }
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Done",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }
    }
}





@Composable
fun TextInput(
    context: Context,
    fromText: String,
    toText: String,
    onFromValueChange: (String) -> Unit,
    onToValueChange: (String) -> Unit
) {
    val navControler = LocalNavController.current

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(start = 8.dp, top = 0.dp, end = 8.dp, bottom = 4.dp)
    ) {
        Column() {
            LabelWithTextInput(label = "From:", placeholder = "Source stop", text = fromText, onTextChange = onFromValueChange, onClick = {navControler.navigate("fromStopSelect")})
            LabelWithTextInput(label = "To:", placeholder = "Destination stop", text = toText, onTextChange = onToValueChange, onClick = {navControler.navigate("toStopSelect")})
        }
    }
}



@Composable
fun SlidersInput(
    labels: List<String>,
    values: List<Float>,
    onValueChanges: List<(Float) -> Unit>,
    maxValues: List<Float>,
    labelLists: List<List<String>>,
    useSharedBikes: Boolean
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        labels.forEachIndexed { index, label ->
            if (index == labels.size - 1 && useSharedBikes) {
                val value = values[index]
                val maxValue = maxValues[index]
                val labelList = labelLists[index]

                val sliderValue = remember { mutableStateOf(value) }
                SliderWithLabels(
                    label = label,
                    value = sliderValue.value,
                    onValueChange = {
                        sliderValue.value = it
                        onValueChanges[index](it)
                    },
                    maxValue = maxValue,
                    labels = labelList
                )
            } else if (index != labels.size - 1) {
                val value = values[index]
                val maxValue = maxValues[index]
                val labelList = labelLists[index]

                SliderWithLabels(
                    label = label,
                    value = value,
                    onValueChange = onValueChanges[index],
                    maxValue = maxValue,
                    labels = labelList
                )
            }
        }
    }

}

@Composable
fun SlidersBox(
    labels: List<String>,
    values: List<Float>,
    onValueChanges: List<(Float) -> Unit>,
    maxValues: List<Float>,
    labelLists: List<List<String>>,
    useSharedBikes: Boolean
){
    var slidersInputVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 12.dp)
    ){
        Column(){
            Box(
                modifier = Modifier
                    .clickable {
                        slidersInputVisible = !slidersInputVisible
                    }
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(start = 8.dp, top = 0.dp, end = 8.dp, bottom = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Extended settings",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Expand Sliders",
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(if (slidersInputVisible) 180f else 0f),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            AnimatedVisibility(visible = slidersInputVisible) {
                SlidersInput(
                    labels = labels,
                    values = values,
                    onValueChanges = onValueChanges,
                    maxValues = maxValues,
                    labelLists = labelLists,
                    useSharedBikes = useSharedBikes
                )
            }
        }
    }
}






@OptIn(ExperimentalSerializationApi::class)
@Composable
fun Body(){
    val navController = LocalNavController.current
    val viewModel = LocalSharedViewModel.current
    val stopListDataStore = LocalStopListDataStore.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // TODO: Implement better error handling - user-friendly error messages and popups
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("OK")
                }
            },
            title = { Text(text = "Error") },
            text = { Text(errorMessage) }
        )
    }

    LazyColumn(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            TextInput(
                context = context,
                fromText = viewModel.fromText.value,
                toText = viewModel.toText.value,
                onFromValueChange = {viewModel.fromText.value = it},
                onToValueChange = {viewModel.toText.value = it}
            )

            Spacer(modifier = Modifier.height(8.dp))

            DateTimeBottomSheet(
                viewModel = viewModel,
                byEarliestDeparture = viewModel.byEarliestDeparture.value,
                onArrDepChange = { viewModel.byEarliestDeparture.value = it },
                onDateChanged = { viewModel.selectedDate.value = it},
                onTimeChanged = { viewModel.selectedTime.value = it}
            )

            Spacer(modifier = Modifier.height(8.dp))

            LabelWithToggleSwitch(label = "Use Shared Bikes", checked = viewModel.useSharedBikes.value) {
                viewModel.useSharedBikes.value = it
                scope.launch{
                    viewModel.saveBoolSetting("useSharedBikes", it)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            SlidersBox(
                labels = sliderLabels,
                values = listOf(viewModel.transferBuffer.value, viewModel.transferLength.value, viewModel.comfortPreference.value, viewModel.bikeTripBuffer.value),
                onValueChanges = listOf(
                    {
                        viewModel.transferBuffer.value = it
                        scope.launch{
                            viewModel.saveFloatSetting("transferBuffer", it)
                        }

                    },
                    {
                        viewModel.transferLength.value = it
                        scope.launch{
                            viewModel.saveFloatSetting("transferLength", it)
                        }
                    },
                    {
                        viewModel.comfortPreference.value = it
                        scope.launch{
                            viewModel.saveFloatSetting("comfortPreference", it)
                        }
                    },
                    {
                        viewModel.bikeTripBuffer.value = it
                        scope.launch {
                            viewModel.saveFloatSetting("bikeTripBuffer", it)
                        }
                    }
                ),
                maxValues = sliderMaxValues,
                labelLists = labelLists,
                useSharedBikes = viewModel.useSharedBikes.value
            )


            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    GlobalScope.launch(Dispatchers.IO) {
                        try {
                            val settings = SearchSettings(
                                walkingPace = viewModel.walkingPace.value ?: 12,
                                cyclingPace = viewModel.cyclingPace.value ?: 5,
                                bikeUnlockTime = viewModel.bikeUnlockTime.value ?: 30,
                                bikeLockTime = viewModel.bikeLockTime.value ?: 15,
                                useSharedBikes = viewModel.useSharedBikes.value,
                                bikeMax15Minutes = true,
                                transferTime = viewModel.transferBuffer.value.toInt(),
                                comfortBalance = viewModel.comfortPreference.value.toInt(),
                                walkingPreference = viewModel.transferLength.value.toInt(),
                                bikeTripBuffer = viewModel.bikeTripBuffer.value.toInt()
                            )
                            val request = StopToStopRequest(
                                srcStopName = viewModel.fromText.value,
                                destStopName = viewModel.toText.value,
                                dateTime = viewModel.selectedDate.value.atTime(viewModel.selectedTime.value).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")),
                                byEarliestDeparture = viewModel.byEarliestDeparture.value,
                                settings = settings
                            )

                            val response: Response = khttp.post(
                                url = "http://prago.xyz/connection/stop-to-stop",
                                json = request.toJsonObject()
                            )

                            when (response.statusCode) {
                                200 -> {
                                    val connectionSearchResult = Json.decodeFromString<ConnectionSearchResult>(response.text)
                                    withContext(Dispatchers.Main) {
                                        viewModel.searchResult.value = connectionSearchResult
                                        navController.navigate("resultPage")
                                    }
                                }
                                404 -> {
                                    withContext(Dispatchers.Main) {
                                        errorMessage = "The connection could not be found. Please try changing the search parameters."
                                        showDialog = true
                                    }
                                }
                                502 -> {
                                    withContext(Dispatchers.Main) {
                                        errorMessage = "The server is currently down. Please try again later."
                                        showDialog = true
                                    }
                                }
                                else -> {
                                    withContext(Dispatchers.Main) {
                                        //errorMessage = "Error: ${response.statusCode}. ${response.text}"
                                        errorMessage = response.text
                                        showDialog = true
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                errorMessage = "An error occurred: ${e.message}"
                                showDialog = true
                            }
                            e.printStackTrace()
                        }
                    }
                },
                modifier = Modifier
                    .width(256.dp)
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Search",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}




@Preview
@Composable
fun HeaderPreview(){
    PragOTheme(darkTheme = false){
        MainTopBar()
    }

}

@Composable
fun LabelWithTextInput(
    label: String,
    placeholder: String,
    text: String,
    onTextChange: (String) -> Unit,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, fontWeight = FontWeight.Bold)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable {
                    Log.i("DEBUG", "OnClick calling")
                    onClick()
                    Log.i("DEBUG", "OnClick finished")
                }
                .border(
                    1.5.dp,
                    MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.small
                )
                .background(Gray33)
                .padding(8.dp),
            contentAlignment = Alignment.CenterStart
        ){
            if(text != ""){
                Text(
                    text = text,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                )
            } else {
                Text(
                    text = placeholder,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }
    }
}


@Composable
fun LabelWithToggleSwitch(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(start = 8.dp, top = 0.dp, end = 8.dp, bottom = 0.dp)

    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color.Black,
                    checkedThumbColor = Color.White,
                    checkedTrackColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }

}

@Composable
fun SliderWithLabels(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    maxValue: Float,
    labels: List<String>
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 12.dp)
    ){
        Column(modifier = Modifier) {
            Text(text = label, fontWeight = FontWeight.Bold)
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = 0f..maxValue,
                steps = maxValue.toInt() - 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                colors = SliderDefaults.colors(
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.onSurface,
                    thumbColor = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                labels.forEachIndexed { index, labelText ->
                    Box(
                        modifier = Modifier
                            .rotate(-45f) // Rotate the box containing the text
                            .padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = labelText,
                            modifier = Modifier.align(Alignment.Center),
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 10.sp,
                            style = TextStyle(
                                lineHeight = 8.sp
                            )
                        )
                    }
                }
            }
        }
    }

}



@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    PragOTheme(darkTheme = true) {
        SearchScreen()
    }
}