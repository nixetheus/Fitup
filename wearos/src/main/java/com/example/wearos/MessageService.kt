package com.example.wearos

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService


class MessageService : WearableListenerService() {
    private var messageIntent: Intent ?=null

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path == "/my_path") {

//...retrieve the message//
            val message = String(messageEvent.data)
            messageIntent = Intent()
            messageIntent!!.action = Intent.ACTION_SEND
            messageIntent!!.putExtra("message", message)

//Broadcast the received Data Layer messages locally//
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent!!)
        }
        else if (messageEvent.path == "/exercise"){
            val message = String(messageEvent.data)
            messageIntent = Intent()
            messageIntent!!.action = Intent.ACTION_SEND
            messageIntent!!.putExtra("exercise", message)
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent!!)

        }
        else {
            super.onMessageReceived(messageEvent)
        }

    }

}