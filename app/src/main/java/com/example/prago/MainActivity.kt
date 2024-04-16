package com.example.prago

import SharedViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.prago.ui.theme.PragOTheme

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

class MainActivity : ComponentActivity() {
    //private val viewModel: MyViewModel by viewModels()
    private val viewModel: SharedViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PragOTheme {
                PragOApp(viewModel) // Pass the viewModel to PragOApp
            }
        }
    }
}



@Composable
fun PragOApp(viewModel: SharedViewModel) {
    val navController = rememberNavController()
    CompositionLocalProvider(LocalNavController provides navController, LocalSharedViewModel provides viewModel) {
        NavHost(navController, startDestination = "searchPage") {
            composable("searchPage") { SearchPage() }
            composable("resultPage") { ResultPage(null) }
        }
    }
}

