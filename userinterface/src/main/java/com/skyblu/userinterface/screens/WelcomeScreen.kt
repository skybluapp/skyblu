package com.skyblu.userinterface.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.skyblu.configuration.*
import com.skyblu.userinterface.R
import com.skyblu.userinterface.componants.ActionConcept
import com.skyblu.userinterface.componants.alerts.AppBanner
import com.skyblu.userinterface.componants.input.*
import com.skyblu.userinterface.componants.scaffold.AppTopAppBar
import com.skyblu.userinterface.ui.theme.ThemeBlueGradient
import com.skyblu.userinterface.viewmodels.*

/**
 * A screen that Welcomes the user to the app
 * @param navController Controls navigation between screens
 * @param viewModel Manages the state for the screen
 */
@Composable
@Preview
fun WelcomeScreen(
    navController: NavController = rememberNavController(),
    viewModel : IWelcomeViewModel = WelcomeViewModelPreview()

)
{
    val state = viewModel.state
    LaunchedEffect(
        key1 = viewModel.state.thisUserID.value,
        block = {
            if(!viewModel.state.thisUserID.value.isNullOrBlank()){
                navController.navigate(Concept.LoggedIn.route)
            }
        }
    )
    val context = LocalContext.current
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = ThemeBlueGradient),

        ){

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = com.skyblu.models.R.drawable.app_icon_no_background),
                    contentDescription = "",
                    alignment = Alignment.Center,
                    modifier = Modifier.size(200.dp),
                )
                Text(
                    text = "Welcome to ${context.getString(R.string.app_name)}",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier
                        .padding(SMALL_PADDING),
                    fontWeight = FontWeight.Bold,
                )

            }



            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(LARGE_PADDING)
            ) {

                AppSpanButton(
                    text = "Login to ${context.getString(R.string.app_name)}",
                    onClick = { navController.navigate(Concept.Login.route) },
                    trailingIcon = Concept.Login.icon
                )

                AppTextButton(
                    onClick = {navController.navigate(Concept.CreateAccount.route)},
                    text = "Don't have an Account? Create Account"
                )
            }
    }
}

/**
 * A screen that Logs the user in to the app
 * @param navController Controls navigation between screens
 * @param viewModel Manages the state for the screen
 */
@Preview
@Composable
fun LoginScreen(
    navController: NavController = rememberNavController(),
    viewModel : LoginViewModel = hiltViewModel(),
){
    val state = viewModel.state

    LaunchedEffect(
        key1 = state.currentUser.value,
        block = {
            if(!state.currentUser.value.isNullOrBlank()){
                navController.navigate(Concept.LoggedIn.route)
            }
        }
    )


    val navIcon = Concept.Login

    Scaffold(
        content = {
                  LoginContent(
                      onUsernameChanged = {state.email.value = it},
                      onPasswordChanged = {state.password.value = it},
                      username = state.email.value,
                      password = state.password.value,
                      errorMessage = state.errorMessage.value,
                      onCloseError = {state.errorMessage.value = null}
                  )
        },
        topBar = {
            AppTopAppBar(
                title = Concept.Login.title,
                navigationIcon = {
                    ActionConceptList(
                        menuActions = listOf(
                            ActionConcept(
                                action = {
                                    navController.navigate(Concept.Welcome.route) {
                                        popUpTo(Concept.Welcome.route) {
                                            inclusive = true
                                        }
                                    }
                                },
                                concept = Concept.Previous
                            )
                        )
                    )
                },
                actionIcons = {
                    ActionConceptList(
                        menuActions = listOf(
                            ActionConcept(
                                action = { viewModel.login() },
                                concept = Concept.Login
                            )
                        )
                    )
                },
            )
        }
    )
}

/**
 * A screen that allows a user to create an account
 * @param navController Controls navigation between screens
 * @param viewModel Manages the state for the screen
 */
