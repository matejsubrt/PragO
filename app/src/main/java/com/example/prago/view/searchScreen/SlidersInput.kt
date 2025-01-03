package com.example.prago.view.searchScreen

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prago.R


@Composable
fun SliderWithLabels(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    maxValue: Float,
    labels: List<String>
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 12.dp)
    ){
        Column(modifier = Modifier) {
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = 0f..maxValue,
                steps = maxValue.toInt() - 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                colors = SliderDefaults.colors(
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = Color.White,
                    thumbColor = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                labels.forEachIndexed { index, labelText ->
                    Box(
                        modifier = Modifier
                            .rotate(-45f) // Rotate the box containing the text
                            .padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = labelText,
                            modifier = Modifier.align(Alignment.Center),
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 10.sp,
                            style = TextStyle(
                                lineHeight = 8.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }
        }
    }

}


@Composable
fun SlidersInput(
    labels: List<String>,
    values: List<Float>,
    onValueChanges: List<(Float) -> Unit>,
    maxValues: List<Float>,
    labelLists: List<List<String>>,
    useSharedBikes: Boolean,
    bikeMax15MinSwitch: @Composable () -> Unit
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        labels.forEachIndexed { index, label ->
            if (index == labels.size - 1 && useSharedBikes) {
                val value = values[index]
                val maxValue = maxValues[index]
                val labelList = labelLists[index]

                val sliderValue = remember { mutableStateOf(value) }
                SliderWithLabels(
                    label = label,
                    value = sliderValue.value,
                    onValueChange = {
                        sliderValue.value = it
                        onValueChanges[index](it)
                    },
                    maxValue = maxValue,
                    labels = labelList
                )
            } else if (index != labels.size - 1) {
                val value = values[index]
                val maxValue = maxValues[index]
                val labelList = labelLists[index]

                SliderWithLabels(
                    label = label,
                    value = value,
                    onValueChange = onValueChanges[index],
                    maxValue = maxValue,
                    labels = labelList
                )
            }
        }

        if(useSharedBikes){
            bikeMax15MinSwitch()
        }
    }

}

@Composable
fun SlidersBox(
    labels: List<String>,
    values: List<Float>,
    onValueChanges: List<(Float) -> Unit>,
    maxValues: List<Float>,
    labelLists: List<List<String>>,
    useSharedBikes: Boolean,
    bikeMax15MinSwitch: @Composable () -> Unit,
    backgroundColor: Color
){
    var slidersInputVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 12.dp)
    ){
        Column(){
            Box(
                modifier = Modifier
                    .clickable {
                        slidersInputVisible = !slidersInputVisible
                    }
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(backgroundColor)
                    .padding(start = 8.dp, top = 0.dp, end = 8.dp, bottom = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.extended_settings),
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = stringResource(R.string.expand_sliders),
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(if (slidersInputVisible) 180f else 0f),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            AnimatedVisibility(visible = slidersInputVisible) {
                SlidersInput(
                    labels = labels,
                    values = values,
                    onValueChanges = onValueChanges,
                    maxValues = maxValues,
                    labelLists = labelLists,
                    useSharedBikes = useSharedBikes,
                    bikeMax15MinSwitch = bikeMax15MinSwitch
                )
            }
        }
    }
}