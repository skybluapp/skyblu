package com.skyblu.userinterface.viewmodels

import android.content.Context
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.skyblu.configuration.success
import com.skyblu.data.authentication.AuthenticationInterface
import com.skyblu.data.datastore.DatastoreInterface
import com.skyblu.data.datastore.PreferenceKeys
import com.skyblu.data.firestore.ReadServerInterface
import com.skyblu.data.firestore.WriteServerInterface
import com.skyblu.data.firestore.toUser
import com.skyblu.data.users.SavedUsersInterface
import com.skyblu.models.jump.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class UniversalState(
    val thisUser: MutableState<String?> = mutableStateOf("ThisUser"),
    val updateWorkUUID: MutableState<String?> = mutableStateOf(null),
    val updateJumpUUID: MutableState<String?> = mutableStateOf(null),
    val deleteJumpUUID: MutableState<String?> = mutableStateOf(null),
    val uploadJumpUUID: MutableState<String?> = mutableStateOf(null),
    val message: MutableState<String> = mutableStateOf(""),
    val messageImportance: MutableState<Alert> = mutableStateOf(Alert.SUCCESS)
)

enum class Alert {
    SUCCESS,
    WARNING,
    ERROR
}

@HiltViewModel
class AppViewModel @Inject constructor(
    private val authentication: AuthenticationInterface,
    val datastore: DatastoreInterface,
    val savedUsers: SavedUsersInterface,
    val readServer: ReadServerInterface,
    val writeServer: WriteServerInterface,
    val appContext: Context
) : ViewModel() {

    val state by mutableStateOf(UniversalState())
    val workManager = WorkManager.getInstance(appContext)

    val workFlow: Flow<String?> = flow<String?> {
        while (true) {
            viewModelScope.launch {
                this.launch {
                    datastore.readStringFromDatastore(PreferenceKeys.UPDATE_USER_WORK) {
                        state.updateWorkUUID.value = it
                    }
                }
                this.launch {
                    datastore.readStringFromDatastore(PreferenceKeys.UPDATE_JUMP_WORK) {
                        state.updateJumpUUID.value = it
                    }
                }
                this.launch {
                    datastore.readStringFromDatastore(PreferenceKeys.DELETE_JUMP_WORK) {
                        state.deleteJumpUUID.value = it
                    }
                }
                this.launch {
                    datastore.readStringFromDatastore(PreferenceKeys.UPLOAD_JUMP_WORK) {
                        state.uploadJumpUUID.value = it
                    }
                }
            }
            delay(1000)
        }
    }

    fun setDisappearingMessage(alert : Alert = Alert.WARNING, message : String){
        state.message.value = message
        state.messageImportance.value = alert
        viewModelScope.launch {
            delay(3000)
            state.message.value = ""
        }
    }



    init {


        viewModelScope.launch {
            workFlow.collectLatest {
                state.updateWorkUUID.value = it
            }
        }



        viewModelScope.launch {
            authentication.loggedInFlow.collectLatest { user ->
                state.thisUser.value = user
                if (user != null && !savedUsers.containsUser(user)) {
                    val result = readServer.getUser(user)
                    val user: User? = result.getOrNull()?.toUser()
                    if (user != null) {
                        savedUsers.addUser(user)
                    }

                }
            }

        }



        observeWork("updateUserWork", "Account Updated", "Account Update Failed", "Waiting For Network", UUID = state.updateWorkUUID)
        observeWork("updateJumpWork", "Jump Updated", "Jump Update Failed", "Waiting For Network", UUID = state.updateJumpUUID)
        observeWork("deleteJumpWork", "Jump Deleted", "Jump Delete Failed", "Waiting For Network", UUID = state.deleteJumpUUID)
        observeWork("uploadJumpWork", "Jump Uploaded", "Jump Upload Failed", "Waiting For Network", UUID = state.uploadJumpUUID)

    }



    fun refresh() {
        savedUsers.userMap.clear()
    }

    private fun observeWork(workName : String, successMessage : String, failedMessage : String, enqueuedMessage : String, UUID : MutableState<String?> ){

        workManager.getWorkInfosForUniqueWorkLiveData(workName)
            .observeForever() { workInfos ->
                val updeteUserWork =
                    workInfos.find { it.id.toString() == UUID.value.toString() }
                if (updeteUserWork != null) {
                    when (updeteUserWork.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            state.message.value = successMessage

                            state.messageImportance.value = Alert.SUCCESS
                            viewModelScope.launch {
                                savedUsers.getThisUser()
                                delay(2000)
                                state.message.value = ""
                            }
                        }
                        WorkInfo.State.FAILED -> {
                            state.message.value = failedMessage
                            state.messageImportance.value = Alert.ERROR
                        }
                        WorkInfo.State.ENQUEUED -> {
                            state.message.value = enqueuedMessage
                            state.messageImportance.value = Alert.WARNING
                        }
                        else -> {
                            state.message.value = ""
                        }
                    }
                }
            }
    }

}