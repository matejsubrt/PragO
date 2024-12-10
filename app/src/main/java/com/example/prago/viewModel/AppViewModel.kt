    package com.example.prago.viewModel

    import android.content.Context
    import android.location.Location
    import android.util.Log
    import androidx.datastore.core.CorruptionException
    import androidx.datastore.core.DataStore
    import androidx.datastore.core.Serializer
    import androidx.datastore.dataStore
    import androidx.datastore.preferences.preferencesDataStore
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.example.prago.StopList
    import com.example.prago.model.ConnectionSearchApi
    import com.example.prago.model.SettingsRepository
    import com.example.prago.model.StopEntry
    import com.example.prago.model.StopListRepository
    import com.example.prago.model.bikeLockTimeDefault
    import com.example.prago.model.bikeTripBufferDefault
    import com.example.prago.model.bikeUnlockTimeDefault
    import com.example.prago.model.comfortPreferenceDefault
    import com.example.prago.model.cyclingPaceDefault
    import com.example.prago.model.dataClasses.AlternativeTripsRequest
    import com.example.prago.model.dataClasses.AlternativeTripsResultState
    import com.example.prago.model.dataClasses.ConnectionSearchResult
    import com.example.prago.model.dataClasses.ConnectionSearchResultState
    import com.example.prago.model.dataClasses.CreateStopToStopRangeRequest
    import com.example.prago.model.dataClasses.SearchSettings
    import com.example.prago.model.dataClasses.TripAlternatives
    import com.example.prago.model.dataClasses.UsedTrip
    import com.example.prago.model.transferBufferDefault
    import com.example.prago.model.transferLengthDefault
    import com.example.prago.model.useSharedBikesDefault
    import com.example.prago.model.walkingPaceDefault
    import com.google.protobuf.InvalidProtocolBufferException
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.FlowPreview
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.SharingStarted
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.flow.combine
    import kotlinx.coroutines.flow.debounce
    import kotlinx.coroutines.flow.stateIn
    import kotlinx.coroutines.launch
    import kotlinx.coroutines.withContext
    import java.io.InputStream
    import java.io.OutputStream
    import java.time.LocalDate
    import java.time.LocalDateTime
    import java.time.LocalTime
    import java.time.format.DateTimeFormatter



    object StopListSerializer : Serializer<StopList> {
        override val defaultValue: StopList = StopList.getDefaultInstance()
        override suspend fun readFrom(input: InputStream): StopList = withContext(Dispatchers.IO) {
            try {
                return@withContext StopList.parseFrom(input)
            } catch (exception: InvalidProtocolBufferException) {
                throw CorruptionException("Cannot read proto.", exception)
            }
        }

        override suspend fun writeTo(t: StopList, output: OutputStream) = withContext(Dispatchers.IO) {t.writeTo(output)}
    }

    val Context.stopListDataStore: DataStore<StopList> by dataStore(
        fileName = "settings.pb",
        serializer = StopListSerializer
    )

    val Context.preferencesDataStore by preferencesDataStore(name = "preferences")


    class AppViewModel(
        val settingsRepository: SettingsRepository,
        val stopListRepository: StopListRepository,
        val connectionSearchApi: ConnectionSearchApi
    ) : ViewModel() {

// =================================================================================================
        // Initiates the search
        suspend fun startSearch(
            showDialog: (Boolean) -> Unit,
            setErrorMessage: (String) -> Unit, // TODO: combine these
            context: Context
        ){
            var startDateTime: LocalDateTime
            if(departureNow.value){
                startDateTime = LocalDateTime.now()
                updateSelectedDate(LocalDate.now())
                updateSelectedTime(LocalTime.now())
            } else {
                startDateTime = LocalDateTime.of(selectedDate.value, selectedTime.value)
            }


            val searchRequest = CreateStopToStopRangeRequest(
                srcStopName = fromSearchQuery.value,
                destStopName = toSearchQuery.value,
                dateTime = startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")),
                byEarliestDeparture = byEarliestDeparture.value,
                settings = getSearchSettings(),
                rangeLength = 15
            )

            viewModelScope.launch(Dispatchers.IO) {
                connectionSearchApi.searchForConnection(searchRequest, context).collect{ result ->
                    when(result){
                        is ConnectionSearchResultState.Success -> {
                            val results = result.results

                            updateSearchResultList(results, false)
                            updateSearchRangeStart(startDateTime)
                            updateSearchRangeEnd(startDateTime.plusMinutes(15))

                            updateNavigateToResults(true)
                        }
                        is ConnectionSearchResultState.Failure -> {
                            Log.e("AppViewModel", "Error fetching search results: ${result.errorMessage}")
                            setErrorMessage(result.errorMessage)
                            showDialog(true)
                        }
                    }
                }
            }
        }



        // Expands the search (either to the past or to the future)
        suspend fun expandSearch(toPast: Boolean, context: Context){
            val rangeStart: LocalDateTime

            if(toPast){
                updateExpandingSearchToPast(true)
                rangeStart = searchRangeStart.value.minusMinutes(15)
            }else{
                updateExpandingSearchToFuture(true)
                rangeStart = searchRangeEnd.value
            }


            val searchRequest = CreateStopToStopRangeRequest(
                srcStopName = fromSearchQuery.value,
                destStopName = toSearchQuery.value,
                dateTime = rangeStart.toString(),
                byEarliestDeparture = byEarliestDeparture.value,
                settings = getSearchSettings(),
                rangeLength = 15
            )

            viewModelScope.launch(Dispatchers.IO) {
                connectionSearchApi.searchForConnection(searchRequest, context).collect{ result ->
                    when(result){
                        is ConnectionSearchResultState.Success -> {
                            val results = result.results

                            val currentResults = searchResultList.value
                            var combinedResults = combineResultLists(currentResults, results, toPast)

                            updateSearchResultList(combinedResults, false)

                            if(toPast){
                                updateSearchRangeStart(searchRangeStart.value.minusMinutes(15))
                            } else {
                                updateSearchRangeEnd(searchRangeEnd.value.plusMinutes(15))
                            }
                        }
                        is ConnectionSearchResultState.Failure -> {
                            Log.e("AppViewModel", "Error expanding search: ${result.errorMessage}")
                        }
                    }
                }
                updateExpandingSearchToPast(false)
                updateExpandingSearchToFuture(false)
            }
        }



        // For the given trip, finds past ofr future alternative trips
        suspend fun fetchAlternatives(
            searchResult: ConnectionSearchResult,
            tripIndex: Int,
            earlier: Boolean,
            context: Context
        ){
            val usedTripAlternativesObject = searchResult.usedTripAlternatives[tripIndex]
            val existingAlternatives = usedTripAlternativesObject.alternatives
            val usedTrip = if (earlier) existingAlternatives.first() else existingAlternatives.last()

            val srcStopId = usedTrip.stopPasses[usedTrip.getOnStopIndex].id
            val destStopId = usedTrip.stopPasses[usedTrip.getOffStopIndex].id

            val dateTimeWithDelay = usedTrip.stopPasses[usedTrip.getOnStopIndex].departureTime.plusSeconds(usedTrip.delayWhenBoarded.value.toLong())
            val dateTime = if (earlier) dateTimeWithDelay else dateTimeWithDelay.plusSeconds(1)
            val dateTimeString = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))

            val count = 5

            val tripId = usedTrip.tripId

            val request = AlternativeTripsRequest(
                srcStopId = srcStopId,
                destStopId = destStopId,
                dateTime = dateTimeString,
                previous = earlier,
                count = count,
                tripId = tripId
            )

            viewModelScope.launch(Dispatchers.IO) {
                try{
                    connectionSearchApi.getAlternativeTrips(request, context).collect { result ->
                        when(result){
                            is AlternativeTripsResultState.Success -> {
                                val newAlternatives = result.results
                                val allAlternatives: List<UsedTrip> = combineAlternativesLists(existingAlternatives, newAlternatives, earlier)//if (earlier) usedTrips + alternatives else alternatives + usedTrips
                                val newIndex = if (earlier) newAlternatives.size - 1 else existingAlternatives.size

                                searchResult.usedTripAlternatives[tripIndex] = TripAlternatives(
                                    currIndex = newIndex,
                                    alternatives = allAlternatives,
                                    count = allAlternatives.size
                                )
                            }
                            is AlternativeTripsResultState.Failure -> {
                                Log.e("AppViewModel", "Error fetching alternatives: ${result.errorMessage}")
                            }
                        }
                    }
                } catch(e: Exception){
                    Log.e("AppViewModel", "Error fetching alternatives: ${e.message}")
                }

            }
        }



        // Updates the delay data for all trips in the search results
        fun updateDelays(
            context: Context
        ){
            val searchResults = searchResultList.value

            viewModelScope.launch(Dispatchers.IO) {
                //try{
                connectionSearchApi.updateDelayData(searchResults, context).collect{ result ->
                    when(result){
                        is ConnectionSearchResultState.Success -> {
                            updateSearchResultList(result.results, true)
                        }
                        is ConnectionSearchResultState.Failure -> {
                            Log.e("AppViewModel", "Error updating delay data: ${result.errorMessage}")
                        }
                    }
                }
            }
        }

