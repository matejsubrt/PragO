package com.example.prago.composables.resultScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.prago.R
import com.example.prago.dataClasses.UsedTransfer
import com.example.prago.formatters.formatDistance
import com.example.prago.formatters.formatTime

@Composable
fun UsedTransferCard(transfer: UsedTransfer) {
    Row(
        modifier = Modifier
            .background(Color(0xFF686868))
            .fillMaxWidth()
            .padding(start = 2.dp, top = 2.dp, end = 2.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width((tripIconSize.dp - transferIconSize.dp) / 2))
            Icon(
                painter = painterResource(id = R.drawable.walk),
                contentDescription = null,
                modifier = Modifier
                    .size(transferIconSize.dp)
                    .padding(all = 4.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.width((tripIconSize.dp - transferIconSize.dp) / 2))
            Text(
                text = "Transfer",
                style = TextStyle(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        Row(
            modifier = Modifier.padding(end = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0xFF888888), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp)
                    .width(distanceBoxWidth)
                    .height(boxHeight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = formatDistance(transfer.distance),
                    style = boxTextStyle,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(4.dp),
                    fontSize = boxTextSize
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .background(Color(0xFF888888), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp)
                    .width(timeBoxWidth)
                    .height(boxHeight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = formatTime(transfer.time.toLong()),
                    style = boxTextStyle,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(4.dp),
                    fontSize = boxTextSize
                )
            }

        }
    }
}