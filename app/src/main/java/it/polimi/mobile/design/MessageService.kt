package it.polimi.mobile.design

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import it.polimi.mobile.design.entities.Workout


class MessageService : WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)


        //If the message’s path equals "/my_path"...//
        if (messageEvent.path == "/my_path") {

//...retrieve the message//
            val message = String(messageEvent.data)
            val workout=Workout(messageEvent.data.toString())
            val messageIntent = Intent()
            messageIntent.action = Intent.ACTION_SEND
            messageIntent.putExtra("message", message)
            //messageIntent.putExtra("workout",workout)

//Broadcast the received Data Layer messages locally//
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent)
        } else {
            super.onMessageReceived(messageEvent)
        }

    }
}