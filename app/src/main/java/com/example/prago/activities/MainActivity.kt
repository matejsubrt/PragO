package com.example.prago.activities

import com.example.prago.viewModels.SharedViewModel
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.prago.StopList
import com.example.prago.composables.resultScreen.ResultScreen
import com.example.prago.composables.SettingsScreen
import com.example.prago.composables.StopSearchScreen
import com.example.prago.composables.searchScreen.SearchScreen
import com.example.prago.ui.theme.PragOTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import com.example.prago.viewModels.preferencesDataStore
import com.example.prago.viewModels.stopListDataStore
import java.time.LocalDateTime

val sliderLabels = listOf("Transfer Buffer", "Transfer length", "Comfort Preference", "Bike Trip Buffer")
val sliderMaxValues = listOf(3f, 2f, 3f, 3f)
//val sliderDefaultValues = listOf(2f, 1f, 2f, 2f)




val transferBufferLabels = listOf("None", "Short", "Normal", "long")
val transferLengthLabels = listOf("Long\n(750m)", "Medium\n (400m)", "Short\n (250m)")
val comfortLabels = listOf("Shortest\nExtreme", "Shortest\nTime", "Balanced", "Least\nTransfers")
val bikeTripBufferLabels = listOf("None", "Short", "Medium", "Long")

val labelLists = listOf(transferBufferLabels, transferLengthLabels, comfortLabels, bikeTripBufferLabels)

val LocalNavController = compositionLocalOf<NavController> { error("No NavController provided") }
val LocalSharedViewModel = compositionLocalOf<SharedViewModel> { error("No SharedViewModel provided") }
val LocalStopListDataStore = compositionLocalOf<DataStore<StopList>> { error("No StopListDataStore provided") }

class MainActivity : ComponentActivity() {
    private lateinit var mainViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isConnectedToWifi = isWifiConnected()

        mainViewModel = SharedViewModel(applicationContext.stopListDataStore, applicationContext.preferencesDataStore)
        setContent {
            PragOTheme {
                PragOApp(mainViewModel, isConnectedToWifi) // Pass the viewModel to PragOApp
            }
        }


    }

    private fun isWifiConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false
    }
}



@Composable
fun PragOApp(mainViewModel: SharedViewModel, isConnectedToWifi: Boolean) {
    val context = LocalContext.current
    val navController = rememberNavController()

    if(isConnectedToWifi){
        Log.i("DEBUG", "Before LaunchedEffect")
        LaunchedEffect(Unit) {
            var generatedAtString = ""
            val exampleCounterFlow: Flow<String> = context.stopListDataStore.data
                .take(1) // TODO: check this location
                .map { value ->
                    // The exampleCounter property is generated from the proto schema.
                    value.generatedAt
                }
            exampleCounterFlow//.take(1)
                .collect { value ->
                generatedAtString = value
                println(value)
            }

            var generatedAtDateTime: LocalDateTime = LocalDateTime.MIN
            try{
                generatedAtDateTime = LocalDateTime.parse(generatedAtString)
            } catch(e: Exception){
                //Log.i("APP", "Error parsing time")
                //viewModel.downloadAndStoreJson("https://data.idos.cz/delays/json/stop-list.json")
            }

            if(generatedAtDateTime.plusDays(7).isBefore(LocalDateTime.now())){
                mainViewModel.downloadAndStoreJson("https://data.pid.cz/stops/json/stops.json")
                Log.i("DEBUG", "Data is outdated, downloading new data")
            }
            else{
                Log.i("DEBUG", "Data is up to date")
            }
        }
        Log.i("APP", "After LaunchedEffect")
    }


    CompositionLocalProvider(
        LocalNavController provides navController,
        LocalSharedViewModel provides mainViewModel,
        LocalStopListDataStore provides mainViewModel.stopListDataStore
    ) {
        NavHost(navController, startDestination = "searchPage") {
            composable("searchPage") { SearchScreen() }
            composable("resultPage") { ResultScreen() }
            composable("fromStopSelect") { StopSearchScreen(mainViewModel, navController, false) }
            composable("toStopSelect") { StopSearchScreen(mainViewModel, navController, true) }
            composable("settingsScreen") { SettingsScreen() }
        }
    }
}


