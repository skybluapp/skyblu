package com.skyblu.messagingservice


import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.skyblu.data.authentication.AuthenticationInterface
import com.skyblu.data.firestore.WriteServerInterface
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MessagingService @Inject constructor(

): FirebaseMessagingService()  {

    init {
        Timber.d("Token")
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        registerToken(token = token)
    }

    private fun registerToken(token : String){

    }
}