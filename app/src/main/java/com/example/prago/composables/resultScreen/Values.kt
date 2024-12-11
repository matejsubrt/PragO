package com.example.prago.composables.resultScreen

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

const val lineNameSize = 17
const val stopNameSize = 14
const val delayTextSize = 13
const val tripIconSize = 42
const val transferIconSize = 32
const val transferTextSize = 14
const val headerTextSize = 16
const val headerTextSizeSmaller = 10


val stopNameStyle = TextStyle(
    color = Color.Black,
    fontSize = stopNameSize.sp,
    fontWeight = FontWeight.Medium
)

val delayTextStyle = TextStyle(
    color = Color.Black,
    fontSize = delayTextSize.sp,
    fontWeight = FontWeight.Bold
)


val colorNextbike = Color(0, 0, 128)

val distanceBoxWidth = 52.dp
val timeBoxWidth = 60.dp
val distanceTimeBoxSpacerWidth = 8.dp
val boxHeight = 24.dp
val boxTextSize = 12.sp

val boxTextStyle = TextStyle(
    fontSize = boxTextSize,
    fontWeight = FontWeight.Medium
)