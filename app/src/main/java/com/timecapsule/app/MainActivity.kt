package com.timecapsule.app

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.timecapsule.app.data.datastore.PreferenceManager
import com.timecapsule.app.navigation.AppNavigation
import com.timecapsule.app.ui.theme.AppTheme
import com.timecapsule.app.utils.CapsuleJobService
import com.timecapsule.app.utils.NotificationHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefManager = PreferenceManager(this)

        NotificationHelper.createChannel(this)

        setContent {
            LaunchedEffect(Unit) {
                val jobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
                val jobInfo = JobInfo.Builder(
                    1,
                    ComponentName(this@MainActivity, CapsuleJobService::class.java)
                )
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPeriodic(15 * 60 * 1000)
                    .build()
                jobScheduler.schedule(jobInfo)
            }

            val darkMode by prefManager.darkModeFlow.collectAsState(initial = false)
            AppTheme(darkTheme = darkMode) {
                AppNavigation()
            }
        }
    }
}
