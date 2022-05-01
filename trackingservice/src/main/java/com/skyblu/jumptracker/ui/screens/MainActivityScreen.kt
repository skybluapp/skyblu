package com.skyblu.jumptracker.ui.screens
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
//import com.skyblu.skybluapp.viewmodels.MainViewModel
import com.skyblu.jumptracker.ui.components.ListItem
import com.skyblu.jumptracker.ui.components.ServiceControls

@OptIn(ExperimentalMaterialApi::class)



    @Composable
    fun JumpTrackerServiceScreen(
    startService: () -> Unit,
    stopService: () -> Unit,
    function: () -> Unit,
//    viewModel: com.skyblu.skybluapp.viewmodels.MainViewModel
){

        Column(
            Modifier.fillMaxSize(0.5F),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
//            Column(verticalArrangement = Arrangement.spacedBy(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
//                ListItem(label = "Pings", value = viewModel.pings.value.toString())
//                ListItem(label = "Lat", value = viewModel.recentLocation.value.latitude.toString())
//                ListItem(label = "Long", value = viewModel.recentLocation.value.longitude.toString())
//                ListItem(label = "Speed", value = viewModel.recentLocation.value.speed.toString())
//                ListItem(label = "Alt", value = viewModel.recentLocation.value.altitude.toString())
//                ListItem(label = "Pressure", value = viewModel.recentPressure.value.toString())
//                ListItem(label = "Accelerometer", value = "X: ${viewModel.recentXAccelerate.value.toString().substring(0,3)} Y: ${viewModel.recentYAccelerate.value.toString().substring(0,3)} Z:${viewModel.recentXAccelerate.value.toString().substring(0,3)}")
//                ServiceControls(startTracking = startService, stopTracking = stopService, function = function)
//            }
        }
    }