@Preview
@Composable
fun CreateAccountScreen(
    navController: NavController = rememberNavController(),
    viewModel : CreateAccountViewModel = hiltViewModel()
){

    val state = viewModel.state

    LaunchedEffect(
        key1 = state.thisUserID.value,
        block = {
            if(!state.thisUserID.value.isNullOrBlank()){
                navController.navigate(Concept.LoggedIn.route)
            }
        }
    )

    Scaffold(
        content = {
            CreateAccountContent(
                onEmailChanged = {state.email.value = it},
                onPasswordChanged = {state.password.value = it},
                onConfirmPasswordChanged = {state.confirmPassword.value = it},
                email = state.email.value,
                password = state.password.value,
                confirmPassword = state.confirmPassword.value,
                errorMessage = state.errorMessage.value,
                onCloseError = {state.errorMessage.value = null; }
            )
        },
        topBar = {
            AppTopAppBar(
                title = Concept.CreateAccount.title,
                navigationIcon = {
                    ActionConceptList(
                        menuActions = listOf(
                            ActionConcept(
                                action = {
                                    navController.navigate(Concept.Welcome.route) {
                                        popUpTo(Concept.Welcome.route) {
                                            inclusive = true
                                        }
                                    }
                                },
                                concept = Concept.Previous
                            )
                        )
                    )
                },
                actionIcons = {
                    ActionConceptList(
                        menuActions = listOf(
                            ActionConcept(
                                action = { viewModel.createAccount() },
                                concept = Concept.CreateAccount
                            )
                        )
                    )
                },
            )
        },
        floatingActionButton = {


        }

    )
}


@Composable
fun LoginContent(
    onUsernameChanged : (String) -> Unit,
    onPasswordChanged : (String) -> Unit,
    username : String,
    password : String,
    errorMessage: String?,
    onCloseError: () -> Unit
){
    Column(Modifier.fillMaxSize()) {
        if(!errorMessage.isNullOrBlank()){
            AppBanner(
                text = errorMessage,
                actionConcept = ActionConcept(Concept.Close) { onCloseError() },
                importance = Alert.WARNING
            )
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(SMALL_PADDING)
                .padding(top = MEDIUM_PADDING),
            verticalArrangement = Arrangement.spacedBy(SMALL_PADDING)
        ) {
            AppTextField(
                value = username,
                onValueChanged = {s -> onUsernameChanged(s)},
                imeAction = ImeAction.Next,
                leadingIcon = R.drawable.email,
                placeholder = EMAIL_STRING,
                keyboardType = KeyboardType.Email
            )
            AppTextField(
                value = password,
                onValueChanged = {s -> onPasswordChanged(s)},
                leadingIcon = R.drawable.password,
                placeholder = "Password",
                keyboardType = KeyboardType.Password
            )

        }

    }
    

}

@Composable
fun CreateAccountContent(
    onEmailChanged : (String) -> Unit,
    onPasswordChanged : (String) -> Unit,
    onConfirmPasswordChanged : (String) -> Unit,
    email : String,
    password : String,
    confirmPassword : String,
    errorMessage : String?,
    onCloseError : () -> Unit
){
    
    Column(Modifier.fillMaxSize()) {
        if(!errorMessage.isNullOrBlank()){
            AppBanner(
                text = errorMessage,
                actionConcept = ActionConcept(Concept.Close) { onCloseError() },
                importance = Alert.WARNING
            )
        }
        Column(
            Modifier
                .fillMaxSize()
                .padding(SMALL_PADDING)
                .padding(top = MEDIUM_PADDING),
            verticalArrangement = Arrangement.spacedBy(SMALL_PADDING)
        ) {



            AppTextField(
                value = email,
                onValueChanged = {s -> onEmailChanged(s)},
                imeAction = ImeAction.Next,
                leadingIcon = R.drawable.email,
                placeholder = EMAIL_STRING
            )
            AppTextField(
                value = password,
                onValueChanged = {s -> onPasswordChanged(s)},
                leadingIcon = R.drawable.password,
                placeholder = "Password",
                keyboardType = KeyboardType.Password
            )
            AppTextField(
                value = confirmPassword,
                onValueChanged = {s -> onConfirmPasswordChanged(s)},
                leadingIcon = R.drawable.password,
                placeholder = "Confirm Password",
                keyboardType = KeyboardType.Password
            )

        }
    }




}


