package com.example.prago.model.dataClasses.searchResult

import kotlinx.serialization.Serializable

@Serializable
data class StopInfo(
    val name: String,
    val id: String,
    val lat: Double,
    val lon: Double
)