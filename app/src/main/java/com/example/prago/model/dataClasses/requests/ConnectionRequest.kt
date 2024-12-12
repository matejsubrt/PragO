package com.example.prago.model.dataClasses.requests

import com.example.prago.utils.serialization.JsonName

data class ConnectionRequest(
    @JsonName("srcStopName") val srcStopName: String,
    @JsonName("srcLat") val srcLat: Double,
    @JsonName("srcLon") val srcLon: Double,

    @JsonName("destStopName") val destStopName: String,
    @JsonName("destLat") val destLat: Double,
    @JsonName("destLon") val destLon: Double,

    @JsonName("dateTime") val dateTime: String,
    @JsonName("rangeLength") val rangeLength: Int,

    @JsonName("byEarliestDeparture") val byEarliestDeparture: Boolean,
    @JsonName("range") val range: Boolean,
    @JsonName("srcByCoords") val srcByCoords: Boolean,
    @JsonName("destByCoords") val destByCoords: Boolean,

    @JsonName("settings") val settings: SearchSettings
)



fun createStopToStopRangeRequest(
    srcStopName: String,
    destStopName: String,
    dateTime: String,
    byEarliestDeparture: Boolean,
    settings: SearchSettings,
    rangeLength: Int
): ConnectionRequest {
    return ConnectionRequest(
        srcStopName = srcStopName,
        srcLat = 0.0,
        srcLon = 0.0,
        destStopName = destStopName,
        destLat = 0.0,
        destLon = 0.0,
        dateTime = dateTime,
        rangeLength = rangeLength,
        byEarliestDeparture = byEarliestDeparture,
        range = true,
        srcByCoords = false,
        destByCoords = false,
        settings = settings
    )
}

fun createCoordsToStopRangeRequest(
    srcLat: Double,
    srcLon: Double,
    destStopName: String,
    dateTime: String,
    byEarliestDeparture: Boolean,
    settings: SearchSettings,
    rangeLength: Int
): ConnectionRequest {
    return ConnectionRequest(
        srcStopName = "",
        srcLat = srcLat,
        srcLon = srcLon,
        destStopName = destStopName,
        destLat = 0.0,
        destLon = 0.0,
        dateTime = dateTime,
        rangeLength = rangeLength,
        byEarliestDeparture = byEarliestDeparture,
        range = true,
        srcByCoords = true,
        destByCoords = false,
        settings = settings
    )
}