package com.timecapsule.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timecapsule.app.viewmodel.CapsuleViewModel
import com.timecapsule.app.data.model.Capsule
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LockedScreen() {

    val viewModel: CapsuleViewModel = viewModel()
    val capsules by viewModel.capsules.collectAsState()
    val formatter = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }

    val currentUserUid = remember { com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid }
    val lockedCapsules = remember(capsules, currentUserUid) {
        capsules.filter { !it.isOpened && it.ownerId == currentUserUid }
    }

    if (lockedCapsules.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No locked capsules", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(lockedCapsules) { capsule ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E))
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(Color(0xFF0F3460), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = "Locked",
                                    tint = Color(0xFFE94560),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = capsule.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFE94560).copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = capsule.type,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            fontSize = 11.sp,
                                            color = Color(0xFFE94560),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Unlocks: ${formatter.format(Date(capsule.openTime))}",
                                    fontSize = 13.sp,
                                    color = Color.LightGray
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = "Time left",
                                        modifier = Modifier.size(16.dp),
                                        tint = Color(0xFFE94560)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    LiveTimer(openTime = capsule.openTime)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LiveTimer(openTime: Long) {
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = System.currentTimeMillis()
        }
    }

    val diffMs = openTime - currentTime
    val text = if (diffMs <= 0) {
        "Ready to open!"
    } else {
        formatTimeDiff(diffMs)
    }

    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFE94560)
    )
}

fun formatTimeDiff(diffMs: Long): String {
    var diff = diffMs / 1000
    val seconds = diff % 60
    diff /= 60
    val minutes = diff % 60
    diff /= 60
    val hours = diff % 24
    diff /= 24
    val days = diff % 30
    diff /= 30
    val months = diff % 12
    val years = diff / 12

    return when {
        years > 0 -> String.format("%02dy %02dm left", years, months)
        months > 0 -> String.format("%02dm %02dd left", months, days)
        days > 0 -> String.format("%02dd %02dh left", days, hours)
        else -> String.format("%02d:%02d:%02d left", hours, minutes, seconds)
    }
}