// =================================================================================================
// SETTINGS VALUES MANAGEMENT
        // The transfer time buffer to be used for searches
        val transferBuffer: StateFlow<Float> = settingsRepository.transferBuffer.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = transferBufferDefault
        )
        fun saveTransferBuffer(value: Float) {
            viewModelScope.launch {
                settingsRepository.saveTransferBuffer(value)
            }
        }


        // The maximum transfer length preference to be used for searches
        val transferLength: StateFlow<Float> = settingsRepository.transferLength.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = transferLengthDefault
        )
        fun saveTransferLength(value: Float) {
            viewModelScope.launch {
                settingsRepository.saveTransferLength(value)
            }
        }


        // The time/comfort balance value to be used for searches
        val timeComfortBalance: StateFlow<Float> = settingsRepository.timeComfortBalance.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = comfortPreferenceDefault
        )
        fun saveTimeComfortBalance(value: Float) {
            viewModelScope.launch {
                settingsRepository.saveComfortPreference(value)
            }
        }


        // The bike trip time buffer to be used for searches
        val bikeTripBuffer: StateFlow<Float> = settingsRepository.bikeTripBuffer.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = bikeTripBufferDefault
        )
        fun saveBikeTripBuffer(value: Float) {
            viewModelScope.launch {
                settingsRepository.saveBikeTripBuffer(value)
            }
        }


        // Whether shared bikes should be used as part of the search
        val useSharedBikes: StateFlow<Boolean> = settingsRepository.useSharedBikes.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = useSharedBikesDefault
        )
        fun saveUseSharedBikes(value: Boolean) {
            viewModelScope.launch {
                settingsRepository.saveUseSharedBikes(value)
            }
        }


        // Whether only bike rides under 15 minutes should be considered (only relevant if useSharedBikes is true)
        val bikeMax15Minutes: StateFlow<Boolean> = settingsRepository.bikeMax15Minutes.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
        fun saveBikeMax15Minutes(value: Boolean) {
            viewModelScope.launch {
                settingsRepository.saveBikeMax15Minutes(value)
            }
        }


        // The walking pace to be used for searches (in min/km)
        val walkingPace: StateFlow<Int> = settingsRepository.walkingPace.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = walkingPaceDefault
        )
        fun saveWalkingPace(value: Int) {
            viewModelScope.launch {
                settingsRepository.saveWalkingPace(value)
            }
        }


        // The cycling pace to be used for searches (in min/km)
        val cyclingPace: StateFlow<Int> = settingsRepository.cyclingPace.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = cyclingPaceDefault
        )
        fun saveCyclingPace(value: Int) {
            viewModelScope.launch {
                settingsRepository.saveCyclingPace(value)
            }
        }


        // The time it takes to unlock a bike (in seconds)
        val bikeUnlockTime: StateFlow<Int> = settingsRepository.bikeUnlockTime.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = bikeUnlockTimeDefault
        )
        fun saveBikeUnlockTime(value: Int) {
            viewModelScope.launch {
                settingsRepository.saveBikeUnlockTime(value)
            }
        }


        // The time it takes to lock a bike (in seconds)
        val bikeLockTime: StateFlow<Int> = settingsRepository.bikeLockTime.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = bikeLockTimeDefault
        )
        fun saveBikeLockTime(value: Int) {
            viewModelScope.launch {
                settingsRepository.saveBikeLockTime(value)
            }
        }



