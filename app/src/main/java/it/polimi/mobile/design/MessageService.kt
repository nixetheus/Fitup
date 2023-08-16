package it.polimi.mobile.design

import android.content.ContentValues
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import kotlin.math.log


class MessageService : WearableListenerService() {
    private var messageIntent: Intent ?=null

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)


        //If the message’s path equals "/my_path"...//
        when (messageEvent.path) {
            "/bpm" -> {

    //...retrieve the message//
                val message = String(messageEvent.data)
                messageIntent=Intent()
                messageIntent!!.action = Intent.ACTION_SEND
                messageIntent!!.putExtra("bpm", message)
                Log.d(ContentValues.TAG, "bpm received correctly")


    //Broadcast the received Data Layer messages locally//
                LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent!!)
            }
            "/start" -> {
                val start = String(messageEvent.data)
                messageIntent=Intent()
                messageIntent!!.action = Intent.ACTION_SEND
                messageIntent!!.putExtra("start", start)
                LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent!!)
                Log.d(ContentValues.TAG, "exercise started from wearOs")

            }
            "/requestExercise" -> {
                val request = String(messageEvent.data)
                messageIntent=Intent()
                messageIntent!!.action = Intent.ACTION_SEND
                messageIntent!!.putExtra("requestExercise", request)
                LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent!!)

            }
            "/next" -> {
                val next = String(messageEvent.data)
                messageIntent=Intent()
                messageIntent!!.action = Intent.ACTION_SEND
                messageIntent!!.putExtra("next", next)
                LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent!!)
                Log.d(ContentValues.TAG, "next exercise from wearOs")

            }
            else -> {
                super.onMessageReceived(messageEvent)
            }
        }

    }
}