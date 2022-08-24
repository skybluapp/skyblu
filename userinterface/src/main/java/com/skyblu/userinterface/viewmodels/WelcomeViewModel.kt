package com.skyblu.userinterface.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyblu.data.Repository
import com.skyblu.data.authentication.AuthenticationInterface
import com.skyblu.data.authentication.EmptyAuthentication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Holds the current state of the Welcome Screen
 * @param thisUserID contains the UID of the currently logged in user, or null if no user is logged in
 */
data class WelcomeState(
    var thisUserID : MutableState<String?> = mutableStateOf(null)
)

interface IWelcomeViewModel {
    val authentication: AuthenticationInterface
    val state: WelcomeState
}

/**
 * Manages data for the Welcome Screen
 * @param repository Provides an API to communicate with sources of data
 * @property authentication an interface to communicate with authentication system
 * @property state contains the current state of the Welcome Screen
 */
@HiltViewModel
class WelcomeViewModel @Inject constructor(
    repository: Repository
) : ViewModel(), IWelcomeViewModel {

    override val authentication = repository.authenticationInterface
    override val state by mutableStateOf(WelcomeState())

    init{
         // Collects current login state from the authentication interface. State will update if any changes are made
        viewModelScope.launch {
            authentication.signedInStatus.collectLatest {
                state.thisUserID.value = it
            }
        }

    }
}

class WelcomeViewModelPreview @Inject constructor() :  IWelcomeViewModel {
    override val authentication = EmptyAuthentication()
    override val state by mutableStateOf(WelcomeState())

}
