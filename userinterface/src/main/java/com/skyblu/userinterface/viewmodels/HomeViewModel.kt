package com.skyblu.userinterface.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Holds the current state of the Home Screen
 * @param isLoading True if the skydive list is currently loading
 * @param skydives The current list of skydives that have been loaded
 * @param errorMessage Contains a string if an error has occurred
 * @param endReached True if there is no more content to be loaded from the
 * @param page The document that acts as the key to access more content
 * @param isRefreshing True if content is being refreshed
 * @param swipeRefreshState contains state for swipe-to-refresh
 */
data class HomeState(
    var isLoading: MutableState<Boolean> = mutableStateOf(false),
    var skydives: MutableList<Jump> = mutableListOf(),
    var error: String? = null,
    var endReached: Boolean = false,
    var page: DocumentSnapshot? = null,
    val isRefreshing: MutableState<Boolean> = mutableStateOf(false),
    val swipeRefreshState: MutableState<SwipeRefreshState> = mutableStateOf(SwipeRefreshState(isRefreshing = isRefreshing.value)),
    val friendList : MutableList<String> = mutableListOf<String>(),
    var errorMessage: String? = null,
)

/**
 * ViewModel for Home Screen
 * @param repository Provides an API to communicate with sources of data
 * @property authentication provide interface to access authentication functions
 * @property savedSkydives provides access to skydives currently in memory
 * @property savedUsers provides access to users currently in memory
 * @property savedUsers provides access to request and check permissions
 * @property state current state of the Home Screen
 * @property pager manages paged jump data
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    //Data Sources
    private val readServer = repository.readServerInterface
    private val savedUsers = repository.savedUsersInterface
    val savedSkydives = repository.savedSkydivesInterface
    private val authentication = repository.authenticationInterface
    val permissions = repository.permissionsInterface

    //Screen State
    var state by mutableStateOf(HomeState())



    /**
     * Pagination manages paged content for the Home Screen
     */
    private val pager = FirestorePaging(
        initialKey = state.page,
        onRequest = { nextPage ->
            readServer.getJumps(
                nextPage,
                JUMP_PAGE_SIZE,
                savedUsers.userMap[authentication.thisUser!!]?.friends

            )
        },
        onLoadUpdated = {
            state.isLoading.value = it
        },
        onSuccess = { list, newKey ->
            state.page = newKey
            state.endReached = list.documents.size < JUMP_PAGE_SIZE

            // For each document received, convert it to a Skydive and add it to the list
            for (document in list.documents) {
                val skydive = document.toJump()
                state.skydives.add(skydive)

                // If the user has not been saved, get the user from the server and store their details
                if (!savedUsers.containsUser(skydive.userID)) {
                    viewModelScope.launch {
                        val result = readServer.getUser(skydive.userID)
                        val user: User? = result.getOrNull()?.toUser()
                        if (user != null ) {
                            savedUsers.addUser(user = user)
                        }
                    }
                }
            }
        },
        onError = { error ->
            if (error != null) {
                state.error = error.message
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

    // Clears paged data and collects first page
    fun refresh() {
        state.skydives.clear()
        pager.reset()
        viewModelScope.launch {

            loadNextSkydivePage()
        }
    }
}
