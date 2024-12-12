package com.example.prago.view.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prago.R
import com.example.prago.view.LocalNavController

@Composable
fun TopBar(
    iconResource: Int,
    title: String,
    onIconClick: () -> Unit
){
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxWidth()
            .height(52.dp)
            .padding(start = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Box(
            modifier = Modifier
                .size(48.dp)
                .padding(all = 4.dp)
                .clickable { onIconClick() }
        ) {
            Icon(
                painter = painterResource(id = iconResource),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = Color.White
            )
        )
    }

}



@Composable
fun SearchTopBar(){
    val navController = LocalNavController.current
    TopBar(
        iconResource = R.drawable.menu,
        title = "PragO",
        onIconClick = { navController.navigate("settingsScreen") }
    )
}


@Composable
fun GoBackTopBar(){
    val navController = LocalNavController.current
    TopBar(
        iconResource = R.drawable.arrow_back,
        title = "PragO",
        onIconClick = { navController.navigate("searchPage") }
    )
}