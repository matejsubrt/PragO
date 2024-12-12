package com.example.prago.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.prago.StopList
import com.example.prago.view.resultScreen.ResultScreen
import com.example.prago.view.searchScreen.SearchScreen
import com.example.prago.model.apis.ConnectionSearchApi
import com.example.prago.model.repositories.SettingsRepository
import com.example.prago.model.repositories.StopListRepository
import com.example.prago.ui.theme.PragOTheme
import com.example.prago.view.settingsScreen.SettingsScreen
import com.example.prago.view.stopSearchScreen.StopSearchScreen
import com.example.prago.viewModel.AppViewModel
import com.example.prago.viewModel.preferencesDataStore
import com.example.prago.viewModel.stopListDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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


        appViewModel = AppViewModel(settingsRepository, stoplistRepository, connectionSearchApi)
        setContent {
            PragOTheme {
                PragOApp(appViewModel)
            }
        }
    }
}



@Composable
fun PragOApp(appViewModel: AppViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    val navigateToResults by appViewModel.navigateToResults.collectAsState()


    LaunchedEffect(navigateToResults) {
        if (navigateToResults) {
            navController.navigate("resultPage")
            appViewModel.updateNavigateToResults(false)
        }
    }


    LaunchedEffect(Unit){
        scope.launch(Dispatchers.IO) {
            appViewModel.tryUpdateStopNameList(context)
        }
    }


    CompositionLocalProvider(
        LocalNavController provides navController,
        LocalAppViewModel provides appViewModel,
    ) {
        NavHost(navController, startDestination = "searchPage") {
            composable("searchPage") { SearchScreen() }
            composable("resultPage") { ResultScreen() }
            composable("fromStopSelect") { StopSearchScreen(appViewModel, false) }
            composable("toStopSelect") { StopSearchScreen(appViewModel, true) }
            composable("settingsScreen") { SettingsScreen() }
        }
    }
}


