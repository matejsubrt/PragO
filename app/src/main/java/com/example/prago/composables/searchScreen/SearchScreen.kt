package com.example.prago.composables.searchScreen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prago.R
import com.example.prago.activities.LocalNavController
import com.example.prago.activities.LocalSharedViewModel
import com.example.prago.composables.MainTopBar
import com.example.prago.ui.theme.PragOTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi


@Composable
fun SearchScreen(){
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column {
            MainTopBar()
            Body()
        }
    }
}

/*@Composable
fun DateTimePickerRow(
    viewModel: SharedViewModel,
    byEarliestDeparture: Boolean,
    onArrDepChange: (Boolean) -> Unit,
    onTimeChanged: (LocalTime) -> Unit,
    onDateChanged: (LocalDate) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth().height(48.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp) // Padding to ensure content is not cut off
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Date Picker
                //DatePicker(viewModel, onDateChanged)

                DateTimeBottomSheet(viewModel = viewModel, byEarliestDeparture = byEarliestDeparture, onArrDepChange = onArrDepChange, onDateChanged = onDateChanged, onTimeChanged = onTimeChanged)

                //Spacer(modifier = Modifier.width(4.dp)) // Spacing between Date and Time Pickers

                // Time Picker
                //TimePicker(viewModel, onTimeChanged)

                // Spacer to push the button to the far right
                //Spacer(modifier = Modifier.weight(1f))

                // Arrival/Departure Button
//                ArrivalDepartureButton(
//                    isDeparture = byEarliestDeparture,
//                    onValueChanged = onArrDepChange
//                )
            }
        }
    }
}*/



@OptIn(ExperimentalSerializationApi::class)
@Composable
fun Body(){
    val navController = LocalNavController.current
    val viewModel = LocalSharedViewModel.current
    //val stopListDataStore = LocalStopListDataStore.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val sliderLabels = listOf(
        stringResource(R.string.transfer_buffer),
        stringResource(R.string.transfer_length),
        stringResource(R.string.comfort_preference),
        stringResource(R.string.bike_trip_buffer)
    )
    val sliderMaxValues = listOf(3f, 2f, 3f, 3f)

    val transferBufferLabels = listOf(
        stringResource(R.string.buffer_none),
        stringResource(R.string.buffer_short),
        stringResource(R.string.buffer_normal),
        stringResource(R.string.buffer_long)
    )
    val transferLengthLabels = listOf(
        stringResource(R.string.long_transfer) + "\n (750m)",
        stringResource(R.string.medium_transfer) + "\n  (400m)",
        stringResource(R.string.short_transfer) + "\n  (250m)"
    )
    val comfortLabels = listOf(stringResource(R.string.comfort_shortest_extreme),
        stringResource(R.string.comfort_shortest),
        stringResource(R.string.comfort_balanced), stringResource(R.string.comfort_least_transfers)
    )
    val bikeTripBufferLabels = listOf(
        stringResource(R.string.buffer_none),
        stringResource(R.string.buffer_short),
        stringResource(R.string.buffer_normal),
        stringResource(R.string.buffer_long)
    )


    val labelLists = listOf(transferBufferLabels, transferLengthLabels, comfortLabels, bikeTripBufferLabels)


    // TODO: Implement better error handling - user-friendly error messages and popups
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("OK")
                }
            },
            title = { Text(text = stringResource(R.string.error)) },
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
                onDateChanged = { viewModel.selectedDate.value = it; viewModel.departureNow.value = false},
                onTimeChanged = { viewModel.selectedTime.value = it; viewModel.departureNow.value = false},
                onNowSelected = { viewModel.departureNow.value = true; viewModel.byEarliestDeparture.value = true }
            )

            Spacer(modifier = Modifier.height(8.dp))

            LabelWithToggleSwitch(label = stringResource(R.string.use_shared_bikes), checked = viewModel.useSharedBikes.value) {
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
                    Log.i("DEBUG", "Search button clicked")
                    viewModel.startSearch(
                        context = context,
                        navController = navController,
                        showDialog = { showDialog = it },
                        setErrorMessage = { errorMessage = it }
                    )
                },
                modifier = Modifier
                    .width(256.dp)
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = stringResource(R.string.search),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

/*@Preview
@Composable
fun HeaderPreview(){
    PragOTheme(darkTheme = false){
        MainTopBar()
    }

}*/

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    PragOTheme(darkTheme = true) {
        SearchScreen()
    }
}