// =================================================================================================
// SEARCH QUERY STATE
        // The name of the source stop
        private val _fromSearchQuery = MutableStateFlow("")
        val fromSearchQuery: StateFlow<String> = _fromSearchQuery
        fun updateFromSearchQuery(query: String) { _fromSearchQuery.value = query }

        // The name of the destination stop
        private val _toSearchQuery = MutableStateFlow("")
        val toSearchQuery: StateFlow<String> = _toSearchQuery
        fun updateToSearchQuery(query: String) { _toSearchQuery.value = query }

        // Whether the start of the search should be specified by coordinates
        private val _startByCoordinates = MutableStateFlow(false)
        val startByCoordinates: StateFlow<Boolean> = _startByCoordinates
        fun updateStartByCoordinates(byCoordinates: Boolean) { _startByCoordinates.value = byCoordinates }

        // The coordinates of the start location (valid only if startByCoordinates is true)
        private val _startCoordinates = MutableStateFlow(Location("default"))
        val startCoordinates: StateFlow<Location> = _startCoordinates
        fun updateStartCoordinates(coordinates: Location) { _startCoordinates.value = coordinates }


        // The date selected by the user
        private val _selectedDate = MutableStateFlow(LocalDate.now())
        val selectedDate: StateFlow<LocalDate> = _selectedDate
        fun updateSelectedDate(date: LocalDate) { _selectedDate.value = date }

        // The time selected by the user
        private val _selectedTime = MutableStateFlow(LocalTime.now())
        val selectedTime: StateFlow<LocalTime> = _selectedTime
        fun updateSelectedTime(time: LocalTime) { _selectedTime.value = time }

        // Whether the selected date and time (false) or the current date and time (true) should be used for the search
        private val _departureNow = MutableStateFlow(true)
        val departureNow: StateFlow<Boolean> = _departureNow
        fun updateDepartureNow(isNow: Boolean) { _departureNow.value = isNow }

        // Whether the date and time specified means the earliest possible departure (true) or the latest possible arrival (false)
        // This implies the direction in which the search needs to be performed
        private val _byEarliestDeparture = MutableStateFlow(true)
        val byEarliestDeparture: StateFlow<Boolean> = _byEarliestDeparture
        fun updateByEarliestDeparture(isEarliest: Boolean) { _byEarliestDeparture.value = isEarliest }



