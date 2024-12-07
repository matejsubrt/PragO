package com.example.prago.activities

//import com.example.prago.viewModel.SharedViewModel
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
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
import com.example.prago.model.ConnectionSearchApi
import com.example.prago.model.SettingsRepository
import com.example.prago.model.StopListRepository
import com.example.prago.ui.theme.PragOTheme
import com.example.prago.viewModel.AppViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import com.example.prago.viewModel.preferencesDataStore
import com.example.prago.viewModel.stopListDataStore
import java.time.LocalDateTime








val LocalNavController = compositionLocalOf<NavController> { error("No NavController provided") }
val LocalAppViewModel = compositionLocalOf<AppViewModel> { error("No AppViewModel provided") }
val LocalStopListDataStore = compositionLocalOf<DataStore<StopList>> { error("No StopListDataStore provided") }




class MainActivity : ComponentActivity() {
    private lateinit var appViewModel: AppViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsRepository = SettingsRepository(applicationContext.preferencesDataStore)
        val stoplistRepository = StopListRepository(applicationContext.stopListDataStore)
        val connectionSearchApi = ConnectionSearchApi()

        val isConnectedToWifi = isWifiConnected()

        appViewModel = AppViewModel(settingsRepository, stoplistRepository, connectionSearchApi)
        setContent {
            PragOTheme {
                PragOApp(appViewModel, isConnectedToWifi)
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
fun PragOApp(appViewModel: AppViewModel, isConnectedToWifi: Boolean) {
    val context = LocalContext.current
    val navController = rememberNavController()

    val navigateToResults by appViewModel.navigateToResults.collectAsState()


    LaunchedEffect(navigateToResults) {
        if (navigateToResults) {
            navController.navigate("resultPage")
            appViewModel.resetNavigateToResults()
        }
    }


    if(isConnectedToWifi){
        Log.i("DEBUG", "Before LaunchedEffect")
        LaunchedEffect(Unit) {

            val lastUpdateTime = appViewModel.stopListLastUpdateTime.value

            if(lastUpdateTime.plusDays(7).isBefore(LocalDateTime.now())){
                //TODO: Change the URL implementation
                appViewModel.downloadAndStoreStopList("https://data.pid.cz/stops/json/stops.json")
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
        LocalAppViewModel provides appViewModel,
    ) {
        NavHost(navController, startDestination = "searchPage") {
            composable("searchPage") { SearchScreen() }
            composable("resultPage") { ResultScreen() }
            composable("fromStopSelect") { StopSearchScreen(appViewModel, navController, false) }
            composable("toStopSelect") { StopSearchScreen(appViewModel, navController, true) }
            composable("settingsScreen") { SettingsScreen() }
        }
    }
}


