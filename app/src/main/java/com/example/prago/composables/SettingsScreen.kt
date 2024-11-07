package com.example.prago.composables

import com.example.prago.viewModels.SharedViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prago.R
import com.example.prago.activities.LocalNavController
import com.example.prago.activities.LocalSharedViewModel
import com.example.prago.ui.theme.PragOTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditableIntSettingRow(
    viewModel: SharedViewModel,
    label: String,
    preferencesKey: String,
    defaultValue: Int,
    units: String,
    onValueChange: (Int) -> Unit
) {
    var currentValue by remember { mutableStateOf(defaultValue) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val textState = remember { mutableStateOf(currentValue.toString()) }

    LaunchedEffect(preferencesKey) {
        currentValue = viewModel.retrieveIntSetting(preferencesKey, defaultValue)
        textState.value = currentValue.toString()
    }

    Card(modifier = Modifier.padding(8.dp)) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                modifier = Modifier.weight(1.2f),
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = textState.value,
                    onValueChange = { newValue ->
                        textState.value = newValue.filter { it.isDigit() }
                        currentValue = textState.value.toIntOrNull() ?: defaultValue
                        onValueChange(currentValue) // Notify parent of the new value
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Number
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        currentValue = textState.value.toIntOrNull() ?: defaultValue
                        scope.launch {
                            viewModel.saveIntSetting(preferencesKey, currentValue)
                        }
                    }),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .background(MaterialTheme.colorScheme.background)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = units,
                    modifier = Modifier.weight(1f),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}







@Composable
fun SettingsScreen() {
    val viewModel = LocalSharedViewModel.current
    val navController = LocalNavController.current

    // State holders for each setting value, initialized to default values temporarily
    var walkingPace by remember { mutableStateOf(12) }
    var cyclingPace by remember { mutableStateOf(5) }
    var bikeUnlockTime by remember { mutableStateOf(30) }
    var bikeLockTime by remember { mutableStateOf(15) }

    val scope = rememberCoroutineScope()

    // Load values from the DataStore when the composable is first composed
    LaunchedEffect(Unit) {
        walkingPace = viewModel.retrieveIntSetting("walkingPace", 12)
        cyclingPace = viewModel.retrieveIntSetting("cyclingPace", 5)
        bikeUnlockTime = viewModel.retrieveIntSetting("bikeUnlockTime", 30)
        bikeLockTime = viewModel.retrieveIntSetting("bikeLockTime", 15)
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ResultTopBar()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Settings",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            EditableIntSettingRow(
                viewModel = viewModel,
                label = stringResource(R.string.walking_pace),
                preferencesKey = "walkingPace",
                defaultValue = walkingPace,
                units = "min/km",
                onValueChange = { walkingPace = it }
            )

            EditableIntSettingRow(
                viewModel = viewModel,
                label = stringResource(R.string.cycling_pace),
                preferencesKey = "cyclingPace",
                defaultValue = cyclingPace,
                units = "min/km",
                onValueChange = { cyclingPace = it }
            )

            EditableIntSettingRow(
                viewModel = viewModel,
                label = stringResource(R.string.bike_unlock_time),
                preferencesKey = "bikeUnlockTime",
                defaultValue = bikeUnlockTime,
                units = "s",
                onValueChange = { bikeUnlockTime = it }
            )

            EditableIntSettingRow(
                viewModel = viewModel,
                label = stringResource(R.string.bike_lock_time),
                preferencesKey = "bikeLockTime",
                defaultValue = bikeLockTime,
                units = "s",
                onValueChange = { bikeLockTime = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    viewModel.walkingPace.value = walkingPace
                    viewModel.cyclingPace.value = cyclingPace
                    viewModel.bikeUnlockTime.value = bikeUnlockTime
                    viewModel.bikeLockTime.value = bikeLockTime
                    scope.launch {
                        viewModel.saveIntSetting("walkingPace", walkingPace)
                        viewModel.saveIntSetting("cyclingPace", cyclingPace)
                        viewModel.saveIntSetting("bikeUnlockTime", bikeUnlockTime)
                        viewModel.saveIntSetting("bikeLockTime", bikeLockTime)
                    }
                    navController.navigate("searchPage")
                },
                modifier = Modifier
                    .width(256.dp)
                    .height(72.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                AutoResizedText(
                    text = stringResource(R.string.save_and_return),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    PragOTheme(darkTheme = true){
        SettingsScreen()
    }
}