package com.ltbth.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.EXTRA_NOTIFICATION_ID
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput

class MainActivity : AppCompatActivity() {
    companion object {
        private const val CHANNEL_ID = "Channel test"
        private var NOTIFICATION_ID = 0
        private const val name = "Channel test"
        private const val descriptionText = "test notification"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()

        val btnNotify = findViewById<Button>(R.id.btn_notify)
        btnNotify.setOnClickListener {
            // Create intent to open browser to search
            val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            // Create pending intent web search to setContentIntent
            val pendingIntent: PendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            // Button in notification
            val snoozeIntent = Intent(this, BroadcastReceiver::class.java).apply {
                val ACTION_SNOOZE = "Snooze"
                action = ACTION_SNOOZE
                putExtra(EXTRA_NOTIFICATION_ID, 0)
            }
            val snoozePendingIntent: PendingIntent =
                PendingIntent.getBroadcast(this, 0, snoozeIntent, 0)

            // Reply on notification
            val KEY_TEXT_REPLY = "key_text_reply"
            val replyLabel = "Type message"
            val remoteInput: RemoteInput = RemoteInput.Builder(KEY_TEXT_REPLY).run {
                setLabel(replyLabel)
                build()
            }
            val replyPendingIntent: PendingIntent =
                PendingIntent.getBroadcast(
                    applicationContext,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            val action: NotificationCompat.Action =
                NotificationCompat.Action.Builder(
                    R.drawable.ic_launcher_background,
                    "reply", replyPendingIntent
                )
                    .addRemoteInput(remoteInput)
                    .build()

            val bitmap =
                BitmapFactory.decodeResource(resources, R.drawable.me)
            // Build
            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.me)
                .setContentTitle("Notification")
                .setContentText("Click to open browser $NOTIFICATION_ID")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setColor(resources.getColor(R.color.purple_200, resources.newTheme()))
                    // Set long paragraph in content
//                .setStyle(
//                    NotificationCompat.BigTextStyle().bigText(resources.getText(R.string.content))
//                )
                    // Set large image in content
                .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                // Add button
                .setContentIntent(pendingIntent)
                .addAction(
                    R.drawable.ic_launcher_foreground, "Snooze",
                    snoozePendingIntent
                )
                // Add actions
                .addAction(action)
                .setAutoCancel(true)
            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                notify(NOTIFICATION_ID++, builder.build())
            }
        }
    }

    // Create notification channel
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}