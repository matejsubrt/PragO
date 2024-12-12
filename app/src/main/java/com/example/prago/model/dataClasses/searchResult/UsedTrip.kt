package com.example.prago.model.dataClasses.searchResult

import androidx.compose.runtime.MutableState
import com.example.prago.utils.serialization.LocalDateTimeSerializer
import com.example.prago.utils.serialization.MutableStateSerializerBoolean
import com.example.prago.utils.serialization.MutableStateSerializerInt
import kotlinx.serialization.Serializable
import java.time.LocalDateTime


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