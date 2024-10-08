package com.example.prago.composables.resultScreen

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

const val lineNameSize = 17
const val stopNameSize = 14
const val tripIconSize = 42
const val transferIconSize = 32
const val transferTextSize = 14
const val headerTextSize = 16


val stopNameStyle = TextStyle(
    color = Color.Black,
    fontSize = stopNameSize.sp,
    fontWeight = FontWeight.Medium
)


val colorNextbike = Color(0, 0, 128)

val distanceBoxWidth = 46.dp
val timeBoxWidth = 60.dp
val boxHeight = 24.dp
val boxTextSize = 12.sp

val boxTextStyle = TextStyle(
    fontSize = boxTextSize,
    fontWeight = FontWeight.Medium
)