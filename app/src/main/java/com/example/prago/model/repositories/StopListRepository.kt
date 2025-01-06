package com.example.prago.model.repositories

import android.util.Log
import androidx.datastore.core.DataStore
import com.example.prago.StopList
import com.example.prago.model.dataClasses.stopList.StopListDataClass
import khttp.get
import khttp.responses.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.text.Normalizer
import java.time.LocalDateTime


const val STOP_LIST_URL = "https://data.pid.cz/stops/json/stops.json"

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


fun normalizeCzech(input: String): String {
    val normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
    return normalized.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "").lowercase()
}

class StopListRepository(
    private val stopListDataStore: DataStore<StopList>
) {
    val stopNameList: Flow<List<StopEntry>> = stopListDataStore.data.map { stopList ->
        stopList.stopGroupsList.map { stopGroup ->
            StopEntry(
                czechName = stopGroup.name,
                normalizedName = normalizeCzech(stopGroup.name),
                id = stopGroup.name + stopGroup.districtCode + stopGroup.cis
            )
        }
    }

    val generatedAt: Flow<LocalDateTime> = stopListDataStore.data
        .take(1)
        .map { value ->
            if(value.generatedAt == null || value.generatedAt == "") {
                return@map LocalDateTime.MIN
            }
            else{
                return@map LocalDateTime.parse(value.generatedAt)
            }
        }




    @OptIn(ExperimentalSerializationApi::class)
    suspend fun downloadAndStoreJson() {
        try {
            val response: Response = get(STOP_LIST_URL)
            val stopListJson = String(response.content, Charsets.UTF_8)
            val json = Json { ignoreUnknownKeys = true }
            val stopList = json.decodeFromString<StopListDataClass>(stopListJson)

            stopListDataStore.updateData { convertToStopList(stopList) }
        } catch (e: Exception) {
            Log.e("StopListRepository", "Error downloading or storing JSON", e)
        }
    }
}
