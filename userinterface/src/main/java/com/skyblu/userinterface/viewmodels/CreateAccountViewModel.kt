package com.skyblu.userinterface.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyblu.data.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Holds the current state of the Create Account Screen
 * @param email the email inputted in the email field
 * @param password the password inputted in the password field
 * @param confirmPassword the password inputted in the confirm password field
 * @param errorMessage a message if an error occurs
 * @param thisUserID contains the UID of the currently logged in user, or null if no user is logged in
 */
data class CreateAccountState(
    val email: MutableState<String> = mutableStateOf(""),
    val password: MutableState<String> = mutableStateOf(""),
    val confirmPassword: MutableState<String> = mutableStateOf(""),
    val errorMessage: MutableState<String?> = mutableStateOf(null),
    val thisUserID: MutableState<String?> = mutableStateOf(null),
)

/**
 * Manages data for the Create Account Screen
 * @param repository Provides an API to communicate with sources of data
 * @property authentication an interface to communicate with authentication system
 * @property state contains the current state of the Welcome Screen
 */
@HiltViewModel
class CreateAccountViewModel @Inject constructor(
    repository: Repository
) : ViewModel() {

    //Data Sources
    private val authentication = repository.authenticationInterface

    //Screen State
    val state by mutableStateOf(CreateAccountState())

    init {
        viewModelScope.launch {
            // Collects current login state from the authentication interface. State will update if any changes are made
            authentication.signedInStatus.collectLatest {
                state.thisUserID.value = it
            }
        }
    }

    // Calls upon the authentication interface to attempt to create a new account.
    // If unsuccessful, the error message is updated

    fun createAccount() {
        authentication.createAccount(
            email = state.email.value,
            password = state.password.value,
            confirm = state.confirmPassword.value,
            onFailure = { errorMessage -> ; state.errorMessage.value = errorMessage },
            onSuccess = { },
        )
    }

    //Clears the error message
    fun clearErrorMessage() {
        state.errorMessage.value = null
    }
}
