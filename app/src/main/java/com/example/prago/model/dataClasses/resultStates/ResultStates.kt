package com.example.prago.model.dataClasses.resultStates

import com.example.prago.model.dataClasses.searchResult.ConnectionSearchResult
import com.example.prago.model.dataClasses.searchResult.DelayData
import com.example.prago.model.dataClasses.searchResult.UsedTrip

sealed class ConnectionSearchResultState {
    data class Success(val results: List<ConnectionSearchResult>) : ConnectionSearchResultState()
    data class Failure(val statusCode: Int, val errorMessage: String) : ConnectionSearchResultState()
}

sealed class AlternativeTripsResultState {
    data class Success(val results: List<UsedTrip>) : AlternativeTripsResultState()
    data class Failure(val statusCode: Int, val errorMessage: String) : AlternativeTripsResultState()
}

sealed class DelayUpdateResultState {
    data class Success(val updatedDelays: Map<String, DelayData>) : DelayUpdateResultState()
    data class Failure(val statusCode: Int, val errorMessage: String) : DelayUpdateResultState()
}