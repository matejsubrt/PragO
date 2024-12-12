package com.example.prago.view.stopSearchScreen

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.prago.R
import com.example.prago.model.repositories.StopEntry
import com.example.prago.view.LocalNavController
import com.example.prago.view.common.GoBackTopBar
import com.example.prago.viewModel.AppViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.delay


@Composable
fun StopNameSuggestion(
    stopName: String,
    onClick: (String) -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick(stopName) }
            .height(56.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        ),


    ){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart,

        ){
            Text(text = stopName, modifier = Modifier.padding(start = 16.dp))
        }
    }

}

@Preview(showBackground = true)
@Composable
fun StopNameSuggestionPreview() {
    StopNameSuggestion(stopName = "Sample Stop Name") {}
}

@Composable
fun StopListEmptyState(
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.no_stops_found),
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = stringResource(R.string.try_adjusting_your_search),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

fun OnSearchQueryChange(it: String, viewModel: AppViewModel, to: Boolean){
    if(to){
        viewModel.updateToSearchQuery(it)
    } else {
        viewModel.updateFromSearchQuery(it)
        viewModel.updateStartByCoordinates(false)
    }
}




@Composable
fun StopSearchScreen(viewModel: AppViewModel, to: Boolean) {
    val searchSuggestions by if(to) viewModel.toStopSuggestions.collectAsStateWithLifecycle() else viewModel.fromStopSuggestions.collectAsStateWithLifecycle()
    val searchQuery by if(to) viewModel.toSearchQuery.collectAsStateWithLifecycle() else viewModel.fromSearchQuery.collectAsStateWithLifecycle()


    StopSearchScreen(
        searchQuery = searchQuery,
        searchResults = searchSuggestions,
        onSearchQueryChange = { OnSearchQueryChange(it, viewModel, to) },
        srcStop = !to,
        onCurrentLocationSelect = { viewModel.updateStartCoordinates(it); viewModel.updateStartByCoordinates(true) }
    )
    Log.i("DEBUG", "Search Screen end")
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalPermissionsApi::class
)
@Composable
fun StopSearchScreen(
    searchQuery: String,
    searchResults: List<StopEntry>,
    onSearchQueryChange: (String) -> Unit,
    onCurrentLocationSelect: (Location) -> Unit,
    srcStop: Boolean
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val navController = LocalNavController.current
    val context = LocalContext.current

    val locationPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(600)
        focusRequester.requestFocus()
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GoBackTopBar()
            SearchBar(
                modifier = Modifier
                    .fillMaxSize()
                    .focusRequester(focusRequester)
                    .background(Color.Red),
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                onSearch = { focusRequester.freeFocus(); keyboardController?.hide() },
                placeholder = {
                    val text = if (srcStop) stringResource(R.string.source_stop) else stringResource(R.string.destination_stop)
                    Text(text = text)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                contentDescription = stringResource(R.string.clear_search)
                            )
                        }
                    }
                },
                content = {
                    if (searchResults.isEmpty()) {
                        StopListEmptyState()
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (srcStop && searchQuery.isEmpty()) {
                                item {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(MaterialTheme.colorScheme.surface)
                                            .clickable {
                                                handleLocationRequest(
                                                    locationPermissionState = locationPermissionState,
                                                    context = context,
                                                    onLocationRetrieved = {
                                                        onCurrentLocationSelect(it)
                                                    },
                                                    onNavigation = { navController.navigate("searchPage") }
                                                )
                                            }
                                            .height(42.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                                        )
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ){
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Icon(
                                                    painter = painterResource(id = R.drawable.my_location),
                                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                                Text(
                                                    text = stringResource(id = R.string.current_location),
                                                    modifier = Modifier.padding(start = 16.dp),
                                                    style = TextStyle(
                                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                        fontSize = 16.sp
                                                    )
                                                )
                                            }

                                        }
                                    }
                                }
                            }

                            val itemsCount = if (searchResults.size > 16) 16 else searchResults.size
                            items(
                                count = itemsCount,
                                key = { index -> searchResults[index].id },
                                itemContent = { index ->
                                    StopNameSuggestion(stopName = searchResults[index].czechName) {
                                        keyboardController?.hide()
                                        onSearchQueryChange(it)
                                        navController.navigate("searchPage")
                                    }
                                }
                            )
                        }
                    }
                },
                active = true,
                onActiveChange = {},
                tonalElevation = 0.dp,
                colors = SearchBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
fun handleLocationRequest(
    locationPermissionState: MultiplePermissionsState,
    context: Context,
    onLocationRetrieved: (Location) -> Unit,
    onNavigation: () -> Unit
) {
    if (locationPermissionState.allPermissionsGranted) {
        retrieveCurrentLocation(context) { location ->
            onLocationRetrieved(location)
            onNavigation()
        }
    } else {
        locationPermissionState.launchMultiplePermissionRequest()
    }
}

@SuppressLint("MissingPermission")
fun retrieveCurrentLocation(context: Context, onLocationReceived: (Location) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            onLocationReceived(location)
        } else {
            Toast.makeText(context, "Unable to retrieve location.", Toast.LENGTH_LONG).show()
        }
    }.addOnFailureListener {
        Toast.makeText(context, "Failed to get location: ${it.message}", Toast.LENGTH_LONG).show()
    }
}


