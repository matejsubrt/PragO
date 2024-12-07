package com.example.prago.composables

//import com.example.prago.viewModel.SharedViewModel
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
import androidx.compose.runtime.collectAsState
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
import com.example.prago.activities.LocalAppViewModel
import com.example.prago.activities.LocalNavController
//import com.example.prago.activities.LocalSharedViewModel
import com.example.prago.ui.theme.PragOTheme
import com.example.prago.viewModel.AppViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditableIntSettingRow(
    viewModel: AppViewModel,
    label: String,
    preferencesKey: String,
    defaultValue: Int,
    units: String,
    onValueChange: (Int) -> Unit
) {
    //var currentValue by remember { mutableStateOf(defaultValue) }
    val currentValue by viewModel.getIntSettingValue(preferencesKey, defaultValue).collectAsState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()


    val textState = remember { mutableStateOf(currentValue.toString()) }



//    LaunchedEffect(preferencesKey) {
//        currentValue = viewModel.retrieveIntSetting(preferencesKey, defaultValue)
//        textState.value = currentValue.toString()
//    }

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
                        var intValue = textState.value.toIntOrNull() ?: defaultValue
                        onValueChange(intValue) // Notify parent of the new value
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Number
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        //currentValue = textState.value.toIntOrNull() ?: defaultValue
//                        scope.launch {
//                            viewModel.saveIntSetting(preferencesKey, currentValue)
//                        }
                        var intValue = textState.value.toIntOrNull() ?: defaultValue
                        onValueChange(intValue) // Notify parent of the new value
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
    val viewModel = LocalAppViewModel.current
    val navController = LocalNavController.current

    val walkingPace by viewModel.walkingPace.collectAsState()
    val cyclingPace by viewModel.cyclingPace.collectAsState()
    val bikeUnlockTime by viewModel.bikeUnlockTime.collectAsState()
    val bikeLockTime by viewModel.bikeLockTime.collectAsState()


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
                onValueChange = { viewModel.saveWalkingPace(it) }
            )

            EditableIntSettingRow(
                viewModel = viewModel,
                label = stringResource(R.string.cycling_pace),
                preferencesKey = "cyclingPace",
                defaultValue = cyclingPace,
                units = "min/km",
                onValueChange = { viewModel.saveCyclingPace(it) }
            )

            EditableIntSettingRow(
                viewModel = viewModel,
                label = stringResource(R.string.bike_unlock_time),
                preferencesKey = "bikeUnlockTime",
                defaultValue = bikeUnlockTime,
                units = "s",
                onValueChange = { viewModel.saveBikeUnlockTime(it) }
            )

            EditableIntSettingRow(
                viewModel = viewModel,
                label = stringResource(R.string.bike_lock_time),
                preferencesKey = "bikeLockTime",
                defaultValue = bikeLockTime,
                units = "s",
                onValueChange = { viewModel.saveBikeLockTime(it) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
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