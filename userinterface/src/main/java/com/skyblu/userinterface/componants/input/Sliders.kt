package com.skyblu.userinterface.componants.input

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.skyblu.configuration.Concept
import com.skyblu.configuration.LARGE_PADDING
import com.skyblu.configuration.SMALL_PADDING

@Composable
fun sliderColors(): SliderColors {
    return SliderDefaults.colors(
        activeTickColor = MaterialTheme.colors.onBackground,
        thumbColor = MaterialTheme.colors.onBackground,
        activeTrackColor = MaterialTheme.colors.onBackground
    )
}

@Composable
fun AppSettingsSlider(
    appConcepts: Concept = Concept.BlueAircraft,
    title: String? = "Slider",
    value: Float,
    onValueChanged: (Float) -> Unit = {},
    range: ClosedFloatingPointRange<Float>,
    prepend: String = ""
) {
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.background),
            verticalAlignment = Alignment.Bottom,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(
                    start = LARGE_PADDING,
                    top = LARGE_PADDING
                )
            ) {
                Icon(
                    painter = painterResource(id = appConcepts.icon),
                    contentDescription = appConcepts.title
                )
                Text(
                    text = title ?: appConcepts.title,
                    modifier = Modifier.padding(start = LARGE_PADDING)
                )
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .height(SMALL_PADDING)
                .background(MaterialTheme.colors.background),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            Slider(
                modifier = Modifier
                    .padding(horizontal = LARGE_PADDING)
                    .fillMaxWidth(0.8f),
                value = value,
                onValueChange = { onValueChanged(it) },
                colors = sliderColors(),
                valueRange = range,
            )

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(end = LARGE_PADDING),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    value.toInt().toString() + prepend
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AppSettingsRangeSlider(
    appConcepts: Concept = Concept.BlueAircraft,
    title: String? = "Slider",
    values: ClosedFloatingPointRange<Float>,
    onValueChanged: (ClosedFloatingPointRange<Float>) -> Unit = {},
    range: ClosedFloatingPointRange<Float>,
    prepend: String = ""
) {
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.background),
            verticalAlignment = Alignment.Bottom,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(
                    start = LARGE_PADDING,
                    top = LARGE_PADDING
                )
            ) {
                Icon(
                    painter = painterResource(id = appConcepts.icon),
                    contentDescription = appConcepts.title
                )
                Text(
                    text = title ?: appConcepts.title,
                    modifier = Modifier.padding(start = LARGE_PADDING)
                )
            }
        }




        Row(
            Modifier
                .fillMaxWidth()
                .height(SMALL_PADDING)
                .background(MaterialTheme.colors.background),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .padding(start = LARGE_PADDING)
                    .weight(0.5f)
            ) {
                Text(
                    values.start.toInt().toString() + prepend
                )
            }

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .padding(horizontal = LARGE_PADDING)
                    .weight(2f)
            ) {
                RangeSlider(
                    values = values,
                    onValueChange = { onValueChanged(it) },
                    valueRange = range,
                    colors = sliderColors()
                )
            }




            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .padding(end = LARGE_PADDING)
                    .weight(0.5f)
            ) {
                Text(
                    values.endInclusive.toInt().toString() + prepend
                )
            }
        }
    }
}

@Preview
@Composable
fun AppSliderPreview() {
    var sliderPosition by remember { mutableStateOf(0f) }
    AppSettingsSlider(
        value = sliderPosition,
        onValueChanged = { sliderPosition = it },
        range = 0f .. 10f
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun AppRangeSliderPreview() {
    var sliderPosition by remember { mutableStateOf(30f .. 100f) }
    var sliderPosition2 by remember { mutableStateOf(30f .. 100f) }


    RangeSlider(
        values = sliderPosition,
        onValueChange = { sliderPosition = it },
        valueRange = 0f .. 150f
    )


    AppSettingsRangeSlider(
        values = sliderPosition2,
        onValueChanged = { sliderPosition2 = it },
        range = 0f .. 150f
    )
}
