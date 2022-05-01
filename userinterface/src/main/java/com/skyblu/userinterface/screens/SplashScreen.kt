package com.skyblu.userinterface.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.skyblu.configuration.Concept
import com.skyblu.userinterface.R

import com.skyblu.userinterface.ui.theme.ThemeBlueGradient
import kotlinx.coroutines.delay
import timber.log.Timber

@Composable
fun SplashScreen(navController : NavController){

    LaunchedEffect(key1 = true) {

        delay(1000)
        navController.navigate(Concept.LoggedIn.route){
            popUpTo(Concept.Splash.route){inclusive = true} }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = ThemeBlueGradient),
        contentAlignment = Alignment.Center
    ){
        Image(
            painter = painterResource(id = com.skyblu.models.R.drawable.freefall),
            contentDescription = "",
            alignment = Alignment.Center,
            modifier = Modifier.size(200.dp),
        )
    }
}



