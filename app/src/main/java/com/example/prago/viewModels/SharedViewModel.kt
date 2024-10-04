package com.example.prago.viewModels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prago.dataClasses.ConnectionSearchResult
import com.example.prago.StopList
import com.example.prago.dataClasses.StopListDataClass
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.time.LocalDate
import java.time.LocalTime
import khttp.get
import khttp.responses.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.text.Normalizer


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


fun convertToStopList(stopListDataClass: StopListDataClass): StopList {
    val stopListBuilder = StopList.newBuilder()
        .setGeneratedAt(stopListDataClass.generatedAt)
        .setDataFormatVersion(stopListDataClass.dataFormatVersion)

    for (stopGroupDataClass in stopListDataClass.stopGroups) {
        val stopGroupBuilder = StopList.StopGroup.newBuilder()
            .setName(stopGroupDataClass.name)
            .setDistrictCode(stopGroupDataClass.districtCode)
            .setIdosCategory(stopGroupDataClass.idosCategory)
            .setIdosName(stopGroupDataClass.idosName)
            .setFullName(stopGroupDataClass.fullName)
            .setUniqueName(stopGroupDataClass.uniqueName)
            .setNode(stopGroupDataClass.node)
            .setCis(stopGroupDataClass.cis)
            .setAvgLat(stopGroupDataClass.avgLat)
            .setAvgLon(stopGroupDataClass.avgLon)
            .setAvgJtskX(stopGroupDataClass.avgJtskX)
            .setAvgJtskY(stopGroupDataClass.avgJtskY)
            .setMunicipality(stopGroupDataClass.municipality)

        for (stopDataClass in stopGroupDataClass.stops) {
            val stopBuilder = StopList.StopGroup.Stop.newBuilder()
                .setId(stopDataClass.id)
                .setPlatform(stopDataClass.platform ?: "")
                .setAltIdosName(stopDataClass.altIdosName)
                .setLat(stopDataClass.lat)
                .setLon(stopDataClass.lon)
                .setJtskX(stopDataClass.jtskX)
                .setJtskY(stopDataClass.jtskY)
                .setZone(stopDataClass.zone)
                .setWheelchairAccess(stopDataClass.wheelchairAccess)
                .addAllGtfsIds(stopDataClass.gtfsIds)

            for (lineDataClass in stopDataClass.lines) {
                val lineBuilder = StopList.StopGroup.Stop.Line.newBuilder()
                    .setId(lineDataClass.id)
                    .setName(lineDataClass.name)
                    .setType(lineDataClass.type)
                    .setDirection(lineDataClass.direction)
                    .setDirection2(lineDataClass.direction2 ?: "")

                stopBuilder.addLines(lineBuilder)
            }

            stopGroupBuilder.addStops(stopBuilder)
        }

        stopListBuilder.addStopGroups(stopGroupBuilder)
    }

    return stopListBuilder.build()
}

data class StopEntry(val czechName: String, val normalizedName: String, val id: String)

