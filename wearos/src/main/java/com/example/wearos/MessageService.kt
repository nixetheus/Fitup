package com.example.wearos

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService


class MessageService : WearableListenerService() {
    private var messageIntent: Intent ?=null

    override fun onMessageReceived(messageEvent: MessageEvent) {
        when (messageEvent.path) {
            "/workout" -> {

    //...retrieve the message//
                val message = String(messageEvent.data)
                messageIntent = Intent()
                messageIntent!!.action = Intent.ACTION_SEND
                messageIntent!!.putExtra("workout", message)

    //Broadcast the received Data Layer messages locally//
                LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent!!)
            }
            "/exercise" -> {
                val message = String(messageEvent.data)
                messageIntent = Intent()
                messageIntent!!.action = Intent.ACTION_SEND
                messageIntent!!.putExtra("exercise", message)
                LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent!!)

            }
            "/finish" -> {
                val next = String(messageEvent.data)
                messageIntent = Intent()
                messageIntent!!.action = Intent.ACTION_SEND
                messageIntent!!.putExtra("finish", next)
                LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent!!)
            }
            "/ready" -> {
                val next = String(messageEvent.data)
                messageIntent=Intent()
                messageIntent!!.action = Intent.ACTION_SEND
                messageIntent!!.putExtra("ready", next)
                LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent!!)}
            "/start" -> {
                val next = String(messageEvent.data)
                messageIntent = Intent()
                messageIntent!!.action = Intent.ACTION_SEND
                messageIntent!!.putExtra("start", next)
                LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent!!)
            }
            "/next" -> {
                val next = String(messageEvent.data)
                messageIntent=Intent()
                messageIntent!!.action = Intent.ACTION_SEND
                messageIntent!!.putExtra("next", next)
                LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent!!)}
            else -> {
                super.onMessageReceived(messageEvent)
            }
        }

    }

}