package com.skyblu.userinterface.viewmodels

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.firebase.firestore.DocumentSnapshot
import com.skyblu.configuration.JUMP_PAGE_SIZE
import com.skyblu.data.authentication.AuthenticationInterface
import com.skyblu.data.firestore.ReadServerInterface
import com.skyblu.data.firestore.WriteServerInterface
import com.skyblu.data.firestore.toUser
import com.skyblu.data.pagination.Pager
import com.skyblu.data.users.SavedUsersInterface
import com.skyblu.models.jump.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

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

@HiltViewModel
class SearchUserViewModel @Inject constructor(
    readServer: ReadServerInterface,
    savedUsers : SavedUsersInterface,
    val authentication : AuthenticationInterface,
    val writeServer : WriteServerInterface,
    val context : Context
) : ViewModel(){

    val state by mutableStateOf(SearchState())

    private val paginator = Pager(
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

    fun search(){
        state.searchedUsers.clear()
        paginator.reset()
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    fun loadNextPage(){
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    fun addFriend(friendID : String){
        writeServer.addFriend(userID = authentication.getThisUserID()!!, friendID = friendID, applicationContext = context )
    }

    fun unfriend(friendID : String){
        writeServer.unfriend(userID = authentication.getThisUserID()!!, friendID = friendID, applicationContext = context )
    }

}
