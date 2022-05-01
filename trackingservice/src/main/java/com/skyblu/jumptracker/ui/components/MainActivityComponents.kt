package com.skyblu.jumptracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import timber.log.Timber

@Preview
    @Composable
    fun ListItem(label : String = "Last Latitude", value : String = "10.59283"){
        Row {
            Text(
                modifier = Modifier.padding(end = 10.dp),
                text = label,
                fontWeight = FontWeight.Bold
            )
            Text(text = value)
        }
    }

    @Preview
    @Composable
    fun ServiceControls(startTracking : () -> Unit = {}, stopTracking : () -> Unit = {}, function : () -> Unit = {}){
        Column(verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = {

                    startTracking()
                          },
                modifier = Modifier.padding(end = 10.dp),
            ) {
                Text(text = "Start Tracking", fontWeight = FontWeight.Bold)
            }
            Button(onClick = {

                stopTracking()
            }) {
                Text(text = "Stop Tracking", fontWeight = FontWeight.Bold)
            }
            Button(onClick = {

                function()
            }) {
                Text(text = "Function", fontWeight = FontWeight.Bold)
            }
        }
    }
