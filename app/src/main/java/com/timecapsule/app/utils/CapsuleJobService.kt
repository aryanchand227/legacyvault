package com.timecapsule.app.utils

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.timecapsule.app.data.model.repository.CapsuleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CapsuleJobService : JobService() {
    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private val repository = CapsuleRepository()
    private val auth = FirebaseAuth.getInstance()

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d("CapsuleJobService", "Background sync started")
        
        serviceScope.launch {
            val user = auth.currentUser
            if (user != null) {
                try {
                    // This is where background logic would go, 
                    // e.g., fetching new shared capsules and triggering notifications if not already done.
                    val owned = repository.getAllCapsulesForUser(user.uid)
                    val shared = user.email?.let { repository.getSharedCapsules(it) } ?: emptyList()
                    
                    Log.d("CapsuleJobService", "Synced ${owned.size + shared.size} capsules in background")
                } catch (e: Exception) {
                    Log.e("CapsuleJobService", "Background sync failed", e)
                }
            }
            jobFinished(params, false)
        }
        
        return true // Task is running in background
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d("CapsuleJobService", "Background sync stopped")
        return true // Reschedule if needed
    }
}