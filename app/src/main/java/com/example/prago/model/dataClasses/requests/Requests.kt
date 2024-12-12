package com.example.prago.model.dataClasses.requests

import org.json.JSONObject

//@Target(AnnotationTarget.PROPERTY)
//annotation class JsonName(val name: String)
//
//
//
//
//
//
//
//
//
//
//fun Any.toJsonObject(): JSONObject {
//    val jsonObject = JSONObject()
//    this::class.java.declaredFields.forEach { field ->
//        field.isAccessible = true
//        val jsonName = field.getAnnotation(JsonName::class.java)?.name ?: field.name
//        val value = field.get(this)
//        if (jsonName != "\$stable") {
//            when (value) {
//                is Any -> {
//                    if (field.type.name == "java.lang.String" || field.type.isPrimitive || field.type.name.startsWith("java.")) {
//                        jsonObject.put(jsonName, value)
//                    } else {
//                        jsonObject.put(jsonName, value.toJsonObject())
//                    }
//                }
//                else -> jsonObject.put(jsonName, value)
//            }
//        }
//    }
//    return jsonObject
//}



