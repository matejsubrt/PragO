package com.example.prago.model.dataClasses.searchResult

import kotlinx.serialization.Serializable

@Serializable
data class UsedTransfer(
    val srcStopInfo: StopInfo,
    val destStopInfo: StopInfo,
    val time: Int,
    val distance: Int
)