package com.example.prago

import android.content.res.Resources.Theme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prago.ui.theme.PragOTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PragOTheme {
                SearchPage()
            }
        }
    }
}

@Composable
fun SearchPage(){
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(){
            Header()
            Body()
        }
    }
}

@Composable
fun Header(){
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxWidth()
            .height(52.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(
            painter = painterResource(id = R.drawable.menu),
            contentDescription = null,
            modifier = Modifier
                .size(tripIconSize.dp)
                .padding(all = 4.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "PragO",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = Color.White
            )
        )
    }
}

@Composable
fun Body(){
    Column(modifier = Modifier.padding(16.dp)) {
        // Label "From" with text input
        LabelWithTextInput(label = "From")

        // Label "To" with text input
        LabelWithTextInput(label = "To")
        Divider(thickness = 1.dp, modifier = Modifier.fillMaxWidth(), color = Color.White)

        // Label "Shared Bikes" with toggle switch
        LabelWithToggleSwitch(label = "Shared Bikes")

        // Sliders with integer values 0-3
        SliderWithValue(label = "Slider 1")
        SliderWithValue(label = "Slider 2")
        SliderWithValue(label = "Slider 3")

        // Search button
        Button(onClick = { /* Handle search action */ }, modifier = Modifier
            .align(Alignment.CenterHorizontally)) {
            Text(text = "Search")
        }
    }
}

@Preview
@Composable
fun HeaderPreview(){
    PragOTheme(darkTheme = false){
        Header()
    }

}

@Composable
fun LabelWithTextInput(label: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, fontWeight = FontWeight.Bold)
        OutlinedTextField(value = "", onValueChange = {}, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun LabelWithToggleSwitch(label: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, fontWeight = FontWeight.Bold)
        Switch(checked = false, onCheckedChange = { /* Handle switch action */ })
    }
}

@Composable
fun SliderWithValue(label: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, fontWeight = FontWeight.Bold)
        Slider(value = 0f, onValueChange = { /* Handle slider action */ }, valueRange = 0f..3f, steps = 1, modifier = Modifier.fillMaxWidth())
    }
}


@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    PragOTheme(darkTheme = true) {
        SearchPage()
    }
}