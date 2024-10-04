package com.example.prago.composables

import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

//fun normalizeCzech(input: String): String {
//    val original = listOf('ě', 'š', 'č', 'ř', 'ž', 'ý', 'á', 'í', 'é', 'ú', 'ů', 'ť', 'ď', 'ň', 'ó')
//    val replacement = listOf('e', 's', 'c', 'r', 'z', 'y', 'a', 'i', 'e', 'u', 'u', 't', 'd', 'n', 'o')
//
//    var result = input
//    for ((orig, repl) in original.zip(replacement)) {
//        result = result.replace(orig, repl)
//    }
//    return result
//}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AutoComplete(optionsList: List<String>, labelText: String, placeholder: String, text: String, onTextChange: (String) -> Unit) {
//    var stopName by remember {
//        mutableStateOf("")
//    }
//
//    var textFieldSize by remember {
//        mutableStateOf(Size.Zero)
//    }
//
//    var expanded by remember {
//        mutableStateOf(false)
//    }
//    val interactionSource = remember {
//        MutableInteractionSource()
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable(
//                interactionSource = interactionSource,
//                indication = null,
//                onClick = {
//                    expanded = false
//                }
//            )
//    ) {
//
//        Text(
//            text = labelText,
//            fontWeight = FontWeight.Bold
//        )
//
//        OutlinedTextField(
//            modifier = Modifier
//                .fillMaxWidth()
//                .onGloballyPositioned { coordinates ->
//                    textFieldSize = coordinates.size.toSize()
//                },
//            value = text,
//            onValueChange = { newValue ->
//                stopName = newValue
//                expanded = true
//                onTextChange(newValue)
//            },
//            placeholder = { Text(placeholder) },
//            keyboardOptions = KeyboardOptions(
//                keyboardType = KeyboardType.Text,
//                imeAction = ImeAction.Done
//            ),
//            singleLine = true
//        )
//
//        DropdownMenu(
//            expanded = expanded,
//            onDismissRequest = { expanded = false },
//            modifier = Modifier
//                .heightIn(min = 0.dp, max = 150.dp)
//                .background(MaterialTheme.colorScheme.surface),
//            properties = PopupProperties(focusable = false)
//        ) {
//            val sortedList = optionsList.sorted()
//            val filteredList = if (stopName.isEmpty()) {
//                sortedList.take(10)
//            } else {
//                sortedList.filter { item ->
//                    normalizeCzech(item.lowercase()).contains(normalizeCzech(stopName.lowercase()))
//                }.take(10)
//            }
//
//            filteredList.forEach { item ->
//                DropdownMenuItem(
//                    onClick = {
//                        stopName = item
//                        expanded = false
//                        onTextChange(item)
//                    },
//                    text = { Text(text = item) }
//                )
//            }
//        }
//
//    }
//}


//@Composable
//fun StopNameOption(
//    title: String,
//    onSelect: (String) -> Unit
//) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable {
//                onSelect(title)
//            }
//            .padding(10.dp)
//    ) {
//        Text(text = title, fontSize = 16.sp)
//    }
//
//}



@Composable
fun ArrivalDepartureButton(
    isDeparture: Boolean,
    onValueChanged: (Boolean) -> Unit
) {
    val icon = if (isDeparture) {
        R.drawable.departure // Icon for departure
    } else {
        R.drawable.arrival // Icon for arrival
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .padding(all = 4.dp)
            .clickable { onValueChanged(!isDeparture) }
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
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
}

@Composable
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
}

@Composable
fun DateTimePickerRow(
    byEarliestDeparture: Boolean,
    onArrDepChange: (Boolean) -> Unit,
    onTimeChanged: (LocalTime) -> Unit,
    onDateChanged: (LocalDate) -> Unit
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            DatePicker(onDateChanged)
            Spacer(modifier = Modifier.width(4.dp))
            TimePicker(onTimeChanged)
        }
        ArrivalDepartureButton(
            isDeparture = byEarliestDeparture,
            onValueChanged = onArrDepChange
        )
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
            // Pass the stopNameList to the AutoComplete functions
            //AutoComplete(optionsList = stopNameList, labelText = "From:", placeholder = "Source stop", text = fromText, onTextChange = onFromValueChange)
            //AutoComplete(optionsList = stopNameList, labelText = "To:", placeholder = "Destination stop", text = toText, onTextChange = onToValueChange)
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
    useSharedBikes: Boolean // Add a parameter to determine if the slider should be shown
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

            // SlidersInput composable, conditionally visible based on slidersInputVisible
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

            DateTimePickerRow(
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
                                502 -> {
                                    withContext(Dispatchers.Main) {
                                        errorMessage = "The server is currently down. Please try again later."
                                        showDialog = true
                                    }
                                }
                                404 -> {
                                    withContext(Dispatchers.Main) {
                                        errorMessage = "The connection could not be found. Please try changing the search parameters."
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
                .border(1.5.dp, MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.small)
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