class SharedViewModel(val stopListDataStore: DataStore<StopList>,
                      val preferencesDataStore: DataStore<Preferences>) : ViewModel()
{
    private val _stopNameList = MutableStateFlow<List<StopEntry>>(emptyList())
    val stopNamesFlow: StateFlow<List<StopEntry>> = _stopNameList
    //val stopNamesFlow = flowOf(listOf(StopEntry("Chodov", "Chodov", "1"), StopEntry("Biskupcova", "Biskupcove", "2"), StopEntry("Větrník", "Vetrnik", "3")))
    var searchResult = MutableLiveData<ConnectionSearchResult>()


    var fromText = mutableStateOf("")
    var toText = mutableStateOf("")
    var fromSearchQuery by mutableStateOf("")
        private set

    var toSearchQuery by mutableStateOf("")
        private set


    var useSharedBikes = mutableStateOf(false)
    var transferBuffer = mutableStateOf(2f)
    var transferLength = mutableStateOf(1f)
    var comfortPreference = mutableStateOf(2f)
    var bikeTripBuffer = mutableStateOf(2f)
    var selectedDate = mutableStateOf(LocalDate.now())
    var selectedTime = mutableStateOf(LocalTime.now())
    var byEarliestDeparture = mutableStateOf(true)

    val walkingPace = MutableStateFlow<Int?>(null)
    val cyclingPace = MutableStateFlow<Int?>(null)
    val bikeUnlockTime = MutableStateFlow<Int?>(null)
    val bikeLockTime = MutableStateFlow<Int?>(null)


    init {
        viewModelScope.launch {
            transferBuffer.value = retrieveFloatSetting("transferBuffer", 2f)
            transferLength.value = retrieveFloatSetting("transferLength", 1f)
            comfortPreference.value = retrieveFloatSetting("comfortPreference", 2f)
            bikeTripBuffer.value = retrieveFloatSetting("bikeTripBuffer", 2f)
            useSharedBikes.value = retrieveBoolSetting("useSharedBikes", false)

            walkingPace.value = retrieveIntSetting("walkingPace", 12)
            cyclingPace.value = retrieveIntSetting("cyclingPace", 5)
            bikeUnlockTime.value = retrieveIntSetting("bikeUnlockTime", 30)
            bikeLockTime.value = retrieveIntSetting("bikeLockTime", 15)
        }
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("DEBUG", "Init started")
            stopListDataStore.data
                .map { stopList ->
                    // Map stop group names to list of StopEntry
                    val stopEntries = stopList.stopGroupsList.map { stopGroup ->
                        StopEntry(czechName = stopGroup.name, normalizedName = normalizeCzech(stopGroup.name), id = stopGroup.name + stopGroup.districtCode + stopGroup.cis)
                    }
                    stopEntries
                }
                .collect { value ->
                    withContext(Dispatchers.Main) {
                        _stopNameList.value = value
                    }
                }
            Log.i("DEBUG", "Init done")
        }
    }







    fun normalizeCzech(input: String): String {
        val normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
        return normalized.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
    }

    fun splitNormalizedWords(normalizedInput: String): List<String> {
        return normalizedInput.split("[\\s,-]+".toRegex()).map { it.trim() }
    }

    val fromSearchResults: StateFlow<List<StopEntry>> =
        snapshotFlow { fromSearchQuery }
            .combine(stopNamesFlow) { fromSearchQuery, stopNames ->
                val queryParts = fromSearchQuery.trim().split("\\s+".toRegex()).map { it.trim() }
                stopNames.filter { stopName ->
                    val nameParts = splitNormalizedWords(stopName.normalizedName)

                    // Ensure that each part of the query matches the beginning of corresponding words in the stop name
                    queryParts.all { queryPart ->
                        nameParts.any { namePart -> namePart.startsWith(queryPart, ignoreCase = true) }
                    }
                }
            }.stateIn(
                scope = viewModelScope,
                initialValue = emptyList(),
                started = SharingStarted.WhileSubscribed(5_000)
            )

    val toSearchResults: StateFlow<List<StopEntry>> =
        snapshotFlow { toSearchQuery }
            .combine(stopNamesFlow) { toSearchQuery, stopNames ->
                val queryParts = toSearchQuery.trim().split("\\s+".toRegex()).map { it.trim() }
                stopNames.filter { stopName ->
                    val nameParts = splitNormalizedWords(stopName.normalizedName)

                    queryParts.all { queryPart ->
                        nameParts.any { namePart -> namePart.startsWith(queryPart, ignoreCase = true) }
                    }
                }
            }.stateIn(
                scope = viewModelScope,
                initialValue = emptyList(),
                started = SharingStarted.WhileSubscribed(5_000)
            )
    fun onFromSearchQueryChange(newQuery: String) {
        fromSearchQuery = newQuery
    }
    fun onToSearchQueryChange(newQuery: String) {
        toSearchQuery = newQuery
    }

    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    suspend fun downloadAndStoreJson(url: String) {
        //Log.i("APP", "HERE")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.i("DEBUG", "Fetching response")
                val response: Response = get(url = url)
                Log.i("DEBUG", "Response fetched")
                val stopListJson = String(response.content, Charsets.UTF_8)
                val json = Json { ignoreUnknownKeys = true }
                val stopList = json.decodeFromString<StopListDataClass>(stopListJson)
                Log.i("DEBUG", "Storing response")
                // Store the StopList object in the Proto DataStore
                stopListDataStore.updateData { convertToStopList(stopList) }
                Log.i("DEBUG", "Response stored")
            } catch (e: Exception) {
                print(e)
            }

            //if (response.statusCode != 200) throw IOException("Unexpected code ${response.statusCode}")


        }
    }



    suspend fun saveBoolSetting(key: String, value: Boolean) {
        val dataStoreKey = stringPreferencesKey(key)
        preferencesDataStore.edit { preferences ->
            preferences[dataStoreKey] = value.toString()
        }
    }



    suspend fun retrieveBoolSetting(key: String, defaultValue: Boolean): Boolean {
        val dataStoreKey = stringPreferencesKey(key)
        val preferences = preferencesDataStore.data.first()
        return preferences[dataStoreKey]?.toBoolean() ?: defaultValue
    }

    suspend fun saveIntSetting(key: String, value: Int) {
        val dataStoreKey = intPreferencesKey(key)
        preferencesDataStore.edit { preferences ->
            preferences[dataStoreKey] = value
        }
    }

    suspend fun retrieveIntSetting(key: String, defaultValue: Int): Int {
        val dataStoreKey = intPreferencesKey(key)
        val preferences = preferencesDataStore.data.first()
        return preferences[dataStoreKey] ?: defaultValue
    }

    suspend fun saveFloatSetting(key: String, value: Float) {
        val dataStoreKey = floatPreferencesKey(key)
        preferencesDataStore.edit { preferences ->
            preferences[dataStoreKey] = value
        }
    }

    suspend fun retrieveFloatSetting(key: String, defaultValue: Float): Float {
        val dataStoreKey = floatPreferencesKey(key)
        val preferences = preferencesDataStore.data.first()
        return preferences[dataStoreKey] ?: defaultValue
    }


    //val settingsFlow: Flow<Preferences> = preferencesDataStore.data


    companion object {
        private var INSTANCE: SharedViewModel? = null

        fun getInstance(stopListDataStore: DataStore<StopList>, preferencesDataStore: DataStore<Preferences>): SharedViewModel {
            if (INSTANCE == null) {
                INSTANCE = SharedViewModel(stopListDataStore, preferencesDataStore)
            }
            return INSTANCE!!
        }
    }
}


