package com.skyblu.userinterface.viewmodels

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.firebase.firestore.DocumentSnapshot
import com.skyblu.configuration.JUMP_PAGE_SIZE
import com.skyblu.data.Repository
import com.skyblu.data.firestore.toJump
import com.skyblu.data.firestore.toUser
import com.skyblu.data.pagination.FirestorePaging
import com.skyblu.models.jump.Jump
import com.skyblu.models.jump.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Holds the current state of the Home Screen
 * @param isLoading True if the skydive list is currently loading
 * @param skydives The current list of skydives that have been loaded
 * @param errorMessage Contains a string if an error has occurred
 * @param endReached True if there is no more content to be loaded from the
 * @param page The document that acts as the key to access more content
 * @param profileUser The UID for the profile being viewed
 * @param isRefreshing True if content is being refreshed
 * @param swipeRefreshState contains state for swipe-to-refresh
 */
data class ProfileState(
    val isLoading: MutableState<Boolean> = mutableStateOf(false),
    var skydives: MutableList<Jump> = mutableListOf(),
    var errorMessage: String? = null,
    var endReached: Boolean = false,
    var page: DocumentSnapshot? = null,
    val profileUser: MutableState<String> = mutableStateOf("user"),
    val isMyProfile : MutableState<Boolean?> = mutableStateOf(null),
    val isRefreshing : MutableState<Boolean> = mutableStateOf(false),
    val swipeRefreshState : MutableState<SwipeRefreshState>  = mutableStateOf(SwipeRefreshState(isRefreshing = isRefreshing.value)),
    )

/**
 * Manages data for the Profile Screen
 * @param repository Provides an API to communicate with sources of data
 * @property authentication provide interface to access authentication functions
 * @property savedSkydives provides access to skydives currently in memory
 * @property savedUsers provides access to users currently in memory
 * @property savedUsers provides access to request and check permissions
 * @property state current state of the Profile Screen
 * @property pager manages paged jump data
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository : Repository
) : ViewModel() {

    //Data Sources
    private val readServer = repository.readServerInterface
    private val savedUsers = repository.savedUsersInterface
    val savedSkydives = repository.savedSkydivesInterface
    private val authentication = repository.authenticationInterface
    val permissions = repository.permissionsInterface

    //Screen State (One for each this user and other users)
    var thisUsersState by mutableStateOf(ProfileState())
    var otherUsersState by mutableStateOf(ProfileState())
    var state = thisUsersState


     // On initialisation, get User and first page
    init {
        viewModelScope.launch {
            authentication.signedInStatus.collectLatest { userID ->
                if (userID != null) {
                    thisUsersState.profileUser.value = userID
                }
            }
            launch {
                pager.loadNextItems()
            }
        }
    }

    private val pager = FirestorePaging(
        initialKey = state.page,
        onRequest = { nextPage ->
            readServer.getJumps(
                nextPage,
                JUMP_PAGE_SIZE,
                fromUsers = listOf(state.profileUser.value)
            )
        },
        onLoadUpdated = {
            state.isLoading.value = it
        },
        onSuccess = { list, newKey ->
            state.page = newKey
            state.endReached = list.documents.isEmpty()

            // For each document received, convert it to a Skydive and add it to the list
            for (document in list.documents) {
                val skydive = document.toJump()
                state.skydives.add(skydive)


                 // If the user has not been saved, get the user from the server and store their details
                if (!savedUsers.containsUser(skydive.userID)) {
                    viewModelScope.launch {
                        val result = readServer.getUser(skydive.userID)
                        val user: User? = result.getOrNull()?.toUser()
                        if (user != null) {
                            savedUsers.addUser(user = user)
                        }
                    }
                }
            }
        },
        onError = { error ->
            if (error != null) {
                state.errorMessage = error.message
            }
        },
        getNextKey = { list ->
            list.lastOrNull()
        },
    )

    //Calls upon pager to retrieve next page
    fun loadNextSkydivePage() {
        viewModelScope.launch {
            pager.loadNextItems()
        }
    }

    //Clears paged data and collects first page
    fun refresh() {
        state.skydives.clear()
        pager.reset()
        loadNextSkydivePage()
    }


    //Sets the user in state
    fun setUser(userID: String) {
        if(state.isMyProfile.value == true){
            thisUsersState = state
        }
        if(userID == thisUsersState.profileUser.value){
            state = thisUsersState
        } else {
            otherUsersState.profileUser.value = userID
            state = otherUsersState
            pager.reset()
            loadNextSkydivePage()
        }
    }

}




