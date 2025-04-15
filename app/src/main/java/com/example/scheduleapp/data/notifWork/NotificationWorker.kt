package com.example.scheduleapp.data.notifWork

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.scheduleapp.domain.model.ScheduleEvent
import com.example.scheduleapp.domain.repository.ScheduleRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.hours

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val repository: ScheduleRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val now = Clock.System.now()
        val events = repository.getEventsForNotification(now..now.plus(1.hours))

        events.forEach { event ->
            showNotification(event)
        }

        return Result.success()
    }



    private fun showNotification(event: ScheduleEvent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        createNotificationChannel()

        val notification = NotificationCompat.Builder(context, "schedule_channel")
            .setContentTitle(event.title)
            .setContentText(event.description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()


        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.notify(event.id.hashCode(), notification)

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "schedule_channel",
                "Schedule Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for schedule reminders"
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 100)
            }

            val manager = context.getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(channel)
        }
    }
}