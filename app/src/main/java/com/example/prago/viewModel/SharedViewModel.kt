//@file:OptIn(ExperimentalSerializationApi::class)
//
//package com.example.prago.viewModel
//
//import android.content.Context
//import android.util.Log
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.compose.runtime.snapshotFlow
//import androidx.datastore.core.CorruptionException
//import androidx.datastore.core.DataStore
//import androidx.datastore.core.Serializer
//import androidx.datastore.dataStore
//import androidx.datastore.preferences.core.Preferences
//import androidx.datastore.preferences.core.edit
//import androidx.datastore.preferences.core.floatPreferencesKey
//import androidx.datastore.preferences.core.intPreferencesKey
//import androidx.datastore.preferences.core.stringPreferencesKey
//import androidx.datastore.preferences.preferencesDataStore
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import androidx.navigation.NavController
//import com.example.prago.R
//import com.example.prago.model.dataClasses.ConnectionSearchResult
//import com.example.prago.StopList
//import com.example.prago.model.dataClasses.AlternativeTripsRequest
//import com.example.prago.model.dataClasses.CreateStopToStopRangeRequest
//import com.example.prago.model.dataClasses.SearchSettings
//import com.example.prago.model.dataClasses.StopListDataClass
//import com.example.prago.model.dataClasses.TripAlternatives
//import com.example.prago.model.dataClasses.UsedTrip
//import com.example.prago.model.dataClasses.toJsonObject
//import com.google.protobuf.InvalidProtocolBufferException
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import java.io.InputStream
//import java.io.OutputStream
//import java.time.LocalDate
//import java.time.LocalTime
//import khttp.get
//import khttp.responses.Response
//import kotlinx.coroutines.FlowPreview
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.SharingStarted
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.combine
//import kotlinx.coroutines.flow.debounce
//import kotlinx.coroutines.flow.distinctUntilChanged
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.flow.map
//import kotlinx.coroutines.flow.stateIn
//import kotlinx.serialization.ExperimentalSerializationApi
//import kotlinx.serialization.decodeFromString
//import kotlinx.serialization.json.Json
//import java.text.Normalizer
//import java.time.LocalDateTime
//import java.time.format.DateTimeFormatter
//
//
//object StopListSerializer : Serializer<StopList> {
//    override val defaultValue: StopList = StopList.getDefaultInstance()
//    override suspend fun readFrom(input: InputStream): StopList = withContext(Dispatchers.IO) {
//        try {
//            return@withContext StopList.parseFrom(input)
//        } catch (exception: InvalidProtocolBufferException) {
//            throw CorruptionException("Cannot read proto.", exception)
//        }
//    }
//
//    override suspend fun writeTo(t: StopList, output: OutputStream) = withContext(Dispatchers.IO) {t.writeTo(output)}
//}
//
//val Context.stopListDataStore: DataStore<StopList> by dataStore(
//    fileName = "settings.pb",
//    serializer = StopListSerializer
//)
//
//val Context.preferencesDataStore by preferencesDataStore(name = "preferences")
//
//
//fun convertToStopList(stopListDataClass: StopListDataClass): StopList {
//    val stopListBuilder = StopList.newBuilder()
//        .setGeneratedAt(stopListDataClass.generatedAt)
//        .setDataFormatVersion(stopListDataClass.dataFormatVersion)
//
//    for (stopGroupDataClass in stopListDataClass.stopGroups) {
//        val stopGroupBuilder = StopList.StopGroup.newBuilder()
//            .setName(stopGroupDataClass.name)
//            .setDistrictCode(stopGroupDataClass.districtCode)
//            .setIdosCategory(stopGroupDataClass.idosCategory)
//            .setIdosName(stopGroupDataClass.idosName)
//            .setFullName(stopGroupDataClass.fullName)
//            .setUniqueName(stopGroupDataClass.uniqueName)
//            .setNode(stopGroupDataClass.node)
//            .setCis(stopGroupDataClass.cis)
//            .setAvgLat(stopGroupDataClass.avgLat)
//            .setAvgLon(stopGroupDataClass.avgLon)
//            .setAvgJtskX(stopGroupDataClass.avgJtskX)
//            .setAvgJtskY(stopGroupDataClass.avgJtskY)
//            .setMunicipality(stopGroupDataClass.municipality)
//
//        for (stopDataClass in stopGroupDataClass.stops) {
//            val stopBuilder = StopList.StopGroup.Stop.newBuilder()
//                .setId(stopDataClass.id)
//                .setPlatform(stopDataClass.platform ?: "")
//                .setAltIdosName(stopDataClass.altIdosName)
//                .setLat(stopDataClass.lat)
//                .setLon(stopDataClass.lon)
//                .setJtskX(stopDataClass.jtskX)
//                .setJtskY(stopDataClass.jtskY)
//                .setZone(stopDataClass.zone)
//                .setWheelchairAccess(stopDataClass.wheelchairAccess)
//                .addAllGtfsIds(stopDataClass.gtfsIds)
//
//            for (lineDataClass in stopDataClass.lines) {
//                val lineBuilder = StopList.StopGroup.Stop.Line.newBuilder()
//                    .setId(lineDataClass.id)
//                    .setName(lineDataClass.name)
//                    .setType(lineDataClass.type)
//                    .setDirection(lineDataClass.direction)
//                    .setDirection2(lineDataClass.direction2 ?: "")
//
//                stopBuilder.addLines(lineBuilder)
//            }
//
//            stopGroupBuilder.addStops(stopBuilder)
//        }
//
//        stopListBuilder.addStopGroups(stopGroupBuilder)
//    }
//
//    return stopListBuilder.build()
//}
//
//data class StopEntry(val czechName: String, val normalizedName: String, val id: String)
//
//class SharedViewModel(val stopListDataStore: DataStore<StopList>,
//                      val preferencesDataStore: DataStore<Preferences>) : ViewModel() {
//    private val _stopNameList = MutableStateFlow<List<StopEntry>>(emptyList())
//    val stopNamesFlow: StateFlow<List<StopEntry>> = _stopNameList
//    //var searchResult = MutableLiveData<ConnectionSearchResult>()
//
//    var searchResultList = MutableLiveData<List<ConnectionSearchResult>>()
//
//
//    // Saved search parameter values
//    // TODO: check if both pairs needed
//    var fromText = mutableStateOf("")
//    var toText = mutableStateOf("")
//    var fromSearchQuery by mutableStateOf("")
//        private set
//    var toSearchQuery by mutableStateOf("")
//        private set
//
////    fun getSearchQuery(srcStop: Boolean): String {
////        return if (srcStop) fromSearchQuery else toSearchQuery
////    }
//
//    var expandingSearchToPast = mutableStateOf(false)
//    var expandingSearchToFuture = mutableStateOf(false)
//    var expansionToPastItems = mutableStateOf(0)
//
//    var useSharedBikes = mutableStateOf(false)
//    var transferBuffer = mutableStateOf(2f)
//    var transferLength = mutableStateOf(1f)
//    var comfortPreference = mutableStateOf(2f)
//    var bikeTripBuffer = mutableStateOf(2f)
//    var selectedDate = mutableStateOf(LocalDate.now())
//    var selectedTime = mutableStateOf(LocalTime.now())
//    var departureNow = mutableStateOf(true)
//    var byEarliestDeparture = mutableStateOf(true)
//
//    var searchRangeStart = mutableStateOf(LocalDateTime.now())
//    var searchRangeEnd = mutableStateOf(LocalDateTime.now())
//
//    val walkingPace = MutableStateFlow<Int?>(null)
//    val cyclingPace = MutableStateFlow<Int?>(null)
//    val bikeUnlockTime = MutableStateFlow<Int?>(null)
//    val bikeLockTime = MutableStateFlow<Int?>(null)
//
//
//    init {
//        viewModelScope.launch {
//            transferBuffer.value = retrieveFloatSetting("transferBuffer", 2f)
//            transferLength.value = retrieveFloatSetting("transferLength", 1f)
//            comfortPreference.value = retrieveFloatSetting("comfortPreference", 2f)
//            bikeTripBuffer.value = retrieveFloatSetting("bikeTripBuffer", 2f)
//            useSharedBikes.value = retrieveBoolSetting("useSharedBikes", false)
//
//            walkingPace.value = retrieveIntSetting("walkingPace", 12)
//            cyclingPace.value = retrieveIntSetting("cyclingPace", 5)
//            bikeUnlockTime.value = retrieveIntSetting("bikeUnlockTime", 30)
//            bikeLockTime.value = retrieveIntSetting("bikeLockTime", 15)
//        }
//        viewModelScope.launch(Dispatchers.IO) {
//            Log.i("DEBUG", "Init started")
//            stopListDataStore.data
//                .map { stopList ->
//                    // Map stop group names to list of StopEntry
//                    val stopEntries = stopList.stopGroupsList.map { stopGroup ->
//                        StopEntry(
//                            czechName = stopGroup.name,
//                            normalizedName = normalizeCzech(stopGroup.name),
//                            id = stopGroup.name + stopGroup.districtCode + stopGroup.cis
//                        )
//                    }
//                    stopEntries
//                }
//                .collect { value ->
//                    withContext(Dispatchers.Main) {
//                        _stopNameList.value = value
//                    }
//                }
//            Log.i("DEBUG", "Init done")
//        }
//    }
//
//
//    fun normalizeCzech(input: String): String {
//        val normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
//        return normalized.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
//    }
//
//    fun splitNormalizedWords(normalizedInput: String): List<String> {
//        return normalizedInput.split("[\\s,-]+".toRegex()).map { it.trim() }
//    }
//
//    @OptIn(FlowPreview::class)
//    val fromSearchResults: StateFlow<List<StopEntry>> =
//        snapshotFlow { fromSearchQuery }
//            .debounce(300)
//            .combine(stopNamesFlow) { fromSearchQuery, stopNames ->
//                val queryParts = fromSearchQuery.trim().split("\\s+".toRegex()).map { it.trim() }
//                stopNames.filter { stopName ->
//                    val nameParts = splitNormalizedWords(stopName.normalizedName)
//
//                    // Ensure that each part of the query matches the beginning of corresponding words in the stop name
//                    queryParts.all { queryPart ->
//                        nameParts.any { namePart ->
//                            namePart.startsWith(
//                                queryPart,
//                                ignoreCase = true
//                            )
//                        }
//                    }
//                }.take(16)
//            }.stateIn(
//                scope = viewModelScope,
//                initialValue = emptyList(),
//                started = SharingStarted.WhileSubscribed(5_000)
//            )
//
//    @OptIn(FlowPreview::class)
//    val toSearchResults: StateFlow<List<StopEntry>> =
//        snapshotFlow { toSearchQuery }
//            .debounce(300)
//            .combine(stopNamesFlow) { toSearchQuery, stopNames ->
//                val queryParts = toSearchQuery.trim().split("\\s+".toRegex()).map { it.trim() }
//                stopNames.filter { stopName ->
//                    val nameParts = splitNormalizedWords(stopName.normalizedName)
//
//                    queryParts.all { queryPart ->
//                        nameParts.any { namePart ->
//                            namePart.startsWith(
//                                queryPart,
//                                ignoreCase = true
//                            )
//                        }
//                    }
//                }.take(16)
//            }
//            .distinctUntilChanged()
//            .stateIn(
//                scope = viewModelScope,
//                initialValue = emptyList(),
//                started = SharingStarted.WhileSubscribed(5_000)
//            )
//
//    fun onFromSearchQueryChange(newQuery: String) {
//        fromSearchQuery = newQuery
//    }
//
//    fun onToSearchQueryChange(newQuery: String) {
//        toSearchQuery = newQuery
//    }
//
//    @OptIn(ExperimentalSerializationApi::class)
//    suspend fun downloadAndStoreJson(url: String) {
//        //Log.i("APP", "HERE")
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                Log.i("DEBUG", "Fetching response")
//                val response: Response = get(url = url)
//                Log.i("DEBUG", "Response fetched")
//                val stopListJson = String(response.content, Charsets.UTF_8)
//                val json = Json { ignoreUnknownKeys = true }
//                val stopList = json.decodeFromString<StopListDataClass>(stopListJson)
//                Log.i("DEBUG", "Storing response")
//                // Store the StopList object in the Proto DataStore
//                stopListDataStore.updateData { convertToStopList(stopList) }
//                Log.i("DEBUG", "Response stored")
//            } catch (e: Exception) {
//                print(e)
//            }
//        }
//    }
//
//
//    suspend fun saveBoolSetting(key: String, value: Boolean) {
//        val dataStoreKey = stringPreferencesKey(key)
//        preferencesDataStore.edit { preferences ->
//            preferences[dataStoreKey] = value.toString()
//        }
//    }
//
//
//    suspend fun retrieveBoolSetting(key: String, defaultValue: Boolean): Boolean {
//        val dataStoreKey = stringPreferencesKey(key)
//        val preferences = preferencesDataStore.data.first()
//        return preferences[dataStoreKey]?.toBoolean() ?: defaultValue
//    }
//
//    suspend fun saveIntSetting(key: String, value: Int) {
//        val dataStoreKey = intPreferencesKey(key)
//        preferencesDataStore.edit { preferences ->
//            preferences[dataStoreKey] = value
//        }
//    }
//
//    suspend fun retrieveIntSetting(key: String, defaultValue: Int): Int {
//        val dataStoreKey = intPreferencesKey(key)
//        val preferences = preferencesDataStore.data.first()
//        return preferences[dataStoreKey] ?: defaultValue
//    }
//
//    suspend fun saveFloatSetting(key: String, value: Float) {
//        val dataStoreKey = floatPreferencesKey(key)
//        preferencesDataStore.edit { preferences ->
//            preferences[dataStoreKey] = value
//        }
//    }
//
//    suspend fun retrieveFloatSetting(key: String, defaultValue: Float): Float {
//        val dataStoreKey = floatPreferencesKey(key)
//        val preferences = preferencesDataStore.data.first()
//        return preferences[dataStoreKey] ?: defaultValue
//    }
//
//    suspend fun expandSearch(toPast: Boolean) {
//        if (toPast) {
//            expandingSearchToPast.value = true
//            //expansionToPastItems.value = 0
//        } else {
//            expandingSearchToFuture.value = true
//        }
//
//        Log.i("DEBUG", "Expanding search, toPast: $toPast")
//        val rangeStart =
//            if (toPast) searchRangeStart.value.minusMinutes(15) else searchRangeEnd.value
//
//
//        Log.i("DEBUG", "Range start: $rangeStart")
//        val response = withContext(Dispatchers.IO) {
//            sendRequest(rangeStart, 15)
//        }
//
//        when (response.statusCode) {
//            200 -> {
//                val connectionSearchResults: List<ConnectionSearchResult> =
//                    Json.decodeFromString(response.text)
//                processRawSearchResults(connectionSearchResults)
//                Log.i("DEBUG", "Results fetched")
//                //withContext(Dispatchers.Main) {
//                val currentList = searchResultList.value ?: emptyList()
//                var newList = connectionSearchResults
//
//                if (toPast) {
//                    for (i in currentList.size - 1 downTo 0) {
//                        if (currentList[i].arrivalDateTime == newList[newList.size - 1].arrivalDateTime) {
//                            newList = newList.subList(0, newList.size - 1)
//                        }
//                    }
//
//                    val newSearchResultList = cleanUpDuplicates(newList + currentList)
//
//                    searchResultList.value = newSearchResultList
//                    expansionToPastItems.value = newList.size
//                    Log.i("DEBUG", "Newlist size: ${newList.size}")
////                        if(newList[newList.size - 1].arrivalDateTime == currentList[0].arrivalDateTime){
////                            searchResultList.value = newList.subList(0, newList.size - 1) + currentList
////                            expansionToPastItems.value = newList.size - 1
////                        } else{
////                            searchResultList.value = newList + currentList
////                            expansionToPastItems.value = newList.size
////                        }
//                } else {
//                    for (i in 0 until currentList.size) {
//                        if (currentList[i].arrivalDateTime == newList[0].arrivalDateTime) {
//                            newList = newList.subList(1, newList.size)
//                        }
//                    }
//
//                    val  newSearchResultList = cleanUpDuplicates(currentList + newList)
//
//                    searchResultList.value = newSearchResultList
//
//
////                        if(currentList[currentList.size - 1].arrivalDateTime == newList[0].arrivalDateTime){
////                            searchResultList.value = currentList.subList(0, currentList.size - 1) + newList
////                        } else{
////                            searchResultList.value = currentList + newList
////                        }
//                    //currentList + newList
//                }
//
//
//                //searchResultList.value = cleanUpDuplicates(searchResultList.value!!)
//                if (toPast) {
//                    searchRangeStart.value = searchRangeStart.value.minusMinutes(15)
//                } else {
//                    searchRangeEnd.value = searchRangeEnd.value.plusMinutes(15)
//                }
////                    searchRangeStart.value = //selectedDate.value.atTime(selectedTime.value)
////                    searchRangeEnd.value = selectedDate.value.atTime(selectedTime.value).plusMinutes(15)
//                //}
//            }
//
//            404, 502 -> { /* handle other status codes if needed */
//            }
//
//            else -> { /* handle other status codes if needed */
//            }
//        }
//
////        if(toPast){
////            searchRangeStart.value = searchRangeStart.value.minusMinutes(15)
////        } else {
////            searchRangeEnd.value = searchRangeEnd.value.plusMinutes(15)
////        }
//        expandingSearchToPast.value = false
//        expandingSearchToFuture.value = false
//    }
//
////    suspend fun sendAlternativeTripsRequest(
////        srcStopId: String,
////        destStopId: String,
////        dateTime: String,
////        previous: Boolean,
////        count: Int
////    ): Response = withContext(Dispatchers.IO) {
////        val request = AlternativeTripsRequest(
////            srcStopId = srcStopId,
////            destStopId = destStopId,
////            dateTime = dateTime,
////            previous = previous,
////            count = count
////        )
////
////        khttp.post(
////            url = "http://prago.xyz/alternative-trips",
////            json = request.toJsonObject()
////        )
////    }
//
//    suspend fun sendRequest(rangeStart: LocalDateTime, rangeLength: Int): Response =
//        withContext(Dispatchers.IO) {
//            val settings = SearchSettings(
//                walkingPace = walkingPace.value ?: 12,
//                cyclingPace = cyclingPace.value ?: 5,
//                bikeUnlockTime = bikeUnlockTime.value ?: 30,
//                bikeLockTime = bikeLockTime.value ?: 15,
//                useSharedBikes = useSharedBikes.value,
//                bikeMax15Minutes = true,
//                transferTime = transferBuffer.value.toInt(),
//                comfortBalance = comfortPreference.value.toInt(),
//                walkingPreference = transferLength.value.toInt(),
//                bikeTripBuffer = bikeTripBuffer.value.toInt()
//            )
//
//            val request = CreateStopToStopRangeRequest(
//                srcStopName = fromText.value,
//                destStopName = toText.value,
//                dateTime = rangeStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")),
//                byEarliestDeparture = byEarliestDeparture.value,
//                settings = settings,
//                rangeLength = rangeLength
//            )
//
//            khttp.post(
//                url = "http://prago.xyz/connection",
//                json = request.toJsonObject()
//            )
//        }
//
//
////    fun processRawSearchResults(rawSearchResults: List<ConnectionSearchResult>) {
////        for (searchResult in rawSearchResults) {
////            searchResult.usedTripAlternativesLists =
////                searchResult.usedTrips.map { listOf(it) }.toMutableList()
////            searchResult.usedTripsIndices = MutableList(searchResult.usedTrips.size) { 0 }
////        }
////    }
//
//    fun processRawSearchResults(rawSearchResults: List<ConnectionSearchResult>) {
//        /*for (searchResult in rawSearchResults) {
//            // Create a SnapshotStateList from a mutable list
//            searchResult.usedTripAlternatives =
//                mutableStateListOf(*searchResult.usedTrips.map {
//                    TripAlternatives(
//                        currIndex = 0,
//                        alternatives = listOf(it),
//                        count = 1
//                    )
//                }.toTypedArray())
//
//
//
//            // Create a SnapshotStateList for usedTripsIndices as well
//            //searchResult.usedTripsIndices = mutableStateListOf(*MutableList(searchResult.usedTrips.size) { 0 }.toTypedArray())
//        }*/
//    }
//
////    fun cleanUpDuplicates(searchResults: List<ConnectionSearchResult>): List<ConnectionSearchResult> {
////        val orderedSearchResults = searchResults.sortedBy { it.departureDateTime }
////
////
////
////
////
////        val seenCombinations = mutableSetOf<Pair<String, String>>()
////        return searchResults.filter { result ->
////            Log.i("DEBUG", "arrivalDateTime: ${result.arrivalDateTime}, usedSegmentTypes: ${result.usedSegmentTypes.joinToString()}")
////            val key = (result.arrivalDateTime.toString()) to result.usedSegmentTypes.joinToString()
////            if (key in seenCombinations) {
////                false
////            } else {
////                seenCombinations.add(key)
////                true
////            }
////            true
////        }
////    }
//
//
//    fun cleanUpDuplicates(searchResults: List<ConnectionSearchResult>): List<ConnectionSearchResult> {
//        val orderedSearchResults = searchResults.sortedBy { it.departureDateTime }
//        val cleanedResults = mutableListOf<ConnectionSearchResult>()
//
//        for (result in orderedSearchResults) {
//            if (cleanedResults.isEmpty() ||
//                result.departureDateTime != cleanedResults.last().departureDateTime ||
//                result.arrivalDateTime != cleanedResults.last().arrivalDateTime ||
//                result.usedSegmentTypes != cleanedResults.last().usedSegmentTypes
//            ) {
//                cleanedResults.add(result)
//            }
//        }
//
//        return cleanedResults
//    }
//
//
//
//
//
//
//
////    suspend fun getAlternativeTrips(resultIndex: Int, tripIndex: Int, before: Boolean): Response =
////        withContext(Dispatchers.IO) {
////            val searchResultToModify = searchResultList.value?.get(resultIndex)
////            val usedTrip = searchResultToModify!!.usedTrips.get(tripIndex)
////
////            val srcStopId = usedTrip.stopPasses[usedTrip.getOnStopIndex].id
////            val destStopId = usedTrip.stopPasses[usedTrip.getOffStopIndex].id
////            val dateTime = usedTrip.stopPasses[usedTrip.getOnStopIndex].departureTime.format(
////                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
////            )
////            val count = 5
////
////            val alternativesRequest = AlternativeTripsRequest(
////                srcStopId = srcStopId,
////                destStopId = destStopId,
////                dateTime = dateTime,
////                previous = before,
////                count = count
////            )
////
////            khttp.post(
////                url = "http://prago.xyz/alternative-trips",
////                json = alternativesRequest.toJsonObject()
////            )
////        }
//
//
//    suspend fun getAlternatives(
//        searchResult: ConnectionSearchResult,
//        tripIndex: Int,
//        before: Boolean
//    ) {
//        //val usedTrip = if(before) // first
//        //    searchResult.usedTripAlternativesLists[tripIndex][0]
//        //else // last
//        //    searchResult.usedTripAlternativesLists[tripIndex][searchResult.usedTripAlternativesLists[tripIndex].size - 1]
//
//        val usedTripAlternatives = searchResult.usedTripAlternatives[tripIndex]
//        val oldTripAltList = usedTripAlternatives.alternatives
//        val usedTrip = if(before) usedTripAlternatives.alternatives[0] else usedTripAlternatives.alternatives[usedTripAlternatives.alternatives.size - 1]
//
//        //val usedTrip = searchResult.usedTripAlternativesLists[tripIndex][searchResult.usedTripsIndices[tripIndex]]//searchResult.usedTrips.get(tripIndex)
//
//        val srcStopId = usedTrip.stopPasses[usedTrip.getOnStopIndex].id
//        val destStopId = usedTrip.stopPasses[usedTrip.getOffStopIndex].id
//
//        val rangeStartWithDelay = usedTrip.stopPasses[usedTrip.getOnStopIndex].departureTime.plusSeconds(usedTrip.delayWhenBoarded.toLong())
//        val rangeStartDateTime = if(before) {
//            rangeStartWithDelay
//        } else {
//            rangeStartWithDelay.plusSeconds(1)
//        }
//        val dateTime = rangeStartDateTime.format(
//            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
//        )
//        val count = 5
//
//        val tripId = usedTrip.tripId
//
//        Log.i("DEBUG", "---------------------------------------------------")
//        Log.i("DEBUG", "Sending request with:")
//        Log.i("DEBUG", "srcStopId: $srcStopId")
//        Log.i("DEBUG", "destStopId: $destStopId")
//        Log.i("DEBUG", "dateTime: $dateTime")
//        Log.i("DEBUG", "before: $before")
//        Log.i("DEBUG", "count: $count")
//        Log.i("DEBUG", "---------------------------------------------------")
//
//        val alternativesRequest = AlternativeTripsRequest(
//            srcStopId = srcStopId,
//            destStopId = destStopId,
//            dateTime = dateTime,
//            previous = before,
//            count = count,
//            tripId = tripId
//        )
//
//        val response = khttp.post(
//            url = "http://prago.xyz/alternative-trips",
//            json = alternativesRequest.toJsonObject()
//        )
//
//        when (response.statusCode) {
//            200 -> {
//                //Log.i("DEBUG", "Old alt count: ${searchResult.usedTripAlternativesLists[tripIndex].size}")
//                val newTripAltList = Json.decodeFromString<List<UsedTrip>>(response.text)
//                //Log.i("DEBUG", "Alternatives: ${alternatives.size}")
//                //val existingTripAlternatives = searchResult.usedTripAlternativesLists.get(tripIndex)
//                //val existingIndex = searchResult.usedTripsIndices.get(tripIndex)
//                //Log.i("DEBUG", "Existing alt count: ${existingTripAlternatives.size}, alternatives size: ${alternatives.size}")
//                //searchResult.usedTripAlternativesLists[tripIndex] =
//                //    if (before) (alternatives + existingTripAlternatives) else (existingTripAlternatives + alternatives)
//                val allTripAltList: List<UsedTrip> = if(before) (newTripAltList + oldTripAltList) else (oldTripAltList + newTripAltList)
//                val newIndex = if(before) (newTripAltList.size - 1) else (oldTripAltList.size)
//                searchResult.usedTripAlternatives[tripIndex] = TripAlternatives(
//                    currIndex = newIndex,
//                    alternatives = allTripAltList,
//                    count = allTripAltList.size
//                )
//                Log.i("DEBUG", "Changed index to $newIndex, new count: ${allTripAltList.size}")
//                //searchResult.usedTripsIndices[tripIndex] =
//                //    if (before) (alternatives.size - 1) else (existingTripAlternatives.size)
//
//                //if(before){
//                    //Log.i("DEBUG", "NEW ALT SIZE: ${searchResult.usedTripAlternativesLists[tripIndex].size}")
//                    //searchResult.expandedToPast = true
//                //} else{
//                    //Log.i("DEBUG", "NEW ALT SIZE: ${searchResult.usedTripAlternativesLists[tripIndex].size}")
//                    //searchResult.expandedToFuture = true
//                //}
//
//
//                //Log.i("DEBUG", "New alt count: ${searchResult.usedTripAlternativesLists[tripIndex].size}")
//            }
//
//            404, 502 -> {
//                Log.i(
//                    "ERROR",
//                    "Error while fetching alternatives"
//                )/* handle other status codes if needed */
//            }
//
//            else -> {
//                Log.i(
//                    "ERROR",
//                    "Error while fetching alternatives"
//                )/* handle other status codes if needed */
//            }
//        }
//    }
//
//    fun updateCurrIndex(result: ConnectionSearchResult, tripIndex: Int, newIndex: Int) {
//        // Find the matching result and update its tripAlternatives.currIndex
//        val resultIndex = searchResultList.value!!.indexOf(result)
//        if (resultIndex != -1) {
//            searchResultList.value!![resultIndex].usedTripAlternatives[tripIndex].currIndex = newIndex
//        }
//    }
//
//    fun startSearch(
//        context: Context,
//        navController: NavController,
//        showDialog: (Boolean) -> Unit,
//        setErrorMessage: (String) -> Unit
//    ) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                var startDateTime = LocalDateTime.now()
//                if (departureNow.value) {
//                    startDateTime = LocalDateTime.now()
//                    selectedDate.value = startDateTime.toLocalDate()
//                    selectedTime.value = startDateTime.toLocalTime()
//                } else {
//                    startDateTime = selectedDate.value.atTime(selectedTime.value)
//                }
//                //val startDateTime = if (departureNow.value) LocalDateTime.now() else selectedDate.value.atTime(selectedTime.value)
//                val response = sendRequest(startDateTime, 15)
//
//                when (response.statusCode) {
//                    200 -> {
//                        var connectionSearchResults =
//                            Json.decodeFromString<List<ConnectionSearchResult>>(response.text)
//                        processRawSearchResults(connectionSearchResults)
//                        connectionSearchResults = cleanUpDuplicates(connectionSearchResults)
//
//                        withContext(Dispatchers.Main) {
//                            searchResultList.value = connectionSearchResults
//                            navController.navigate("resultPage")
//
//                            searchRangeStart.value =
//                                selectedDate.value.atTime(selectedTime.value)
//                            searchRangeEnd.value =
//                                selectedDate.value.atTime(selectedTime.value).plusMinutes(15)
//                        }
//                    }
//
//                    404 -> {
//                        withContext(Dispatchers.Main) {
//                            setErrorMessage(context.getString(R.string.error_msg_404_connection))
//                            showDialog(true)
//                        }
//                    }
//
//                    502 -> {
//                        withContext(Dispatchers.Main) {
//                            setErrorMessage(context.getString(R.string.error_msg_502_connection))
//                            showDialog(true)
//                        }
//                    }
//
//                    else -> {
//                        Log.i("DEBUG", "Error while fetching alternatives")
//                        withContext(Dispatchers.Main) {
//                            setErrorMessage(response.text)
//                            showDialog(true)
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    setErrorMessage(context.getString(R.string.an_error_occurred) + ":" + e.message)
//                    showDialog(true)
//                }
//                e.printStackTrace()
//            }
//        }
//    }
//}
//
//    //val settingsFlow: Flow<Preferences> = preferencesDataStore.data
//
//
////    companion object {
////        private var INSTANCE: SharedViewModel? = null
////
////        fun getInstance(stopListDataStore: DataStore<StopList>, preferencesDataStore: DataStore<Preferences>): SharedViewModel {
////            if (INSTANCE == null) {
////                INSTANCE = SharedViewModel(stopListDataStore, preferencesDataStore)
////            }
////            return INSTANCE!!
////        }
////    }
//
//
//
