package com.example.prago.dataClasses

import org.json.JSONObject

@Target(AnnotationTarget.PROPERTY)
annotation class JsonName(val name: String)

data class SearchSettings(
    @JsonName("WalkingPace") val walkingPace: Int,
    @JsonName("CyclingPace") val cyclingPace: Int,
    @JsonName("BikeUnlockTime") val bikeUnlockTime: Int,
    @JsonName("BikeLockTime") val bikeLockTime: Int,
    @JsonName("UseSharedBikes") val useSharedBikes: Boolean,
    @JsonName("BikeMax15Minutes") val bikeMax15Minutes: Boolean,
    @JsonName("TransferTime") val transferTime: Int,
    @JsonName("ComfortBalance") val comfortBalance: Int,
    @JsonName("WalkingPreference") val walkingPreference: Int,
    @JsonName("BikeTripBuffer") val bikeTripBuffer: Int
)

//data class StopToStopRequest(
//    @JsonName("srcStopName") val srcStopName: String,
//    @JsonName("destStopName") val destStopName: String,
//    @JsonName("dateTime") val dateTime: String,
//    @JsonName("byEarliestDeparture") val byEarliestDeparture: Boolean,
//    @JsonName("settings") val settings: SearchSettings
//)
//
//data class CoordToCoordRequest(
//    @JsonName("srcLon") val srcLon: Double,
//    @JsonName("srcLat") val srcLat: Double,
//    @JsonName("destLon") val destLon: Double,
//    @JsonName("destLat") val destLat: Double,
//    @JsonName("dateTime") val dateTime: String,
//    @JsonName("byEarliestDeparture") val byEarliestDeparture: Boolean,
//    @JsonName("settings") val settings: SearchSettings
//)

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

fun CreateStopToStopRangeRequest(
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

fun Any.toJsonObject(): JSONObject {
    val jsonObject = JSONObject()
    this::class.java.declaredFields.forEach { field ->
        field.isAccessible = true
        val jsonName = field.getAnnotation(JsonName::class.java)?.name ?: field.name
        val value = field.get(this)
        if (jsonName != "\$stable") {
            when (value) {
                is Any -> {
                    if (field.type.name == "java.lang.String" || field.type.isPrimitive || field.type.name.startsWith("java.")) {
                        jsonObject.put(jsonName, value)
                    } else {
                        jsonObject.put(jsonName, value.toJsonObject())
                    }
                }
                else -> jsonObject.put(jsonName, value)
            }
        }
    }
    return jsonObject
}