// =================================================================================================
// SEARCH RESULTS STATE
        // The start of the search range for which we currently have results
        private val _searchRangeStart = MutableStateFlow(LocalDateTime.now())
        val searchRangeStart: StateFlow<LocalDateTime> = _searchRangeStart
        fun updateSearchRangeStart(dateTime: LocalDateTime) { _searchRangeStart.value = dateTime }

        // The end of the search range for which we currently have results
        private val _searchRangeEnd = MutableStateFlow(LocalDateTime.now())
        val searchRangeEnd: StateFlow<LocalDateTime> = _searchRangeEnd
        fun updateSearchRangeEnd(dateTime: LocalDateTime) { _searchRangeEnd.value = dateTime }

        // The list of search results to display
        private val _searchResultList = MutableStateFlow<List<ConnectionSearchResult>>(emptyList())
        val searchResultList: StateFlow<List<ConnectionSearchResult>> = _searchResultList
        fun updateSearchResultList(newResults: List<ConnectionSearchResult>, updatingDelays: Boolean) {
            if(updatingDelays){
                if(_searchResultList.value.size != newResults.size){
                    Log.i("ERROR", "Different number of results")
                } else {
                    for(i in 0.. _searchResultList.value.size - 1){
                        if(_searchResultList.value[i].usedTripAlternatives.size != newResults[i].usedTripAlternatives.size){
                            Log.i("ERROR", "Different number of trips")
                        } else {
                            for(j in 0.. _searchResultList.value[i].usedTripAlternatives.size - 1){
                                if(_searchResultList.value[i].usedTripAlternatives[j].alternatives.size != newResults[i].usedTripAlternatives[j].alternatives.size){
                                    Log.i("ERROR", "Different number of alternatives")
                                } else {
                                    for(k in 0.. _searchResultList.value[i].usedTripAlternatives[j].alternatives.size - 1){
                                        Log.i("DEBUG", "i: $i, j: $j, k: $k")
                                        Log.i("DEBUG", "Delay before: ${_searchResultList.value[i].usedTripAlternatives[j].alternatives[k].delayWhenBoarded}")
                                        _searchResultList.value[i].usedTripAlternatives[j].alternatives[k].currentDelay.value = 0
                                        _searchResultList.value[i].usedTripAlternatives[j].alternatives[k].delayWhenBoarded.value = 0
                                        _searchResultList.value[i].usedTripAlternatives[j].alternatives[k].delayWhenBoarded = newResults[i].usedTripAlternatives[j].alternatives[k].delayWhenBoarded
                                        _searchResultList.value[i].usedTripAlternatives[j].alternatives[k].currentDelay = newResults[i].usedTripAlternatives[j].alternatives[k].currentDelay
                                        Log.i("DEBUG", "Delay after: ${_searchResultList.value[i].usedTripAlternatives[j].alternatives[k].delayWhenBoarded}")
                                    }
                                }

                            }
                        }

                    }
                }

            }
            else{
                _searchResultList.value = emptyList()
                _searchResultList.value = newResults
            }
        }


