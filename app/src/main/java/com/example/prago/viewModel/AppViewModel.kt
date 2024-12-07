    package com.example.prago.viewModel

    import android.content.Context
    import android.util.Log
    import androidx.datastore.core.CorruptionException
    import androidx.datastore.core.DataStore
    import androidx.datastore.core.Serializer
    import androidx.datastore.dataStore
    import androidx.datastore.preferences.core.Preferences
    import androidx.datastore.preferences.preferencesDataStore
    import androidx.lifecycle.LiveData
    import androidx.lifecycle.MutableLiveData
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.example.prago.R
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
    import kotlinx.coroutines.flow.map
    import kotlinx.coroutines.flow.stateIn
    import kotlinx.coroutines.launch
    import kotlinx.coroutines.withContext
    import java.io.InputStream
    import java.io.OutputStream
    import java.lang.Thread.State
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
        private val _navigateToResults = MutableStateFlow(false)
        val navigateToResults: StateFlow<Boolean> = _navigateToResults

        suspend fun resetNavigateToResults(){
            _navigateToResults.value = false
        }

        /*private val _stopNameList = MutableLiveData<List<StopEntry>>()
        val stopNameList : LiveData<List<StopEntry>> = _stopNameList


        private val _currSearchResults = MutableLiveData<List<ConnectionSearchResult>>()
        val currSearchResults : LiveData<List<ConnectionSearchResult>> = _currSearchResults



        private val _useSharedBikes = MutableLiveData<Boolean>()
        val useSharedBikes : LiveData<Boolean> = _useSharedBikes

        private val _transferBuffer = MutableLiveData<Float>()
        val transferBuffer : LiveData<Float> = _transferBuffer

        private val _transferLength = MutableLiveData<Float>()
        val transferLength : LiveData<Float> = _transferLength

        private val _comfortPreference = MutableLiveData<Float>()
        val comfortPreference : LiveData<Float> = _comfortPreference

        private val _bikeTripBuffer = MutableLiveData<Float>()
        val bikeTripBuffer : LiveData<Float> = _bikeTripBuffer


        private val _walkingPace = MutableLiveData<Int>()
        val walkingPace : LiveData<Int> = _walkingPace

        private val _cyclingPace = MutableLiveData<Int>()
        val cyclingPace : LiveData<Int> = _cyclingPace

        private val _bikeUnlockTime = MutableLiveData<Int>()
        val bikeUnlockTime : LiveData<Int> = _bikeUnlockTime

        private val _bikeLockTime = MutableLiveData<Int>()
        val bikeLockTime : LiveData<Int> = _bikeLockTime



        private val _expandingToPast = MutableLiveData<Boolean>(false)
        val expandingToPast : LiveData<Boolean> = _expandingToPast

        private val _expandingToFuture = MutableLiveData<Boolean>(false)
        val expandingToFuture : LiveData<Boolean> = _expandingToFuture



        private val _fromSearchQuery = MutableLiveData<String>()
        val fromSearchQuery : LiveData<String> = _fromSearchQuery

        private val _toSearchQuery = MutableLiveData<String>()
        val toSearchQuery : LiveData<String> = _toSearchQuery*/


        val transferBuffer: StateFlow<Float> = settingsRepository.transferBuffer.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = transferBufferDefault
        )

        val transferLength: StateFlow<Float> = settingsRepository.transferLength.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = transferLengthDefault
        )

        val comfortPreference: StateFlow<Float> = settingsRepository.comfortPreference.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = comfortPreferenceDefault
        )

        val bikeTripBuffer: StateFlow<Float> = settingsRepository.bikeTripBuffer.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = bikeTripBufferDefault
        )

        val useSharedBikes: StateFlow<Boolean> = settingsRepository.useSharedBikes.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = useSharedBikesDefault
        )
        val bikeMax15Minutes: StateFlow<Boolean> = settingsRepository.bikeMax15Minutes.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )



        val walkingPace: StateFlow<Int> = settingsRepository.walkingPace.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = walkingPaceDefault
        )

        val cyclingPace: StateFlow<Int> = settingsRepository.cyclingPace.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = cyclingPaceDefault
        )

        val bikeUnlockTime: StateFlow<Int> = settingsRepository.bikeUnlockTime.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = bikeUnlockTimeDefault
        )

        val bikeLockTime: StateFlow<Int> = settingsRepository.bikeLockTime.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = bikeLockTimeDefault
        )

        fun getIntSettingValue(preferencesKey: String, defaultValue: Int): StateFlow<Int> {
            //return settingsRepository.retrieveIntSetting(preferencesKey, defaultValue)
            return settingsRepository.getIntSettingValue(preferencesKey, defaultValue).stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = defaultValue
            )
        }


        fun saveTransferBuffer(value: Float) {
            viewModelScope.launch {
                settingsRepository.saveTransferBuffer(value)
            }
        }

        fun saveTransferLength(value: Float) {
            viewModelScope.launch {
                settingsRepository.saveTransferLength(value)
            }
        }

        fun saveComfortPreference(value: Float) {
            viewModelScope.launch {
                settingsRepository.saveComfortPreference(value)
            }
        }

        fun saveBikeTripBuffer(value: Float) {
            viewModelScope.launch {
                settingsRepository.saveBikeTripBuffer(value)
            }
        }

        fun saveUseSharedBikes(value: Boolean) {
            viewModelScope.launch {
                settingsRepository.saveUseSharedBikes(value)
            }
        }
        fun saveBikeMax15Minutes(value: Boolean) {
            viewModelScope.launch {
                settingsRepository.saveBikeMax15Minutes(value)
            }
        }


        fun saveWalkingPace(value: Int) {
            viewModelScope.launch {
                settingsRepository.saveWalkingPace(value)
            }
        }

        fun saveCyclingPace(value: Int) {
            viewModelScope.launch {
                settingsRepository.saveCyclingPace(value)
            }
        }

        fun saveBikeUnlockTime(value: Int) {
            viewModelScope.launch {
                settingsRepository.saveBikeUnlockTime(value)
            }
        }

        fun saveBikeLockTime(value: Int) {
            viewModelScope.launch {
                settingsRepository.saveBikeLockTime(value)
            }
        }


        val stopNameList: StateFlow<List<StopEntry>> = stopListRepository.stopNameList.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Function to trigger JSON download and store operation
        fun downloadAndStoreStopList(url: String) {
            viewModelScope.launch {
                stopListRepository.downloadAndStoreJson(url)
            }
        }

        private val _fromSearchQuery = MutableStateFlow("")
        val fromSearchQuery: StateFlow<String> = _fromSearchQuery

        private val _toSearchQuery = MutableStateFlow("")
        val toSearchQuery: StateFlow<String> = _toSearchQuery

        private val _expandingSearchToPast = MutableStateFlow(false)
        val expandingSearchToPast: StateFlow<Boolean> = _expandingSearchToPast

        private val _expandingSearchToFuture = MutableStateFlow(false)
        val expandingSearchToFuture: StateFlow<Boolean> = _expandingSearchToFuture

