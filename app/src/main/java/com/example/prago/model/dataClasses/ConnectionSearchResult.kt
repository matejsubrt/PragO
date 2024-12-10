@file:OptIn(ExperimentalSerializationApi::class)

package com.example.prago.model.dataClasses

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.format.DateTimeFormatter
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.*

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

object MutableStateSerializerInt : KSerializer<MutableState<Int>> {
    override val descriptor = PrimitiveSerialDescriptor("MutableState", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: MutableState<Int>) {
        encoder.encodeInt(value.value)
    }

    override fun deserialize(decoder: Decoder): MutableState<Int> {
        return mutableStateOf(decoder.decodeInt())
    }
}

object MutableStateSerializerBoolean : KSerializer<MutableState<Boolean>> {
    override val descriptor = PrimitiveSerialDescriptor("MutableState", PrimitiveKind.BOOLEAN)

    override fun serialize(encoder: Encoder, value: MutableState<Boolean>) {
        encoder.encodeBoolean(value.value)
    }

    override fun deserialize(decoder: Decoder): MutableState<Boolean> {
        return mutableStateOf(decoder.decodeBoolean())
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
    @Serializable(with = MutableStateSerializerBoolean::class)
    val hasDelayInfo: MutableState<Boolean>,
    @Serializable(with = MutableStateSerializerInt::class)
    var delayWhenBoarded: MutableState<Int>,
    @Serializable(with = MutableStateSerializerInt::class)
    var currentDelay: MutableState<Int>,
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


//@Serializable
//data class ConnectionSearchResult(
//    val usedTripsIndices: List<Int> = emptyList(),
//    val usedTripsWithAlternatives: List<List<UsedTrip>> = emptyList(),
//    val usedTrips: List<UsedTrip>,
//    val usedTransfers: List<UsedTransfer>,
//    val usedBikeTrips: List<UsedBikeTrip>,
//    val usedSegmentTypes: List<Int>,
//    val transferCount: Int,
//    val tripCount: Int,
//    val bikeTripCount: Int,
//    @Serializable(with = LocalDateTimeSerializer::class)
//    val departureDateTime: LocalDateTime,
//    @Serializable(with = LocalDateTimeSerializer::class)
//    val arrivalDateTime: LocalDateTime
//)



@Serializable(with = TripAlternativesSerializer::class)
class TripAlternatives(
    var currIndex: Int = 0,
    val alternatives: List<UsedTrip> = emptyList(),
    val count: Int
)

object TripAlternativesSerializer : KSerializer<TripAlternatives> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("TripAlternatives") {
        element<Int>("currIndex")
        element<List<UsedTrip>>("alternatives")
        element<Int>("count")
    }

    override fun serialize(encoder: Encoder, value: TripAlternatives) {
        encoder.encodeStructure(descriptor) {
            encodeIntElement(descriptor, 0, value.currIndex)
            encodeSerializableElement(descriptor, 1, ListSerializer(UsedTrip.serializer()), value.alternatives)
            encodeIntElement(descriptor, 2, value.count)
        }
    }

    override fun deserialize(decoder: Decoder): TripAlternatives {
        return decoder.decodeStructure(descriptor) {
            var currIndex = 0
            var alternatives: List<UsedTrip> = emptyList()
            var count = 0

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> currIndex = decodeIntElement(descriptor, 0)
                    1 -> alternatives = decodeSerializableElement(
                        descriptor, 1, ListSerializer(
                            UsedTrip.serializer()))
                    2 -> count = decodeIntElement(descriptor, 2)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> throw SerializationException("Unexpected index: $index")
                }
            }

            TripAlternatives(currIndex, alternatives, count)
        }
    }
}

@Serializable
data class DelayData(
    val hasDelayData: Boolean,
    val delayWhenBoarded: Int,
    val currentDelay: Int
)

//@Serializable
//class ConnectionSearchResultRaw(
//    var usedTripAlternatives: List<TripAlternatives>,
//    val usedTrips: List<UsedTrip>,
//    val usedTransfers: List<UsedTransfer>,
//    val usedBikeTrips: List<UsedBikeTrip>,
//    val usedSegmentTypes: List<Int>,
//    val transferCount: Int,
//    val tripCount: Int,
//    val bikeTripCount: Int,
//    @Serializable(with = LocalDateTimeSerializer::class)
//    val departureDateTime: LocalDateTime,
//    @Serializable(with = LocalDateTimeSerializer::class)
//    val arrivalDateTime: LocalDateTime,
//
//    val secondsAfterLastTrip: Int,
//    val secondsBeforeFirstTrip: Int
//)


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


// Custom Serializer for SnapshotStateList
object SnapshotStateListSerializer : KSerializer<SnapshotStateList<TripAlternatives>> {
    override val descriptor: SerialDescriptor =
        ListSerializer(TripAlternatives.serializer()).descriptor

    override fun serialize(encoder: Encoder, value: SnapshotStateList<TripAlternatives>) {
        Log.i("SnapshotStateListSerializer", "serialize")
        val actualList = value.toList()
        encoder.encodeSerializableValue(ListSerializer(TripAlternatives.serializer()), actualList)



    //encoder.encodeSerializableValue(ListSerializer(TripAlternatives.serializer()), value.toList())
    }

    override fun deserialize(decoder: Decoder): SnapshotStateList<TripAlternatives> {
        Log.i("SnapshotStateListSerializer", "deserialize")
        val list = decoder.decodeSerializableValue(ListSerializer(TripAlternatives.serializer()))
        return list.toMutableStateList()
    }
}



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

//fun main() {
//    val jsonString = """/* Your JSON String here */"""
//    val connectionSearchResult = Json.decodeFromString<ConnectionSearchResult>(jsonString)
//    println(connectionSearchResult)
//}