// =================================================================================================
// STOP NAME LIST MANAGEMENT
        // The list of stop names to be used for stop suggestions
        val stopNameList: StateFlow<List<StopEntry>> = stopListRepository.stopNameList.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Downloads and stores the current stop name list from the given URL
        fun downloadAndStoreStopNameList(url: String) {
            viewModelScope.launch {
                stopListRepository.downloadAndStoreJson(url)
            }
        }

        // The time of the last update of the stop name list
        val stopListLastUpdateTime: StateFlow<LocalDateTime> = stopListRepository.generatedAt
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), LocalDateTime.MIN)


// =================================================================================================
// APPLICATION STATE MANAGEMENT
        private val _navigateToResults = MutableStateFlow(false)
        val navigateToResults: StateFlow<Boolean> = _navigateToResults
        fun updateNavigateToResults(navigateToResults: Boolean){
            _navigateToResults.value = navigateToResults
        }



        private val _expandingSearchToPast = MutableStateFlow(false)
        val expandingSearchToPast: StateFlow<Boolean> = _expandingSearchToPast
        fun updateExpandingSearchToPast(expanding: Boolean) {
            _expandingSearchToPast.value = expanding
        }


        private val _expandingSearchToFuture = MutableStateFlow(false)
        val expandingSearchToFuture: StateFlow<Boolean> = _expandingSearchToFuture
        fun updateExpandingSearchToFuture(expanding: Boolean) {
            _expandingSearchToFuture.value = expanding
        }



        //----------------------------------------------------------------------------------

        fun getSearchSettings(): SearchSettings {
            return SearchSettings(
                walkingPace = walkingPace.value,
                cyclingPace = cyclingPace.value,
                bikeUnlockTime = bikeUnlockTime.value,
                bikeLockTime = bikeLockTime.value,
                useSharedBikes = useSharedBikes.value,
                bikeMax15Minutes = bikeMax15Minutes.value,
                transferTime = transferBuffer.value.toInt(),
                comfortBalance = timeComfortBalance.value.toInt(),
                walkingPreference = transferLength.value.toInt(),
                bikeTripBuffer = bikeTripBuffer.value.toInt()
            )
        }



        fun splitNormalizedWords(normalizedInput: String): List<String> {
            return normalizedInput.split("[\\s,-]+".toRegex()).map { it.trim() }
        }
        fun getStopSuggestions(query: String, stopNames: List<StopEntry>): List<StopEntry> {
            val queryParts = query.trim().split("\\s+".toRegex()).map { it.trim() }
            val matchedStops = stopNames.filter { stopName ->
                val nameParts = splitNormalizedWords(stopName.normalizedName)
                nameParts.windowed(queryParts.size).any { window ->
                    queryParts.indices.all { index ->
                        window[index].startsWith(queryParts[index], ignoreCase = true)
                    }
                }
            }
            val firstWordMatches = matchedStops.filter { stopName ->
                val nameParts = splitNormalizedWords(stopName.normalizedName)
                nameParts.firstOrNull()?.startsWith(queryParts.first(), ignoreCase = true) == true
            }
            val otherMatches = matchedStops.filterNot { stopName ->
                val nameParts = splitNormalizedWords(stopName.normalizedName)
                nameParts.firstOrNull()?.startsWith(queryParts.first(), ignoreCase = true) == true
            }
            return (firstWordMatches + otherMatches).take(16)
        }

        @OptIn(FlowPreview::class)
        val fromStopSuggestions: StateFlow<List<StopEntry>> =
            fromSearchQuery
                .debounce(200)
                .combine(stopNameList) { fromSearchQuery, stopNames ->
                    getStopSuggestions(fromSearchQuery, stopNames)
                }
                .stateIn(
                    scope = viewModelScope,
                    initialValue = emptyList(),
                    started = SharingStarted.WhileSubscribed(5_000)
                )

        @OptIn(FlowPreview::class)
        val toStopSuggestions: StateFlow<List<StopEntry>> =
            toSearchQuery
                .debounce(200)
                .combine(stopNameList) { toSearchQuery, stopNames ->
                    getStopSuggestions(toSearchQuery, stopNames)
                }
                .stateIn(
                    scope = viewModelScope,
                    initialValue = emptyList(),
                    started = SharingStarted.WhileSubscribed(5_000)
                )


        private fun cleanUpDuplicates(searchResults: List<ConnectionSearchResult>): List<ConnectionSearchResult> {
            val orderedSearchResults = searchResults.sortedBy { it.departureDateTime }
            val cleanedResults = mutableListOf<ConnectionSearchResult>()

            for (result in orderedSearchResults) {
                if (cleanedResults.isEmpty() ||
                    result.departureDateTime != cleanedResults.last().departureDateTime ||
                    result.arrivalDateTime != cleanedResults.last().arrivalDateTime ||
                    result.usedSegmentTypes != cleanedResults.last().usedSegmentTypes
                ) {
                    cleanedResults.add(result)
                }
            }

            return cleanedResults
        }

        fun combineResultLists(
            currentResults: List<ConnectionSearchResult>,
            newResults: List<ConnectionSearchResult>,
            toPast: Boolean
        ) : List<ConnectionSearchResult>{
            var nonIdenticalNewResults : List<ConnectionSearchResult> = newResults
            if(toPast){
                while(currentResults.contains(nonIdenticalNewResults.last())){
                    nonIdenticalNewResults = nonIdenticalNewResults.dropLast(1)
                }

                return cleanUpDuplicates(nonIdenticalNewResults + currentResults)
            } else {
                while(nonIdenticalNewResults.size != 0 && currentResults.contains(nonIdenticalNewResults[0])){
                    nonIdenticalNewResults = nonIdenticalNewResults.drop(1)
                }

                //TODO: Check if necessary
                return cleanUpDuplicates(currentResults + nonIdenticalNewResults)
            }

        }





        fun combineAlternativesLists(currResults: List<UsedTrip>, newResults: List<UsedTrip>, newAreEarlier: Boolean) : List<UsedTrip>{
            var nonIdenticalNewResults : List<UsedTrip> = newResults

            if(newAreEarlier){
                while(nonIdenticalNewResults.isNotEmpty() && currResults.any { it.tripId == nonIdenticalNewResults.last().tripId }){
                    nonIdenticalNewResults = nonIdenticalNewResults.dropLast(1)
                }
                return nonIdenticalNewResults + currResults
            } else {
                while(nonIdenticalNewResults.isNotEmpty() && currResults.any { it.tripId == nonIdenticalNewResults.first().tripId }){
                    nonIdenticalNewResults = nonIdenticalNewResults.drop(1)
                }
                return currResults + nonIdenticalNewResults
            }
        }




        fun updateCurrIndex(result: ConnectionSearchResult, tripIndex: Int, newIndex: Int){
            val resultIndex = searchResultList.value.indexOf(result)
            if(resultIndex != -1){
                Log.i("DEBUG", "Updating curr index to $newIndex")
                _searchResultList.value[resultIndex].usedTripAlternatives[tripIndex].currIndex = newIndex
            }
            else{
                Log.i("ERROR", "Result not found")
            }
        }
    }