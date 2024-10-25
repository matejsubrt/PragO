@file:OptIn(ExperimentalSerializationApi::class)

package com.example.prago.dataClasses

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = LocalDateTime::class)
object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("LocalDateTime") {
        element<String>("value")
    }

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.format(formatter))
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString(), formatter)
    }
}




@Serializable
data class UsedTrip(
    val getOnStopIndex: Int,
    val getOffStopIndex: Int,
    val routeName: String,
    val color: ColorStruct,
    val stopPasses: List<StopPass>,
    val vehicleType: Int,
    val hasDelayInfo: Boolean,
    val delayWhenBoarded: Int,
    val currentDelay: Int,
    val tripId: String
)

@Serializable
data class UsedBikeTrip(
    val srcStopInfo: StopInfo,
    val destStopInfo: StopInfo,
    val distance: Int,
    val time: Int,
    val remainingBikes: Int
)

@Serializable
data class ColorStruct(
    val r: Int,
    val g: Int,
    val b: Int
)

@Serializable
data class StopPass(
    val name: String,
    val id: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val arrivalTime: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val departureTime: LocalDateTime
)

@Serializable
data class StopInfo(
    val name: String,
    val id: String,
    val lat: Double,
    val lon: Double
)

@Serializable
data class UsedTransfer(
    val srcStopInfo: StopInfo,
    val destStopInfo: StopInfo,
    val time: Int,
    val distance: Int
)


@Serializable
data class ConnectionSearchResult(
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
    val arrivalDateTime: LocalDateTime
)


fun main() {
    val jsonString = """/* Your JSON String here */"""
    val connectionSearchResult = Json.decodeFromString<ConnectionSearchResult>(jsonString)
    println(connectionSearchResult)
}
