package com.example.prago.model.dataClasses.requests

import com.example.prago.utils.serialization.JsonName

data class AlternativeTripsRequest(
    @JsonName("srcStopId") val srcStopId: String,
    @JsonName("destStopId") val destStopId: String,
    @JsonName("dateTime") val dateTime: String,
    @JsonName("previous") val previous: Boolean,
    @JsonName("count") val count: Int,
    @JsonName("tripId") val tripId: String
)