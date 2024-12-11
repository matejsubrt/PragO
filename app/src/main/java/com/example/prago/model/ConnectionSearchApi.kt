package com.example.prago.model

import android.content.Context
import android.util.Log
import com.example.prago.R
import com.example.prago.model.dataClasses.AlternativeTripsRequest
import com.example.prago.model.dataClasses.AlternativeTripsResultState
import com.example.prago.model.dataClasses.ConnectionRequest
import com.example.prago.model.dataClasses.ConnectionSearchResult
import com.example.prago.model.dataClasses.ConnectionSearchResultState
import com.example.prago.model.dataClasses.UsedTrip
import com.example.prago.model.dataClasses.toJsonObject
import khttp.responses.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val BASE_URL = "http://prago.xyz"
const val CONNECTION_SEARCH_ENDPOINT = "/connection"
const val ALTERNATIVE_TRIPS_ENDPOINT = "/alternative-trips"
const val DELAY_UPDATE_ENDPOINT = "/update-delays"


class ConnectionSearchApi {
    private suspend fun sendConnectionRequest(request: ConnectionRequest): Response {
        return khttp.post(
            url = BASE_URL + CONNECTION_SEARCH_ENDPOINT,
            json = request.toJsonObject()
        )
    }

    private suspend fun sendAlternativeTripsRequest(request: AlternativeTripsRequest): Response {
        return khttp.post(
            url = BASE_URL + ALTERNATIVE_TRIPS_ENDPOINT,
            json = request.toJsonObject()
        )
    }

    @OptIn(ExperimentalSerializationApi::class)
    private suspend fun sendDelayUpdateRequest(request: List<ConnectionSearchResult>): Response {
        return khttp.post(
            url =  BASE_URL + DELAY_UPDATE_ENDPOINT,
            data = Json.encodeToString(request),
            headers = mapOf("Content-Type" to "application/json")
        )
    }


    private fun cleanUpDuplicates(searchResults: List<ConnectionSearchResult>): List<ConnectionSearchResult> {
        val orderedSearchResults = searchResults.sortedBy { it.departureDateTime }
        val cleanedResults = mutableListOf<ConnectionSearchResult>()

        // Only add the first occurrence of each unique connection (consecutive)
        for (result in orderedSearchResults) {
            if(cleanedResults.isEmpty()){
                cleanedResults.add(result)
                continue
            } else {
                val lastResult = cleanedResults.last()
                val sameDepartureTime = result.departureDateTime == lastResult.departureDateTime
                val sameArrivalTime = result.arrivalDateTime == lastResult.arrivalDateTime
                val sameSegmentTypes = result.usedSegmentTypes == lastResult.usedSegmentTypes
                val onlyBikeAndTransfers = result.usedSegmentTypes.all { it == 0 || it == 2 }
                val bothOnlyBikeAndTransfers = sameSegmentTypes && onlyBikeAndTransfers

                if(!bothOnlyBikeAndTransfers && (!sameArrivalTime || !sameDepartureTime)){
                    cleanedResults.add(result)
                    continue
                }
            }
        }

        return cleanedResults
    }

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun searchForConnection(request: ConnectionRequest, context: Context): Flow<ConnectionSearchResultState> = flow {
        val response = sendConnectionRequest(request)

        when (response.statusCode) {
            200 -> {
                var connectionSearchResults = Json.decodeFromString<List<ConnectionSearchResult>>(response.text)
                connectionSearchResults = cleanUpDuplicates(connectionSearchResults)

                val resultState = ConnectionSearchResultState.Success(connectionSearchResults)
                emit(resultState)
            }
            404 -> {
                val errorMessage = context.getString(R.string.error_msg_404_connection)

                val resultState = ConnectionSearchResultState.Failure(404, errorMessage)
                emit(resultState)
            }
            502 -> {
                val errorMessage = context.getString(R.string.error_msg_502_connection)
                val resultState = ConnectionSearchResultState.Failure(502, errorMessage)
                emit(resultState)
            }
            else -> {
                val errorMessage = "Error: ${response.statusCode} - ${response.text}"
                val resultState = ConnectionSearchResultState.Failure(response.statusCode, errorMessage)
                emit(resultState)
            }
        }
    }.catch { e ->
        val errorMessage = "An error occurred: ${e.message}"
        emit(ConnectionSearchResultState.Failure(-1, errorMessage))
    }






    suspend fun getAlternativeTrips(request: AlternativeTripsRequest, context: Context): Flow<AlternativeTripsResultState> = flow {
        val response = sendAlternativeTripsRequest(request)

        when (response.statusCode) {
            200 -> {
                val usedTrips = Json.decodeFromString<List<UsedTrip>>(response.text)
                emit(AlternativeTripsResultState.Success(usedTrips))
            }
            404 -> {
                val errorMessage = context.getString(R.string.error_msg_404_alternatives)
                emit(AlternativeTripsResultState.Failure(404, errorMessage))
            }
            502 -> {
                val errorMessage = context.getString(R.string.error_msg_502_alternatives)
                emit(AlternativeTripsResultState.Failure(502, errorMessage))
            }
            else -> {
                val errorMessage = "Error: ${response.statusCode} - ${response.text}"
                emit(AlternativeTripsResultState.Failure(response.statusCode, errorMessage))
            }
        }
    }.catch { e ->
        val errorMessage = "An error occurred: ${e.message}"
        emit(AlternativeTripsResultState.Failure(-1, errorMessage))
    }



    suspend fun updateDelayData(results: List<ConnectionSearchResult>, context: Context): Flow<ConnectionSearchResultState> = flow {
        val response = sendDelayUpdateRequest(results)

        when (response.statusCode) {
            200 -> {
                val updatedResults = Json.decodeFromString<List<ConnectionSearchResult>>(response.text)
                emit(ConnectionSearchResultState.Success(updatedResults))
            }
            else -> {
                val errorMessage = "Error: ${response.statusCode} - ${response.text}"
                emit(ConnectionSearchResultState.Failure(response.statusCode, errorMessage))
            }
        }
    }//.catch { e ->
//        Log.i("DEBUG", "ERROR: ${e.message}")
//        val errorMessage = "An error occurred: ${e.message}"
//        emit(ConnectionSearchResultState.Failure(-1, errorMessage))
//    }
}

