package com.example.prago.dataClasses

import kotlinx.serialization.Serializable

@Serializable
data class Line(
    val id: Int,
    val name: String,
    val type: String,
    val direction: String,
    val direction2: String? = ""
)

@Serializable
data class Stop(
    val id: String,
    val platform: String? = "",
    val altIdosName: String,
    val lat: Double,
    val lon: Double,
    val jtskX: Double,
    val jtskY: Double,
    val zone: String,
    val wheelchairAccess: String,
    val gtfsIds: List<String>,
    val lines: List<Line>
)

@Serializable
data class StopGroup(
    val name: String,
    val districtCode: String,
    val isTrain: Boolean = false,
    val idosCategory: Int,
    val idosName: String,
    val fullName: String,
    val uniqueName: String,
    val node: Int,
    val cis: Int,
    val avgLat: Double,
    val avgLon: Double,
    val avgJtskX: Double,
    val avgJtskY: Double,
    val municipality: String,
    val stops: List<Stop>
)

@Serializable
data class StopListDataClass(
    val generatedAt: String,
    val dataFormatVersion: String,
    val stopGroups: List<StopGroup>
)