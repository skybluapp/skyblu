package com.skyblu.userinterface.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyblu.data.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Holds the current state of the Login Screen
 * @param email the email inputted in the email field
 * @param password the password inputted in the password field
 * @param errorMessage a message if an error occurs
 */
data class LoginState(
    val email: MutableState<String> = mutableStateOf(""),
    val password: MutableState<String> = mutableStateOf(""),
    val errorMessage: MutableState<String?> = mutableStateOf(null),
    val loggedIn: MutableState<Boolean> = mutableStateOf(false),
    val currentUser: MutableState<String?> = mutableStateOf(null)
)

/**
 * Manages data for the Login Screen
 * @param repository Provides an API to communicate with sources of data
 * @property authentication an interface to communicate with authentication system
 * @property state contains the current state of the Welcome Screen
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    repository: Repository
) : ViewModel() {

    //Data Sources
    private val authentication = repository.authenticationInterface

    //Screen State
    var state by mutableStateOf(LoginState())

    init {
        viewModelScope.launch {
            // Collects current login state from the authentication interface. State will update if any changes are made
            authentication.signedInStatus.collectLatest {
                state.currentUser.value = it
            }
        }
    }

    /**
     * Calls upon the authentication interface to attempt to log a user in.
     * If unsuccessful, the error message is updated.
     * If successful, logged in state is updated.
     */
    fun login() {
        authentication.loginWithEmailAndPassword(
            email = state.email.value.trim(),
            password = state.password.value,
            onFailure = { errorMessage -> state.errorMessage.value = errorMessage },
            onSuccess = {
                if (!authentication.thisUser.isNullOrBlank()) {
                    state.loggedIn.value = true
                }
            })

    }

}


