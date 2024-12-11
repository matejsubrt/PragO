package com.example.prago.view.searchScreen

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.prago.view.LocalAppViewModel
//import com.example.prago.activities.LocalSharedViewModel
import com.example.prago.view.MainTopBar
import com.example.prago.ui.theme.PragOTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.ceil


@Composable
fun SearchScreen(){
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column {
            MainTopBar()
            Body()
        }
    }
}

@Composable
fun Body(){
    val viewModel = LocalAppViewModel.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val srcStopName by viewModel.fromSearchQuery.collectAsState()
    val destStopName by viewModel.toSearchQuery.collectAsState()
    val srcByLocation by viewModel.startByCoordinates.collectAsState()


    Log.i("DEBUG", "Start by coords: $srcByLocation")
    val fromText = if (srcByLocation) stringResource(R.string.current_location) else srcStopName
    val toText = destStopName
    val byEarliestDeparture by viewModel.byEarliestDeparture.collectAsState()

    val useSharedBikes by viewModel.useSharedBikes.collectAsState()
    val transferBuffer by viewModel.transferBuffer.collectAsState()
    val transferLength by viewModel.transferLength.collectAsState()
    val comfortPreference by viewModel.timeComfortBalance.collectAsState()
    val bikeTripBuffer by viewModel.bikeTripBuffer.collectAsState()
    val bikeMax15Min by viewModel.bikeMax15Minutes.collectAsState()

    val startingSearch by viewModel.startingSearch.collectAsState()


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
                fromText = fromText,
                toText = toText,
                onDirectionSwitch = {
                    val fromSearchQuery = viewModel.fromSearchQuery.value
                    viewModel.updateFromSearchQuery(viewModel.toSearchQuery.value)
                    viewModel.updateToSearchQuery(fromSearchQuery)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            DateTimeBottomSheet(
                viewModel = viewModel,
                byEarliestDeparture = byEarliestDeparture,
                onArrDepChange = {viewModel.updateByEarliestDeparture(it)},
                onDateChanged = {viewModel.updateSelectedDate(it); viewModel.updateDepartureNow(false)},
                onTimeChanged = {viewModel.updateSelectedTime(it); viewModel.updateDepartureNow(false)},
                onNowSelected = {
                    viewModel.updateDepartureNow(true)
                    viewModel.updateByEarliestDeparture(true)
                    viewModel.updateSelectedDate(LocalDate.now())

                    val now = LocalTime.now()
                    val minutes = now.minute
                    val roundedMinutes = (ceil(minutes / 5.0) * 5).toInt() % 60
                    val additionalHours = if (roundedMinutes == 0 && minutes != 0) 1 else 0
                    val roundedTime = now
                        .withSecond(0)
                        .withMinute(roundedMinutes)
                        .plusHours(additionalHours.toLong())

                    viewModel.updateSelectedTime(roundedTime)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            LabelWithToggleSwitch(
                label = stringResource(R.string.use_shared_bikes),
                checked = useSharedBikes
            ) {
                viewModel.saveUseSharedBikes(it)
            }

            Spacer(modifier = Modifier.height(8.dp))

            SlidersBox(
                labels = sliderLabels,
                values = listOf(transferBuffer, transferLength, comfortPreference, bikeTripBuffer),
                onValueChanges = listOf(
                    {
                        viewModel.saveTransferBuffer(it)
                    },
                    {
                        viewModel.saveTransferLength(it)
                    },
                    {
                        viewModel.saveTimeComfortBalance(it)
                    },
                    {
                        viewModel.saveBikeTripBuffer(it)
                    }
                ),
                maxValues = sliderMaxValues,
                labelLists = labelLists,
                useSharedBikes = useSharedBikes,
                bikeMax15MinSwitch = {
                    LabelWithToggleSwitch(
                        label = stringResource(R.string.bike_max_15_min),
                        checked = bikeMax15Min
                    ) {
                        viewModel.saveBikeMax15Minutes(it)
                    }
                }
            )


            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    Log.i("DEBUG", "Search button clicked")

                    scope.launch {
                        viewModel.startSearch(
                            context = context,
                            showDialog = { showDialog = it },
                            setErrorMessage = { errorMessage = it }
                        )
                    }
                },
                modifier = Modifier
                    .width(256.dp)
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if(startingSearch){
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
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
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    PragOTheme(darkTheme = true) {
        SearchScreen()
    }
}