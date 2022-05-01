package com.skyblu.userinterface.screens

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
import com.skyblu.userinterface.componants.*
import com.skyblu.userinterface.componants.ActionConcept
import com.skyblu.userinterface.componants.alerts.StyledBanner
import com.skyblu.userinterface.componants.input.ActionConceptList
import com.skyblu.userinterface.componants.input.AppButton
import com.skyblu.userinterface.componants.input.AppTextButton
import com.skyblu.userinterface.componants.input.AppTextField
import com.skyblu.userinterface.componants.scaffold.AppTopAppBar
import com.skyblu.userinterface.ui.theme.ThemeBlueGradient
import com.skyblu.userinterface.viewmodels.Alert
import com.skyblu.userinterface.viewmodels.CreateAccountViewModel
import com.skyblu.userinterface.viewmodels.LoginViewModel
import com.skyblu.userinterface.viewmodels.WelcomeViewModel
import timber.log.Timber

@Composable
@Preview
fun WelcomeScreen(
    navController: NavController = rememberNavController(),
    viewModel : WelcomeViewModel = hiltViewModel()

)
{

    LaunchedEffect(
        key1 = viewModel.currentUser.value,
        block = {
            if(!viewModel.currentUser.value.isNullOrBlank()){
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

            Text(
                text = "Welcome to ${context.getString(R.string.app_name)}",
                style = MaterialTheme.typography.h3,
                modifier = Modifier.padding(LARGE_PADDING),
                fontWeight = FontWeight.Bold,

            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = LARGE_PADDING)
            ) {

                AppButton(
                    text = "Login to ${context.getString(R.string.app_name)}",
                    onClick = { navController.navigate(Concept.Login.route) },
                )

                AppTextButton(

                    onClick = {navController.navigate(Concept.CreateAccount.route)},
                    text = "Don't have an Account? Create Account"
                )
            }
    }
}

@Preview
@Composable
fun LoginScreen(
    navController: NavController = rememberNavController(),
    viewModel : LoginViewModel = hiltViewModel(),
){

    LaunchedEffect(
        key1 = viewModel.currentUser.value,
        block = {
            if(!viewModel.currentUser.value.isNullOrBlank()){
                navController.navigate(Concept.LoggedIn.route)
            }
        }
    )


    val navIcon = Concept.Login

    Scaffold(
        content = {
                  LoginContent(
                      onUsernameChanged = {viewModel.email.value = it},
                      onPasswordChanged = {viewModel.password.value = it},
                      username = viewModel.email.value,
                      password = viewModel.password.value,
                      errorMessage = viewModel.errorMessage.value,
                      onCloseError = {viewModel.errorMessage.value = null}
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

@Preview
@Composable
fun CreateAccountScreen(
    navController: NavController = rememberNavController(),
    viewModel : CreateAccountViewModel = hiltViewModel()
){

    LaunchedEffect(
        key1 = viewModel.currentUser.value,
        block = {
            if(!viewModel.currentUser.value.isNullOrBlank()){
                navController.navigate(Concept.LoggedIn.route)
            }
        }
    )

    Scaffold(
        content = {
            CreateAccountContent(
                onEmailChanged = {viewModel.email.value = it},
                onPasswordChanged = {viewModel.password.value = it},
                onConfirmPasswordChanged = {viewModel.confirmPassword.value = it},
                email = viewModel.email.value,
                password = viewModel.password.value,
                confirmPassword = viewModel.confirmPassword.value,
                errorMessage = viewModel.errorMessage.value,
                onCloseError = {viewModel.errorMessage.value = null; }
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
            StyledBanner(
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
            StyledBanner(
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


