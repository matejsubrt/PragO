package com.example.prago.composables.resultScreen

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prago.R
import com.example.prago.model.dataClasses.UsedBikeTrip
import com.example.prago.formatters.formatDistance
import com.example.prago.formatters.formatTime

fun GetMapyCzUrl(bikeTrip: UsedBikeTrip): String{
    return "https:://mapy.cz/fnc/v1/route?mapset=outdoor&start=${bikeTrip.srcStopInfo.lon},${bikeTrip.srcStopInfo.lat}&end=${bikeTrip.destStopInfo.lon},${bikeTrip.destStopInfo.lat}&routeType=bike_road"
}
@Composable
fun UsedBikeTripCard(bikeTrip: UsedBikeTrip) {
    val context = LocalContext.current
    val mapyCzUrl = GetMapyCzUrl(bikeTrip)

    val intent = remember { Intent(Intent.ACTION_VIEW, Uri.parse(mapyCzUrl)) }

    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .fillMaxWidth()
            .padding(start = 2.dp, top = 0.dp, end = 2.dp, bottom = 4.dp)
            .clickable {
                Log.i("DEBUG", "Opening URL: $mapyCzUrl with Mapy.cz")
                context.startActivity(intent)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.bike),
            contentDescription = null,
            modifier = Modifier
                .size(tripIconSize.dp)
                .padding(all = 4.dp),
            tint = MaterialTheme.colorScheme.onTertiaryContainer
        )
        Column(
            modifier = Modifier.padding(start = 4.dp, top = 2.dp, end = 8.dp, bottom = 4.dp)
        ) {
            TripNameRow(stringResource(R.string.nextbike), colorNextbike, false, 0, hasBikeCountData = true, bikeCount = bikeTrip.remainingBikes)
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ){
//                TripNameRow(stringResource(R.string.nextbike), colorNextbike, false, 0, hasBikeCountData = true, bikeCount = bikeTrip.remainingBikes)
                //Text(text = "BAF", fontSize = 15.sp)
//                Box(
//                    modifier = Modifier
//                        .background(Color(0xFF888888), RoundedCornerShape(16.dp))
//                        .padding(horizontal = 2.dp),
//                    contentAlignment = Alignment.Center
//                ){
//                    Row(
//                        horizontalArrangement = Arrangement.End, // Align items to the end (right) of the row
//                        verticalAlignment = Alignment.CenterVertically
//                    ){
//                        Icon(
//                            painter = painterResource(id = R.drawable.bike),
//                            contentDescription = null,
//                            tint = colorNextbike,
//                            modifier = Modifier
//                                .size(24.dp)
//                                .padding(all = 4.dp)
//                        )
//                        Text(
//                            text = "${bikeTrip.remainingBikes}",
//                            color = colorNextbike,
//                            fontSize = 15.sp
//                        )
//                        Log.i("DEBUG", "Remaining bikes: ${bikeTrip.remainingBikes}")
//                    }
//                }
//            }


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Column(){
                    Text(
                        text = bikeTrip.srcStopInfo.name,
                        style = stopNameStyle,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = bikeTrip.destStopInfo.name,
                        style = stopNameStyle,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }

            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ){
                Box(
                    modifier = Modifier
                        .background(Color(0xFF888888), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp)
                        .width(distanceBoxWidth)
                        .height(boxHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = formatDistance(bikeTrip.distance),
                        style = boxTextStyle,
                        modifier = Modifier.padding(4.dp),
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        textAlign = TextAlign.Center,
                        fontSize = boxTextSize
                    )
                }
                Spacer(modifier = Modifier.width(distanceTimeBoxSpacerWidth))
                Box(
                    modifier = Modifier
                        .background(Color(0xFF888888), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp)
                        .width(timeBoxWidth)
                        .height(boxHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = formatTime(bikeTrip.time.toLong()),
                        style = boxTextStyle,
                        modifier = Modifier.padding(4.dp),
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        textAlign = TextAlign.Center,
                        fontSize = boxTextSize
                    )
                }
            }
        }
    }
}