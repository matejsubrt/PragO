package com.example.prago.composables

import com.example.prago.viewModels.SharedViewModel
import com.example.prago.viewModels.StopEntry
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableSharedFlow


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
            text = "No stops found",
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = "Try adjusting your search",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

fun OnSearchQueryChange(it: String, viewModel: SharedViewModel, to: Boolean){
    if(to){
        viewModel.onToSearchQueryChange(it)
        viewModel.toText.value = it
    } else {
        viewModel.onFromSearchQueryChange(it)
        viewModel.fromText.value = it
    }
}

val onSearchQueryChange: MutableSharedFlow<String> = MutableSharedFlow()



@Composable
fun StopSearchScreen(viewModel: SharedViewModel, navController: NavController, to: Boolean) {
    Log.i("DEBUG", "Search Screen start")
    val searchResults by if(to) viewModel.toSearchResults.collectAsStateWithLifecycle() else viewModel.fromSearchResults.collectAsStateWithLifecycle()
    val searchQuery = if(to) viewModel.toSearchQuery else viewModel.fromSearchQuery
    //val searchResults = listOf(StopEntry("Chodov", "Chodov", "1"), StopEntry("Biskupcova", "Biskupcova", "2"), StopEntry("ABC", "ABC", "3"),StopEntry("ABC", "ABC", "4"))
    Log.i("DEBUG", "Recomposition triggered - Search Query: $searchQuery, Results size: ${searchResults.size}")


    StopSearchScreen(
        searchQuery = searchQuery,
        searchResults = searchResults,
        onSearchQueryChange = { OnSearchQueryChange(it, viewModel, to) },
        navController = navController,
        srcStop = !to
    )
    Log.i("DEBUG", "Search Screen end")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun StopSearchScreen(
    searchQuery: String,
    searchResults: List<StopEntry>,
    onSearchQueryChange: (String) -> Unit,
    navController: NavController,
    srcStop: Boolean
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ResultTopBar(navController)
            SearchBar(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red),
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                onSearch = { keyboardController?.hide() },
                placeholder = {
                    val text = if (srcStop) "Source stop" else "Destination stop"
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
                                contentDescription = "Clear search"
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
                            val itemsCount = if (searchResults.size > 16) 16 else searchResults.size
                            items(
                                count = itemsCount,
                                key = { index -> searchResults[index].id },
                                itemContent = { index ->
                                    StopNameSuggestion(stopName = searchResults[index].czechName) {
                                        keyboardController?.hide()
                                        Log.i("DEBUG", "Stop selected")
                                        onSearchQueryChange(it) // Update search query state
                                        //navController.popBackStack()
                                        navController.navigate("searchPage")
                                        Log.i("DEBUG", "Navigated back to search screen")
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
                    containerColor = MaterialTheme.colorScheme.surface,
                )
            )
        }
    }

}

