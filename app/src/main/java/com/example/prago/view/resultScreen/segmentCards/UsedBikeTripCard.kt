package com.example.prago.view.resultScreen.segmentCards

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.prago.R
import com.example.prago.model.dataClasses.searchResult.UsedBikeTrip
import com.example.prago.ui.theme.Gray96
import com.example.prago.view.resultScreen.distanceTimeBoxSpacerWidth
import com.example.prago.view.resultScreen.stopNameStyle
import com.example.prago.view.resultScreen.tripIconSize


// URL to launch Mapy.cz with the bike trip route
fun getMapyCzUrl(bikeTrip: UsedBikeTrip): String{
    return "https:://mapy.cz/fnc/v1/route?mapset=outdoor&start=${bikeTrip.srcStopInfo.lon},${bikeTrip.srcStopInfo.lat}&end=${bikeTrip.destStopInfo.lon},${bikeTrip.destStopInfo.lat}&routeType=bike_road"
}
@Composable
fun UsedBikeTripCard(bikeTrip: UsedBikeTrip) {
    val context = LocalContext.current
    val mapyCzUrl = getMapyCzUrl(bikeTrip)

    val intent = remember { Intent(Intent.ACTION_VIEW, Uri.parse(mapyCzUrl)) }

    Row(
        modifier = Modifier
            .background(Gray96)
            .fillMaxWidth()
            .padding(start = 2.dp, top = 0.dp, end = 2.dp, bottom = 4.dp)
            .clickable {
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
            tint = Color.White
        )

        Column(
            modifier = Modifier.padding(start = 4.dp, top = 2.dp, end = 8.dp, bottom = 4.dp)
        ) {
            BikeServiceRow(bikeTrip)


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Column(){
                    Text(
                        text = bikeTrip.srcStopInfo.name,
                        style = stopNameStyle,
                        color = Color.White
                    )
                    Text(
                        text = bikeTrip.destStopInfo.name,
                        style = stopNameStyle,
                        color = Color.White
                    )
                }

            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ){
                LengthBox(false, bikeTrip.distance)
                Spacer(modifier = Modifier.width(distanceTimeBoxSpacerWidth))
                LengthBox(true, bikeTrip.time)
            }
        }
    }
}


