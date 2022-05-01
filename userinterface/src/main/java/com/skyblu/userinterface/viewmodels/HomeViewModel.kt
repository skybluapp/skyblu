package com.skyblu.userinterface.viewmodels

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.firebase.firestore.DocumentSnapshot
import com.skyblu.configuration.PERMISSIONS
import com.skyblu.configuration.JUMP_PAGE_SIZE
import com.skyblu.data.authentication.AuthenticationInterface
import com.skyblu.data.firestore.ReadServerInterface
import com.skyblu.data.firestore.WriteServerInterface
import com.skyblu.data.firestore.toJump
import com.skyblu.data.firestore.toUser
import com.skyblu.data.pagination.Pager
import com.skyblu.data.users.SavedSkydives
import com.skyblu.data.users.SavedUsersInterface
import com.skyblu.dependancyinjection.PermissionsInterfaceImpl
import com.skyblu.models.jump.Jump
import com.skyblu.models.jump.User
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Contains the current state of the Home Screen
 * @param isLoading True if the skydive list is currently loading
 * @param skydives The current list of skydives that have been loaded
 * @param error Contains a string if an error has occurred
 * @param endReached True if there is no more content to be loaded from the
 * @param page The document that acts as the key to access more content
 */
data class HomeState(
    var isLoading: MutableState<Boolean> = mutableStateOf(false),
    var skydives: MutableList<Jump> = mutableListOf(),
    var error: String? = null,
    var endReached: Boolean = false,
    var page: DocumentSnapshot? = null,
    val isRefreshing: MutableState<Boolean> = mutableStateOf(false),
    val swipeRefreshState: MutableState<SwipeRefreshState> = mutableStateOf(SwipeRefreshState(isRefreshing = isRefreshing.value)),
    val friendList : MutableList<String> = mutableListOf<String>()
)

/**
 * ViewModel for Home Screen
 * @param room Provide interface to access Room local database
 * @param authentication provide interface to access authentication functions
 * @property state current state of the Home Screen
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authentication: AuthenticationInterface,
    val readServer : ReadServerInterface,
    val writeServer : WriteServerInterface,
    val savedUsers: SavedUsersInterface,
    val savedSkydives: SavedSkydives,
    @ApplicationContext val context: Context
) : ViewModel() {

    /**
     * Create new state
     */
    var state by mutableStateOf(HomeState())

    /**
     * Pagination manages paged content for the Home Screen
     */
    private val paginator = Pager(
        initialKey = state.page,
        onRequest = { nextPage ->
            readServer.getJumps(
                nextPage,
                JUMP_PAGE_SIZE,
                savedUsers.userMap[authentication.getThisUserID()!!]?.friends

            )
        },
        onLoadUpdated = {
            state.isLoading.value = it
        },
        onSuccess = { list, newKey ->
            state.page = newKey
            state.endReached = list.documents.size < JUMP_PAGE_SIZE

            /**
             * For each document received, convert it to a Skydive and add it to the list
             */
            for (document in list.documents) {
                val skydive = document.toJump()
                state.skydives.add(skydive)


                /**
                 * If the user has not been saved, get the user from the server and store their details
                 */
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

    /**
     * Loads the next page
     */
    fun loadNextSkydivePage() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    /**
     * Refreshes the list of skydives
     */
    @OptIn(ExperimentalCoilApi::class)
    fun refresh() {
        state.skydives.clear()
        paginator.reset()
        viewModelScope.launch {

            loadNextSkydivePage()
        }


    }

    /**
     * Checks if permissions have been granted
     */
    fun checkPermissions(activity: Activity): Boolean {
        val permissionInterface = PermissionsInterfaceImpl(activity = activity)
        return permissionInterface.checkPermissions(PERMISSIONS)
    }

    /**
     * Request location permissions for tracking skydives
     */
    fun requestPermissions(activity: Activity) {
        val permissionInterface = PermissionsInterfaceImpl(activity = activity)
        permissionInterface.requestPermission(PERMISSIONS)
    }
}

