package com.example.prago.view.searchScreen.dateTimeSelecting

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prago.R
import com.example.prago.viewModel.AppViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


fun getLabelFromDate(context: Context, date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("E d.M.") // Format: Mo/Tue/... DD.MM.
    return when (date) {
        LocalDate.now() -> context.getString(R.string.today)
        LocalDate.now().plusDays(1) -> context.getString(R.string.tomorrow) // Add to strings.xml
        else -> date.format(formatter)
    }
}


fun getLabelFromTime(time: LocalTime): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return time.format(formatter)
}

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
        Text(text = if(departureButton) stringResource(R.string.departure) else stringResource(R.string.arrival))
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

        ArrivalDepartureButton(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            departureButton = true,
            departureSelected = isDeparture,
            onValueChanged = onValueChanged
        )

        Spacer(modifier = Modifier.width(8.dp))


        ArrivalDepartureButton(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            departureButton = false,
            departureSelected = isDeparture,
            onValueChanged = onValueChanged
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimeBottomSheet(
    viewModel: AppViewModel,
    byEarliestDeparture: Boolean,
    onArrDepChange: (Boolean) -> Unit,
    onTimeChanged: (LocalTime) -> Unit,
    onDateChanged: (LocalDate) -> Unit,
    onNowSelected: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()

    val departureNow by viewModel.departureNow.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedTime by viewModel.selectedTime.collectAsState()



    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(8.dp)
            .height(36.dp)
            .fillMaxSize()
            .clickable(
                onClick = {
                    scope.launch {
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
                R.drawable.departure
            } else {
                R.drawable.arrival
            }

            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier
                    .height(24.dp)
                    .padding(start = 4.dp, end = 4.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.width(8.dp))

            val text = if(departureNow){
                stringResource(R.string.now)
            }else{
                getLabelFromDate(LocalContext.current, selectedDate) + " " + getLabelFromTime(selectedTime)
            }
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.now),
                style = TextStyle(
                    fontSize = 17.sp,
                    color = Color.Gray
                ),
                modifier = Modifier.clickable(
                    onClick = {
                        onNowSelected()
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

                Spacer(modifier = Modifier.height(40.dp))

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

                Spacer(modifier = Modifier.height(40.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .size(80.dp, 40.dp)
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
                            text = stringResource(R.string.done),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}