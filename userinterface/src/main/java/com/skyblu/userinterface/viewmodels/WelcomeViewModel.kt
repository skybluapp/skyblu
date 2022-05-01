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
class WelcomeViewModel @Inject constructor(
    authentication: AuthenticationInterface
) : ViewModel() {

    val currentUser : MutableState<String?> = mutableStateOf(null)

    init{
        viewModelScope.launch {
            authentication.loggedInFlow.collectLatest {
                currentUser.value = it
            }
        }
    }
}
