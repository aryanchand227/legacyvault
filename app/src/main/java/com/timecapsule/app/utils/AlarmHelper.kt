package com.timecapsule.app.utils



import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

object AlarmHelper {

    fun scheduleAlarm(
        context: Context,
        time: Long,
        title: String,
        message: String,
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Check for exact alarm permission on Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // If we can't schedule exact, fallback to inexact or just log
                Log.e("AlarmHelper", "Cannot schedule exact alarms. Permission missing.")
                alarmManager.set(AlarmManager.RTC_WAKEUP, time, getPendingIntent(context, time, title, message))
                return
            }
        }

        try {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                time,
                getPendingIntent(context, time, title, message)
            )
        } catch (e: SecurityException) {
            Log.e("AlarmHelper", "SecurityException while scheduling exact alarm", e)
            // Fallback to non-exact alarm
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, getPendingIntent(context, time, title, message))
        }
    }

    private fun getPendingIntent(context: Context, time: Long, title: String, message: String): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
        }

        return PendingIntent.getBroadcast(
            context,
            time.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}