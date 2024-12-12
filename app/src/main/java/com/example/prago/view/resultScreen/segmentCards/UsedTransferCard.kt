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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.prago.R
import com.example.prago.model.dataClasses.searchResult.UsedTransfer
import com.example.prago.view.resultScreen.distanceTimeBoxSpacerWidth
import com.example.prago.view.resultScreen.transferIconSize
import com.example.prago.view.resultScreen.tripIconSize

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
                text = stringResource(R.string.transfer),
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
            LengthBox(showsTime = false, length = transfer.distance)
            Spacer(modifier = Modifier.width(distanceTimeBoxSpacerWidth))
            LengthBox(showsTime = true, length = transfer.time)
        }
    }
}