//        private val _expansionToPastItems = MutableStateFlow(0)
//        val expansionToPastItems: StateFlow<Int> = _expansionToPastItems

        private val _selectedDate = MutableStateFlow(LocalDate.now())
        val selectedDate: StateFlow<LocalDate> = _selectedDate

        private val _selectedTime = MutableStateFlow(LocalTime.now())
        val selectedTime: StateFlow<LocalTime> = _selectedTime

        private val _departureNow = MutableStateFlow(true)
        val departureNow: StateFlow<Boolean> = _departureNow

        private val _byEarliestDeparture = MutableStateFlow(true)
        val byEarliestDeparture: StateFlow<Boolean> = _byEarliestDeparture

        private val _searchRangeStart = MutableStateFlow(LocalDateTime.now())
        val searchRangeStart: StateFlow<LocalDateTime> = _searchRangeStart

        private val _searchRangeEnd = MutableStateFlow(LocalDateTime.now())
        val searchRangeEnd: StateFlow<LocalDateTime> = _searchRangeEnd


        val stopListLastUpdateTime: StateFlow<LocalDateTime> = stopListRepository.generatedAt
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), LocalDateTime.MIN)


        // Methods to update the state
        fun updateFromSearchQuery(query: String) {
            _fromSearchQuery.value = query
        }

        fun updateToSearchQuery(query: String) {
            _toSearchQuery.value = query
        }

        fun updateExpandingSearchToPast(expanding: Boolean) {
            _expandingSearchToPast.value = expanding
        }

        fun updateExpandingSearchToFuture(expanding: Boolean) {
            _expandingSearchToFuture.value = expanding
        }

