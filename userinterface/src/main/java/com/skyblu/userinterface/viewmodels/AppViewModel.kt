package com.skyblu.userinterface.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.skyblu.configuration.PreferenceKeys
import com.skyblu.data.Repository
import com.skyblu.data.firestore.toUser
import com.skyblu.models.jump.User
import dagger.hilt.android.lifecycle.HiltViewModel
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
    repository: Repository
) : ViewModel() {

    //Data Sources
    private val workManager = repository.workManager.getWorkManager()
    private val datastore = repository.datastoreInterface
    private val authentication = repository.authenticationInterface
    val savedUsers = repository.savedUsersInterface
    private val readServer = repository.readServerInterface

    //App State
    val state by mutableStateOf(UniversalState())

    private suspend fun workLoop(){
        while (true) {

                datastore.getDatastore().data.map { preferences ->
                    state.updateWorkUUID.value = preferences[PreferenceKeys.UPDATE_USER_WORK]
                    state.updateJumpUUID.value = preferences[PreferenceKeys.UPDATE_JUMP_WORK]
                    state.deleteJumpUUID.value = preferences[PreferenceKeys.DELETE_JUMP_WORK]
                    state.uploadJumpUUID.value = preferences[PreferenceKeys.UPLOAD_JUMP_WORK]
                }.collect()

            delay(2000)
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

        viewModelScope.launch { workLoop() }


        viewModelScope.launch {
            authentication.signedInStatus.collectLatest { user ->
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
        Timber.d("Observing $workName")
        workManager.getWorkInfosForUniqueWorkLiveData(workName)
            .observeForever() { workInfos ->
                Timber.d("Observing Forever $workName")
                val work = workInfos.find { it.id.toString() == UUID.value.toString() }
                if (work != null) {
                    when (work.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            Timber.d("$workName Success!")
                            state.message.value = successMessage
                            state.messageImportance.value = Alert.SUCCESS
                            viewModelScope.launch {
                                delay(2000)
                                state.message.value = ""
                            }
                        }
                        WorkInfo.State.FAILED -> {
                            Timber.d("$workName Failed!")
                            state.message.value = failedMessage
                            state.messageImportance.value = Alert.ERROR
                        }
                        WorkInfo.State.ENQUEUED -> {
                            Timber.d("$workName Queued!")
                            state.message.value = enqueuedMessage
                            state.messageImportance.value = Alert.WARNING
                        }
                        else -> {
                            Timber.d("$workName ${work.state.name}!")
                            state.message.value = ""
                        }
                    }
                }
            }
    }

}