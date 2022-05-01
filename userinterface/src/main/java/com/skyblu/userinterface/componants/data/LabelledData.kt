package com.skyblu.userinterface.componants.data

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skyblu.configuration.Concept
import com.skyblu.configuration.MEDIUM_PADDING
import com.skyblu.userinterface.R
import com.skyblu.userinterface.componants.icons.BasicIcon

@Composable
@Preview(showBackground = true)
fun AppDataPoint(
    appConcepts: Concept = Concept.Longitude,
    data: String = "data"
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(MEDIUM_PADDING)
            .fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
        ) {
            BasicIcon(list = listOf(appConcepts))
        }
        Column(
            modifier = Modifier.padding(start = MEDIUM_PADDING)
        ) {
            Text(
                text = appConcepts.title + ":",
                fontWeight = FontWeight.Bold
            )

            Text(
                text = data,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AppDataPoint(
    label: String = "label",
    data: String = "data",
    @DrawableRes icon :  Int = R.drawable.parachute
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(MEDIUM_PADDING)
            .fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
        ) {
            BasicIcon(icon = icon)
        }
        Column(
            modifier = Modifier.padding(start = MEDIUM_PADDING)
        ) {
            Text(
                text = "$label:",
                fontWeight = FontWeight.Bold
            )

            Text(
                text = data,
                textAlign = TextAlign.Center,
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LabelledText(
    text: String = "100",
    label: String = "Total Skydives"
) {
    val typography = MaterialTheme.typography
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = text,
            fontStyle = typography.h6.fontStyle,
            fontSize = typography.h6.fontSize,
        )

        Text(
            text = label,
            fontStyle = typography.body1.fontStyle,
            fontSize = typography.body1.fontSize,
            fontWeight = FontWeight.Bold
        )
    }
}
