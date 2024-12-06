package com.example.prago.composables.searchScreen

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prago.R
import com.example.prago.activities.LocalNavController
import com.example.prago.ui.theme.Gray33
import com.example.prago.viewModels.SharedViewModel


@Composable
fun LabelWithTextInput(
    label: String,
    placeholder: String,
    text: String,
    onTextChange: (String) -> Unit,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, fontWeight = FontWeight.Bold)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable {
                    Log.i("DEBUG", "OnClick calling")
                    onClick()
                    Log.i("DEBUG", "OnClick finished")
                }
                .border(
                    1.5.dp,
                    MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.small
                )
                .background(Gray33)
                .padding(8.dp),
            contentAlignment = Alignment.CenterStart
        ){
            if(text != ""){
                Text(
                    text = text,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                )
            } else {
                Text(
                    text = placeholder,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun TextInput(
    viewModel: SharedViewModel,
    context: Context,
    fromText: String,
    toText: String,
    onFromValueChange: (String) -> Unit,
    onToValueChange: (String) -> Unit
) {
    val navControler = LocalNavController.current

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(start = 8.dp, top = 0.dp, end = 8.dp, bottom = 4.dp)
    ) {
        Column() {
            LabelWithTextInput(label = stringResource(R.string.from) + ":", placeholder = stringResource(R.string.source_stop), text = fromText, onTextChange = onFromValueChange, onClick = {navControler.navigate("fromStopSelect")})
            Spacer(modifier = Modifier.height(8.dp))
            LabelWithTextInput(label = stringResource(R.string.to) + ":", placeholder = stringResource(R.string.destination_stop), text = toText, onTextChange = onToValueChange, onClick = {navControler.navigate("toStopSelect")})
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(end = 10.dp),
            horizontalArrangement = Arrangement.End,
        ){
            Icon(
                painter = painterResource(id = R.drawable.arrows),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .offset(x = 0.dp, y = 92.dp)
                    .size(32.dp)
                    .clickable(
                        onClick = {
                            val fromSearchQuery = viewModel.fromText.value
                            //viewModel.onFromSearchQueryChange(viewModel.toSearchQuery)
                            //viewModel.onToSearchQueryChange(fromSearchQuery)
                            viewModel.fromText.value = viewModel.toText.value
                            viewModel.toText.value = fromSearchQuery
                        }
                    )
            )
        }

    }
}