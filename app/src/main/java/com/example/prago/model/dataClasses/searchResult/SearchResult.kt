package com.example.prago.model.dataClasses.searchResult

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.prago.utils.serialization.LocalDateTimeSerializer
import com.example.prago.utils.serialization.SnapshotStateListSerializer
import com.example.prago.utils.serialization.TripAlternativesSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable(with = TripAlternativesSerializer::class)
class TripAlternatives(
    var currIndex: Int = 0,
    val alternatives: List<UsedTrip> = emptyList(),
    val count: Int
)



@Serializable
data class DelayData(
    val hasDelayData: Boolean,
    val delayWhenBoarded: Int,
    val currentDelay: Int
)

@Serializable
data class ConnectionSearchResult(
    @Serializable(with = SnapshotStateListSerializer::class)
    val usedTripAlternatives: SnapshotStateList<TripAlternatives> = mutableStateListOf(),
    val usedTrips: List<UsedTrip>,
    val usedTransfers: List<UsedTransfer>,
    val usedBikeTrips: List<UsedBikeTrip>,
    val usedSegmentTypes: List<Int>,
    val transferCount: Int,
    val tripCount: Int,
    val bikeTripCount: Int,
    @Serializable(with = LocalDateTimeSerializer::class)
    val departureDateTime: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val arrivalDateTime: LocalDateTime,

    val secondsAfterLastTrip: Int,
    val secondsBeforeFirstTrip: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ConnectionSearchResult) return false


        return departureDateTime == other.departureDateTime &&
                arrivalDateTime == other.arrivalDateTime &&
                usedSegmentTypes == other.usedSegmentTypes // not ideal, but should be enough for our use case (can theoretically lead to false positives if 2 connections have same departure and arrival time, but use different trips)
    }

    override fun hashCode(): Int {
        var result = usedTripAlternatives.hashCode()
        result = 31 * result + usedTrips.hashCode()
        result = 31 * result + usedTransfers.hashCode()
        result = 31 * result + usedBikeTrips.hashCode()
        result = 31 * result + usedSegmentTypes.hashCode()
        result = 31 * result + transferCount
        result = 31 * result + tripCount
        result = 31 * result + bikeTripCount
        result = 31 * result + departureDateTime.hashCode()
        result = 31 * result + arrivalDateTime.hashCode()
        return result
    }
}