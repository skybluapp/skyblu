package com.skyblu.userinterface.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyblu.data.authentication.AuthenticationInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
    private val authentication: AuthenticationInterface
) : ViewModel() {

    val email: MutableState<String> = mutableStateOf("")
    val password: MutableState<String> = mutableStateOf("")
    val confirmPassword: MutableState<String> = mutableStateOf("")
    val errorMessage: MutableState<String?> = mutableStateOf(null)
    val currentUser : MutableState<String?> = mutableStateOf(null)



    init {
        viewModelScope.launch {
            authentication.loggedInFlow.collectLatest {
                currentUser.value = it
            }
        }
    }


    fun createAccount() {
        authentication.createAccount(
            email = email.value,
            password = password.value,
            confirm = confirmPassword.value,
            onFailure = { errorMessage -> ; this.errorMessage.value = errorMessage },
            onSuccess = { uid ->  },
        )
    }


    fun clearErrorMessage(){
        errorMessage.value = null
    }
}
