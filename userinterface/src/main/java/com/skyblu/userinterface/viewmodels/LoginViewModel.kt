package com.skyblu.userinterface.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyblu.data.authentication.AuthenticationInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    val authentication : AuthenticationInterface
) : ViewModel(){

    val email : MutableState<String> = mutableStateOf("")
    val password : MutableState<String> = mutableStateOf("")
    val errorMessage : MutableState<String?> = mutableStateOf(null)
    val loggedIn : MutableState<Boolean> = mutableStateOf(false)

    val currentUser : MutableState<String?> = mutableStateOf(null)



    init {
        viewModelScope.launch {
            authentication.loggedInFlow.collectLatest {
                currentUser.value = it
            }
        }
    }

    fun login(){

        authentication.login(email.value.trim(), password.value, onFailure = { errorMessage -> this.errorMessage.value = errorMessage}, onSuccess = {
            if(!currentUser().isNullOrBlank()){
                loggedIn.value = true
            }
        })

    }

    fun currentUser() : String?{
        return authentication.getThisUserID()
    }

}