//        fun updateExpansionToPastItems(count: Int) {
//            _expansionToPastItems.value = count
//        }

        fun updateSelectedDate(date: LocalDate) {
            _selectedDate.value = date
        }

        fun updateSelectedTime(time: LocalTime) {
            _selectedTime.value = time
        }

        fun updateDepartureNow(isNow: Boolean) {
            _departureNow.value = isNow
        }

        fun updateByEarliestDeparture(isEarliest: Boolean) {
            _byEarliestDeparture.value = isEarliest
        }

        fun updateSearchRangeStart(dateTime: LocalDateTime) {
            _searchRangeStart.value = dateTime
        }

        fun updateSearchRangeEnd(dateTime: LocalDateTime) {
            _searchRangeEnd.value = dateTime
        }


        private val _searchResultList = MutableStateFlow<List<ConnectionSearchResult>>(emptyList())
        val searchResultList: StateFlow<List<ConnectionSearchResult>> = _searchResultList

        fun updateSearchResults(newResults: List<ConnectionSearchResult>) {
            _searchResultList.value = newResults
        }

        fun getCurrIndexFlow(resultIndex: Int, tripIndex: Int): StateFlow<Int?> {
            return searchResultList.map { resultList ->
                // Safely retrieve currIndex for the given resultIndex and tripIndex
                resultList.getOrNull(resultIndex)?.usedTripAlternatives?.getOrNull(tripIndex)?.currIndex
            }.stateIn(viewModelScope, SharingStarted.Lazily, null) // Default value: null or any appropriate default
        }

        fun getCurrIndexFlow(result: ConnectionSearchResult, tripIndex: Int): StateFlow<Int> {
            return searchResultList.map { resultList ->
                // Find the specific result in the list and get the currIndex for the given tripIndex
                resultList.find { it == result }
                    ?.usedTripAlternatives
                    ?.getOrNull(tripIndex)
                    ?.currIndex ?: -1
            }.stateIn(viewModelScope, SharingStarted.Lazily, -1) // Default value: null or any appropriate default
        }

