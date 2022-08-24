package com.skyblu.userinterface.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.firebase.firestore.DocumentSnapshot
import com.skyblu.configuration.JUMP_PAGE_SIZE
import com.skyblu.data.Repository
import com.skyblu.data.firestore.toUser
import com.skyblu.data.pagination.FirestorePaging
import com.skyblu.models.jump.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Holds the current state of the Home Screen
 * @param search Text entered in to search field
 * @param isLoading True if the user search list is currently loading
 * @param searchedUsers List of users returned from the search field
 * @param endReached True if there is no more content to be loaded from the
 * @param page The document that acts as the key to access more content
 * @param isRefreshing True if content is being refreshed
 * @param swipeRefreshState contains state for swipe-to-refresh
 */
data class SearchState(
    val search : MutableState<String> = mutableStateOf(""),
    val isLoading : MutableState<Boolean> = mutableStateOf(false),
    var endReached : Boolean = false,
    var page: DocumentSnapshot? = null,
    val searchedUsers : MutableList<User> = mutableListOf<User>(),
    var error : MutableState<String> = mutableStateOf(""),
    val isRefreshing: MutableState<Boolean> = mutableStateOf(false),
    val swipeRefreshState: MutableState<SwipeRefreshState> = mutableStateOf(SwipeRefreshState(isRefreshing = isRefreshing.value)),
)

/**
 * Manages data for the Search User Screen
 * @param repository Provides an API to communicate with sources of data
 * @property authentication provide interface to access authentication functions
 * @property savedUsers provides access to users currently in memory
 * @property savedUsers provides access to request and check permissions
 * @property state current state of the Search Screen
 * @property pager manages paged jump data
 */
@HiltViewModel
class SearchUserViewModel @Inject constructor(
    private val repository : Repository
) : ViewModel(){

    val state by mutableStateOf(SearchState())
    private val readServer = repository.readServerInterface
    private val authentication = repository.authenticationInterface
    private val savedUsers = repository.savedUsersInterface
    private val writeServer = repository.writeServerInterface

    private val pager = FirestorePaging(
        initialKey = state.page,
        onRequest = { nextPage ->
            readServer.getUsers(
                nextPage,
                JUMP_PAGE_SIZE,
                search = state.search.value
            )
        },
        onLoadUpdated = {
            state.isLoading.value = it
        },
        onSuccess = { list, newKey ->
            state.page = newKey
            state.endReached = list.documents.isEmpty()

            /**
             * For each document received, convert it to a user and add it to the list
             */
            for (document in list.documents) {
                val user = document.toUser()
                state.searchedUsers.add(user)

                /**
                 * If the user has not been saved, get the user from the server and store their details
                 */
                if (!savedUsers.containsUser(user.ID)) {
                    viewModelScope.launch {
                        val result = readServer.getUser(user.ID)
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
                state.error.value = error.message.toString()
            }
        },
        getNextKey = { list ->
            list.lastOrNull()
        },
    )

    /**
     * Resets the pager and conducts a fresh search with the search string
     */
    fun search(){
        state.searchedUsers.clear()
        pager.reset()
        viewModelScope.launch {
            pager.loadNextItems()
        }
    }

    /**
     * Loads next page of search results
     */
    fun loadNextPage(){
        viewModelScope.launch {
            pager.loadNextItems()
        }
    }

    /**
     * Calls upon writeServer interface to add a new friend using their ID
     */
    fun addFriend(friendID : String){
        writeServer.addFriend(userID = authentication.thisUser!!, friendID = friendID)
    }

    /**
     * Calls upon writeServer interface to remove a friend using their ID
     */
    fun unfriend(friendID : String){
        writeServer.unfriend(userID = authentication.thisUser!!, friendID = friendID)
    }

}
