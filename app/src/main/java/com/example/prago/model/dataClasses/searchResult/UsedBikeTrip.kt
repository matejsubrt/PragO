package com.example.prago.model.dataClasses.searchResult

import kotlinx.serialization.Serializable

@Serializable
data class UsedBikeTrip(
    val srcStopInfo: StopInfo,
    val destStopInfo: StopInfo,
    val distance: Int,
    val time: Int,
    val remainingBikes: Int
)