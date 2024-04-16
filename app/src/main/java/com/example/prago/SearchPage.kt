package com.example.prago

import SharedViewModel
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.commandiron.wheel_picker_compose.WheelDatePicker
import com.commandiron.wheel_picker_compose.WheelTimePicker
import com.commandiron.wheel_picker_compose.core.TimeFormat
import com.commandiron.wheel_picker_compose.core.WheelPickerDefaults
import com.example.prago.ui.theme.PragOTheme
import khttp.post
import khttp.responses.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun SearchPage(){
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(){
            MainTopBar()
            Body()
        }
    }
}

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
            modifier = Modifier.fillMaxSize().padding(4.dp),
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
        size = DpSize(160.dp, 40.dp),
        rowCount = 1,
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
        size = DpSize(100.dp, 40.dp),
        rowCount = 1,
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
    fromText: String,
    toText: String,
    onFromValueChange: (String) -> Unit,
    onToValueChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(start = 8.dp, top = 0.dp, end = 8.dp, bottom = 4.dp)

    ) {
        Column() {
            // Label "From" with text input
            LabelWithTextInput(label = "From:", placeholder = "Source stop", text = fromText, onTextChange = onFromValueChange)

            // Label "To" with text input
            LabelWithTextInput(label = "To:", placeholder = "Destination stop", text = toText, onTextChange = onToValueChange)
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
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(labels) { index, label ->
            if (index == labels.size - 1 && useSharedBikes) { // Check if it's the last slider and useSharedBikes is true
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
            } else if (index != labels.size - 1) { // Check if it's not the last slider
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






@OptIn(ExperimentalSerializationApi::class)
@Composable
fun Body(){
    val navController = LocalNavController.current
    val viewModel = LocalSharedViewModel.current


    var slidersInputVisible by remember { mutableStateOf(false) }
    // Mutable state variables
    var fromText by remember { mutableStateOf("") }
    var toText by remember { mutableStateOf("") }
    var useSharedBikes by remember { mutableStateOf(false) }
    var transferBuffer by remember { mutableStateOf(2f) }
    var transferLength by remember { mutableStateOf(1f) }
    var comfortPreference by remember { mutableStateOf(2f) }
    var bikeTripBuffer by remember { mutableStateOf(2f) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTime by remember { mutableStateOf(LocalTime.now()) }
    var byEarliestDeparture by remember { mutableStateOf(true)}

    Column(modifier = Modifier.padding(16.dp)) {
        TextInput(fromText = fromText, toText = toText, onFromValueChange = {fromText = it}, onToValueChange = {toText = it})

        Spacer(modifier = Modifier.height(8.dp))

        DateTimePickerRow(
            byEarliestDeparture = byEarliestDeparture,
            onArrDepChange = { byEarliestDeparture = it },
            onDateChanged = { selectedDate = it},
            onTimeChanged = { selectedTime = it}
        )

        Spacer(modifier = Modifier.height(8.dp))

        LabelWithToggleSwitch(label = "Use Shared Bikes", checked = useSharedBikes) { useSharedBikes = it }

        Spacer(modifier = Modifier.height(8.dp))


        Box(
            modifier = Modifier
                .clickable { slidersInputVisible = !slidersInputVisible }
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        AnimatedVisibility(visible = slidersInputVisible) {
            SlidersInput(
                labels = sliderLabels,
                values = listOf(transferBuffer, transferLength, comfortPreference, bikeTripBuffer),
                onValueChanges = listOf(
                    { transferBuffer = it },
                    { transferLength = it },
                    { comfortPreference = it },
                    { bikeTripBuffer = it }
                ),
                maxValues = sliderMaxValues,
                labelLists = labelLists,
                useSharedBikes = useSharedBikes
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        val settings = SearchSettings(
                            walkingPace = 12, // TODO: add long time settings
                            cyclingPace = 5,
                            bikeUnlockTime = 30,
                            bikeLockTime = 15,
                            useSharedBikes = useSharedBikes,
                            bikeMax15Minutes = true,
                            transferTime = transferBuffer.toInt(),
                            comfortBalance = comfortPreference.toInt(),
                            walkingPreference = transferLength.toInt(),
                            bikeTripBuffer = bikeTripBuffer.toInt()
                        )
                        val request = StopToStopRequest(
                            srcStopName = fromText,
                            destStopName = toText,
                            dateTime = selectedDate.atTime(selectedTime).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")),
                            byEarliestDeparture = byEarliestDeparture,
                            settings = settings
                        )

                        val response: Response = khttp.post(
                            url = "http://prago.xyz/connection/stop-to-stop",
                            json = request.toJsonObject()
                        )

                        val connectionSearchResult = Json.decodeFromString<ConnectionSearchResult>(response.text)
                        withContext(Dispatchers.Main) {
                            viewModel.searchResult.value = connectionSearchResult
                            navController.navigate("resultPage")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            ,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(256.dp)
                .height(64.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ){
            Text(
                text = "Search",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
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
fun LabelWithTextInput(label: String, placeholder: String, text: String, onTextChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = placeholder) }
        )
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
fun SliderWithValue(label: String, value: Float, onValueChange: (Float) -> Unit, maxValue: Float) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, fontWeight = FontWeight.Bold)
        Slider(
            value = value, // Initial value
            onValueChange = onValueChange,
            valueRange = 0f..maxValue,
            steps = maxValue.toInt() - 1,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondary,
                thumbColor = MaterialTheme.colorScheme.primary
            )
        )
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
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.secondary,
                    thumbColor = MaterialTheme.colorScheme.primary
                )
            )
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
        SearchPage()
    }
}