//        fun updateCurrIndexForTrip(result: ConnectionSearchResult, tripIndex: Int, newIndex: Int){
//            val resultIndex = searchResultList.value.indexOf(result)
//            if(resultIndex != -1){
//                Log.i("DEBUG", "Updating curr index to $newIndex")
//                _searchResultList.value[resultIndex].usedTripAlternatives[tripIndex].currIndex = newIndex
//            }
//            else{
//                Log.i("ERROR", "Result not found")
//            }
//        }

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
                comfortBalance = comfortPreference.value.toInt(),
                walkingPreference = transferLength.value.toInt(),
                bikeTripBuffer = bikeTripBuffer.value.toInt()
            )
        }



        fun splitNormalizedWords(normalizedInput: String): List<String> {
            return normalizedInput.split("[\\s,-]+".toRegex()).map { it.trim() }
        }

        @OptIn(FlowPreview::class)
        val fromStopSuggestions: StateFlow<List<StopEntry>> =
            fromSearchQuery
                .debounce(300) // Apply debounce here
                .combine(stopNameList) { fromSearchQuery, stopNames ->
                    val queryParts = fromSearchQuery.trim().split("\\s+".toRegex()).map { it.trim() }
                    stopNames.filter { stopName ->
                        val nameParts = splitNormalizedWords(stopName.normalizedName)

                        // Ensure that each part of the query matches the beginning of corresponding words in the stop name
                        queryParts.all { queryPart ->
                            nameParts.any { namePart ->
                                namePart.startsWith(queryPart, ignoreCase = true)
                            }
                        }
                    }.take(16)
                }
                .stateIn(
                    scope = viewModelScope,
                    initialValue = emptyList(),
                    started = SharingStarted.WhileSubscribed(5_000)
                )

        @OptIn(FlowPreview::class)
        val toStopSuggestions: StateFlow<List<StopEntry>> =
            toSearchQuery
                .debounce(300) // Apply debounce here
                .combine(stopNameList) { toSearchQuery, stopNames ->
                    val queryParts = toSearchQuery.trim().split("\\s+".toRegex()).map { it.trim() }
                    stopNames.filter { stopName ->
                        val nameParts = splitNormalizedWords(stopName.normalizedName)

                        // Ensure that each part of the query matches the beginning of corresponding words in the stop name
                        queryParts.all { queryPart ->
                            nameParts.any { namePart ->
                                namePart.startsWith(queryPart, ignoreCase = true)
                            }
                        }
                    }.take(16)
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

        suspend fun expandSearch(toPast: Boolean, context: Context){
            val rangeStart: LocalDateTime

            if(toPast){
                updateExpandingSearchToPast(true)
                rangeStart = searchRangeStart.value.minusMinutes(15)
            }else{
                updateExpandingSearchToFuture(true)
                rangeStart = searchRangeEnd.value
            }

            Log.i("DEBUG", "Expanding search to ${if (toPast) "past" else "future"}")


            Log.i("DEBUG", "Expanding with range start: $rangeStart")


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
                            Log.i("AppViewModel", "Fetched search results to expand: ${results.size}")

                            val currentResults = searchResultList.value
                            var combinedResults = combineResultLists(currentResults, results, toPast)


                            Log.i("AppViewModel", "Combined results: ${combinedResults.size}")
                            updateSearchResults(combinedResults)
                            //updateExpansionToPastItems()


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
            }

            updateExpandingSearchToPast(false)
            updateExpandingSearchToFuture(false)
        }

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
                //try{
                    connectionSearchApi.searchForConnection(searchRequest, context).collect{ result ->
                        when(result){
                            is ConnectionSearchResultState.Success -> {
                                val results = result.results

                                updateSearchResults(results)
                                updateSearchRangeStart(startDateTime)
                                updateSearchRangeEnd(startDateTime.plusMinutes(15))

                                _navigateToResults.value = true
                            }
                            is ConnectionSearchResultState.Failure -> {
                                Log.e("AppViewModel", "Error fetching search results: ${result.errorMessage}")
                                setErrorMessage(result.errorMessage)
                                showDialog(true)
                            }
                        }
                    }
//                } catch(e: Exception){
//                    Log.e("AppViewModel", "Error starting search: ${e.message}")
//                    setErrorMessage(context.getString(R.string.an_error_occurred) + ":" + e.message)
//                    showDialog(true)
//                }
            }
        }

        suspend fun fetchAlternatives(
            searchResult: ConnectionSearchResult,
            tripIndex: Int,
            earlier: Boolean,
            context: Context
        ){
            val usedTripAlternativesObject = searchResult.usedTripAlternatives[tripIndex]
            val alternatives = usedTripAlternativesObject.alternatives
            val usedTrip = if (earlier) alternatives.first() else alternatives.last()

            val srcStopId = usedTrip.stopPasses[usedTrip.getOnStopIndex].id
            val destStopId = usedTrip.stopPasses[usedTrip.getOffStopIndex].id

            val dateTimeWithDelay = usedTrip.stopPasses[usedTrip.getOnStopIndex].departureTime.plusSeconds(usedTrip.delayWhenBoarded.toLong())
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
                                val usedTrips = result.results
                                val allAlternatives: List<UsedTrip> = if (earlier) usedTrips + alternatives else alternatives + usedTrips
                                val newIndex = if (earlier) usedTrips.size - 1 else alternatives.size

                                searchResult.usedTripAlternatives[tripIndex] = TripAlternatives(
                                    currIndex = newIndex,
                                    alternatives = allAlternatives,
                                    count = allAlternatives.size
                                )

                                var searchResultsListFFFUUU = searchResultList.value.toMutableList()
                                searchResultsListFFFUUU[0] = searchResult

                                //updateSearchResults(searchResultsListFFFUUU) //TODO: check if necessary

                                Log.i("AppViewModel", "Fetched alternatives: ${allAlternatives.size}")
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