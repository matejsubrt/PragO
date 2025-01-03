package com.example.prago.view.resultScreen.segmentCards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.prago.ui.theme.Gray144
import com.example.prago.utils.formatting.formatDistance
import com.example.prago.utils.formatting.formatTime
import com.example.prago.view.resultScreen.boxHeight
import com.example.prago.view.resultScreen.boxTextSize
import com.example.prago.view.resultScreen.boxTextStyle
import com.example.prago.view.resultScreen.distanceBoxWidth
import com.example.prago.view.resultScreen.timeBoxWidth

@Composable
fun LengthBox(
    showsTime: Boolean,
    length: Int
){
    val formattedText = if (showsTime) formatTime(length.toLong()) else formatDistance(length)
    val width = if (showsTime) timeBoxWidth else distanceBoxWidth

    Box(
        modifier = Modifier
            .background(Gray144, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp)
            .width(width)
            .height(boxHeight),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = formattedText,
            style = boxTextStyle,
            modifier = Modifier.padding(4.dp),
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = boxTextSize
        )
    }
}