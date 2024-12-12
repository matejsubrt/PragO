package com.example.prago.utils.serialization

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.example.prago.StopList
import com.example.prago.model.dataClasses.searchResult.TripAlternatives
import com.example.prago.model.dataClasses.searchResult.UsedTrip
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import org.json.JSONObject
import java.io.InputStream
import java.io.OutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
                            UsedTrip.serializer())
                    )
                    2 -> count = decodeIntElement(descriptor, 2)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> throw SerializationException("Unexpected index: $index")
                }
            }

            TripAlternatives(currIndex, alternatives, count)
        }
    }
}



object SnapshotStateListSerializer : KSerializer<SnapshotStateList<TripAlternatives>> {
    override val descriptor: SerialDescriptor =
        ListSerializer(TripAlternatives.serializer()).descriptor

    override fun serialize(encoder: Encoder, value: SnapshotStateList<TripAlternatives>) {
        val actualList = value.toList()
        encoder.encodeSerializableValue(ListSerializer(TripAlternatives.serializer()), actualList)
    }

    override fun deserialize(decoder: Decoder): SnapshotStateList<TripAlternatives> {
        val list = decoder.decodeSerializableValue(ListSerializer(TripAlternatives.serializer()))
        return list.toMutableStateList()
    }
}


object StopListSerializer : Serializer<StopList> {
    override val defaultValue: StopList = StopList.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): StopList = withContext(Dispatchers.IO) {
        try {
            return@withContext StopList.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: StopList, output: OutputStream) = withContext(Dispatchers.IO) {t.writeTo(output)}
}





@Target(AnnotationTarget.PROPERTY)
annotation class JsonName(val name: String)

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