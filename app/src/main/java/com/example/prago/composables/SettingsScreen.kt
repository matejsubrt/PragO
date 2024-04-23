package com.example.prago.composables

import com.example.prago.viewModels.SharedViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
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
    defaultValue: Int
) {
    var currentValue by remember { mutableStateOf(defaultValue) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val textState = remember { mutableStateOf("") }

    // Side effect to retrieve value when the Composable is first composed
    LaunchedEffect(true) {
        currentValue = viewModel.retrieveIntSetting(preferencesKey, defaultValue)
        textState.value = currentValue.toString()
    }

    Row(modifier = Modifier.padding(8.dp)) {
        Text(text = label)
        OutlinedTextField(
            value = textState.value,
            onValueChange = { newValue ->
                textState.value = newValue.filter { it.isDigit() }
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
                .focusRequester(FocusRequester())
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused) {
                        // If the text field loses focus, update the current value
                        currentValue = textState.value.toIntOrNull() ?: defaultValue
                    }
                }
        )
    }
}




@Composable
fun SettingsScreen() {
    val viewModel = LocalSharedViewModel.current
    val navController = LocalNavController.current

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)

        ){
            ResultTopBar(rememberNavController())
            EditableIntSettingRow(viewModel = viewModel, label = "Walking pace", preferencesKey = "walkingPace", defaultValue = 12)
            EditableIntSettingRow(viewModel = viewModel, label = "Cycling pace", preferencesKey = "cyclingPace", defaultValue = 5)
            EditableIntSettingRow(viewModel = viewModel, label = "Bike unlock time", preferencesKey = "bikeUnlockTime", defaultValue = 30)
            EditableIntSettingRow(viewModel = viewModel, label = "Bike lock time", preferencesKey = "bikeLockTime", defaultValue = 15)
        }
    }
}

@Composable
fun Baf(){
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Text("BAF")
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    PragOTheme(darkTheme = true){
        SettingsScreen()
        //Baf()
    }
}