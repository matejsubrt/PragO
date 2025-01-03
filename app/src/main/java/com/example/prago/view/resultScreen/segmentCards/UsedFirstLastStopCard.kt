package com.example.prago.view.resultScreen.segmentCards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.prago.R
import com.example.prago.ui.theme.Gray96
import com.example.prago.view.resultScreen.transferIconSize
import com.example.prago.view.resultScreen.tripIconSize
import java.time.LocalDateTime

@Preview
@Composable
fun FirstLastStopCardPreview(){
    UsedFirstLastStopCard("Stop 1", LocalDateTime.now())
}


@Composable
fun UsedFirstLastStopCard(stopName: String, time: LocalDateTime){
    Row(
        modifier = Modifier
            .background(Gray96)
            .fillMaxWidth()
            .padding(start = 2.dp, top = 2.dp, end = 2.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width((tripIconSize.dp - transferIconSize.dp) / 2))
            Icon(
                painter = painterResource(id = R.drawable.location),
                contentDescription = null,
                modifier = Modifier
                    .size(transferIconSize.dp)
                    .padding(all = 4.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width((tripIconSize.dp - transferIconSize.dp) / 2))
            StopRow(
                stopName = stopName,
                time = time
            )
        }
    }
}