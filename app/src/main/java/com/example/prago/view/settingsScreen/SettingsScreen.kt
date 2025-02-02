package com.example.prago.view.settingsScreen

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
//import com.example.prago.activities.LocalSharedViewModel
import com.example.prago.ui.theme.PragOTheme
import com.example.prago.view.common.AutoResizedText
import com.example.prago.view.LocalAppViewModel
import com.example.prago.view.LocalNavController
import com.example.prago.view.common.GoBackTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableIntSettingRow(
    currentValue: Int,
    label: String,
    defaultValue: Int,
    units: String,
    onValueChange: (Int) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current


    val textState = remember { mutableStateOf(currentValue.toString()) }


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
                        val intValue = textState.value.toIntOrNull() ?: defaultValue
                        onValueChange(intValue)
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Number
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        val intValue = textState.value.toIntOrNull() ?: defaultValue
                        onValueChange(intValue)
                    }),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary
                    )


//                        .outlinedTextFieldColors(
//                        focusedBorderColor = MaterialTheme.colorScheme.primary,
//                        //unfocusedBorderColor = MaterialTheme.colorScheme.onPrimaryContainer,
//                        containerColor = MaterialTheme.colorScheme.secondaryContainer
//                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = units,
                    modifier = Modifier.weight(1f),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
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
            GoBackTopBar()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.settings),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            EditableIntSettingRow(
                currentValue = walkingPace,
                label = stringResource(R.string.walking_pace),
                defaultValue = walkingPace,
                units = "min/km",
                onValueChange = { viewModel.saveWalkingPace(it) }
            )

            EditableIntSettingRow(
                currentValue = cyclingPace,
                label = stringResource(R.string.cycling_pace),
                defaultValue = cyclingPace,
                units = "min/km",
                onValueChange = { viewModel.saveCyclingPace(it) }
            )

            EditableIntSettingRow(
                currentValue = bikeUnlockTime,
                label = stringResource(R.string.bike_unlock_time),
                defaultValue = bikeUnlockTime,
                units = "s",
                onValueChange = { viewModel.saveBikeUnlockTime(it) }
            )

            EditableIntSettingRow(
                currentValue = bikeLockTime,
                label = stringResource(R.string.bike_lock_time),
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
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                AutoResizedText(
                    text = stringResource(R.string.save_and_return),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimary
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