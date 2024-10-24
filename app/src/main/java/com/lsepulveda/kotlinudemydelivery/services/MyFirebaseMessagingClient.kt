package com.lsepulveda.kotlinudemydelivery.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lsepulveda.kotlinudemydelivery.channel.NotificationHelper

class MyFirebaseMessagingClient: FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    // aca entra cuando se recibe una notificacion
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val data = remoteMessage.data
        // parametros que debe enviar el cliente
        val title = data["title"]
        val body = data["body"]
        val idNotification = data["id_notification"]

        if (!title.isNullOrBlank() && !body.isNullOrBlank() && !idNotification.isNullOrBlank()) {
            showNotification(title, body, idNotification)
        }
    }

    private fun showNotification(title: String, body: String, idNotification: String) {
        val helper = NotificationHelper(baseContext)
        val builder = helper.getNotification(title, body)
        val id = idNotification.toInt()
        helper.getManager().notify(id, builder.build()) // metodo que muestra notificacion en el dispositivo
    }

}