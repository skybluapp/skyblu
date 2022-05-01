package com.skyblu.userinterface

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.skyblu.configuration.Concept
import com.skyblu.models.jump.JumpParams
import com.skyblu.models.jump.UserParameterNames
import com.skyblu.userinterface.screens.SplashScreen

import com.skyblu.userinterface.screens.*
import com.skyblu.userinterface.screens.settingsScreens.*
import com.skyblu.userinterface.viewmodels.*

/**
 * @author Oliver Stocks
 * [Documentation](https://developer.android.com/jetpack/compose/navigation)
 * Manages Navigation for the application
 */
@Composable
fun Navigation(
) {

    val homeViewModel: HomeViewModel = hiltViewModel()
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val trackingViewModel: TrackingScreenViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val navHostController: NavHostController = rememberNavController()
    val appViewModel : AppViewModel = hiltViewModel()


    NavHost(
        navController = navHostController,
        startDestination = Concept.Splash.route,
    ) {

        /**
         * Splash Screen Destination
         */
        composable(Concept.Splash.route) {
            SplashScreen(navController = navHostController)
        }

        /**
         * Logged In Destinations
         */
        navigation(
            route = Concept.LoggedIn.route,
            startDestination = Concept.Home.route
        ) {

            composable(Concept.Home.route) {
                HomeScreen(
                    navController = navHostController,
                    viewModel = homeViewModel,
                    appViewModel = appViewModel
                )
            }

            composable(Concept.Account.route) {
                CompleteAccountScreen(
                    navController = navHostController,
                    appViewModel = appViewModel
                )
            }

            composable(Concept.TrackSkydive.route) {
                TrackingScreen(
                    navController = navHostController,
                    viewModel = trackingViewModel
                )
            }

            composable(Concept.Tick.route) {
                CompleteSkydiveScreen(
                    navController = navHostController,
                    trackingScreenViewModel = trackingViewModel
                )
            }

            composable(route = "${Concept.Map.route}/{${JumpParams.JUMP_ID}}") { backStackEntry ->
                backStackEntry.arguments?.getString(JumpParams.JUMP_ID)
                    ?.let { jumpID ->
                        MapScreen(
                            navController = navHostController,
                            jumpID = jumpID
                        )
                    }
            }

            composable("${Concept.Profile.route}{${UserParameterNames.ID}}") { backStackEntry ->
                backStackEntry.arguments?.getString(UserParameterNames.ID)
                    ?.let {
                        ProfileScreen(
                            navController = navHostController,
                            userID = it,
                            viewModel = profileViewModel,
                            appViewModel = appViewModel
                        )
                    }
            }

            composable(Concept.Edit.route) {
                EditScreen(navController = navHostController, appViewModel = appViewModel)
            }

            composable(route = Concept.Search.route) {
                Searchscreen(navController = navHostController, appViewModel = appViewModel)
            }



        }
        /**
         * Logged Out Destinations
         */
        navigation(
            route = Concept.LoggedOut.route,
            startDestination = Concept.Welcome.route
        ) {
            composable(Concept.Welcome.route) { WelcomeScreen(navController = navHostController) }
            composable(route = Concept.Login.route) { LoginScreen(navController = navHostController) }
            composable(Concept.CreateAccount.route) { CreateAccountScreen(navController = navHostController) }
        }
        /**
         * Settings Destinations
         */
        navigation(
            route = Concept.Settings.route,
            startDestination = Concept.Settings.route + Concept.Home.route
        ) {
            composable(Concept.Settings.route + Concept.Home.route) { SettingsScreen(navController = navHostController, viewModel = settingsViewModel, appViewModel = appViewModel) }
            composable(route = Concept.LocationTracking.route + Concept.Settings.route) { TrackingSettingsScreen(navController = navHostController) }
            composable(Concept.Account.route + Concept.Settings.route) { AccountSettingsScreen(navController = navHostController, appViewModel = appViewModel) }
            composable(route = Concept.AircraftDetection.route + Concept.Settings.route) { AircraftDetectionScreen(navController = navHostController) }
            composable(route = Concept.FreefallDetection.route + Concept.Settings.route) { FreefallDetectionScreen(navController = navHostController) }
            composable(route = Concept.CanopyDetection.route + Concept.Settings.route) { CanopyDetectionScreen(navController = navHostController) }
            composable(route = Concept.LandingDetection.route + Concept.Settings.route) { LandingDetectionScreen(navController = navHostController) }
        }

    }
}


