package com.timecapsule.app.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.timecapsule.app.data.model.Capsule
import com.timecapsule.app.viewmodel.CapsuleViewModel
import com.timecapsule.app.utils.AlarmHelper
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCapsuleScreen(navController: NavController) {

    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    var selectedType by remember { mutableStateOf("Personal") }
    val types = listOf("Personal", "Motivation", "Reminder")
    var selectedMillis by remember { mutableLongStateOf(0L) }
    var selectedDate by remember { mutableStateOf("Select Date") }
    var selectedTime by remember { mutableStateOf("Select Time") }
    
    var selectedAccessType by remember { mutableStateOf("Personal") }
    val accessTypes = listOf("Personal", "Family", "Friends", "Others")
    var unlockKey by remember { mutableStateOf("") }
    var sharedEmails by remember { mutableStateOf("") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val viewModel: CapsuleViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Capsule", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A2E),
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        },
        containerColor = Color(0xFF16213E)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = "Secure your memory",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Title & Message Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFE94560),
                            unfocusedBorderColor = Color.DarkGray
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Your Message", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth().height(150.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFE94560),
                            unfocusedBorderColor = Color.DarkGray
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Configuration Options
            Text(text = "Settings", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            // Type & Access Type
            var expandedType by remember { mutableStateOf(false) }
            var expandedAccess by remember { mutableStateOf(false) }

            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { expandedType = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(selectedType)
                    }
                    DropdownMenu(expanded = expandedType, onDismissRequest = { expandedType = false }) {
                        types.forEach { type ->
                            DropdownMenuItem(text = { Text(type) }, onClick = { selectedType = type; expandedType = false })
                        }
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { expandedAccess = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(selectedAccessType)
                    }
                    DropdownMenu(expanded = expandedAccess, onDismissRequest = { expandedAccess = false }) {
                        accessTypes.forEach { access ->
                            DropdownMenuItem(text = { Text(access) }, onClick = { selectedAccessType = access; expandedAccess = false })
                        }
                    }
                }
            }

            if (selectedAccessType != "Personal") {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = sharedEmails,
                    onValueChange = { sharedEmails = it },
                    label = { Text("Shared User Emails (comma separated)", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFE94560)
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = unlockKey,
                onValueChange = { unlockKey = it },
                label = { Text("Secret Unlock Key (Optional)", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFE94560)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Unlock Time
            Text(text = "Unlock Date & Time", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = {
                        DatePickerDialog(context, { _, y, m, d ->
                            selectedDate = "$d/${m+1}/$y"; calendar.set(y, m, d); selectedMillis = calendar.timeInMillis
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(selectedDate)
                }
                Spacer(modifier = Modifier.width(12.dp))
                OutlinedButton(
                    onClick = {
                        TimePickerDialog(context, { _, h, min ->
                            selectedTime = String.format(Locale.getDefault(), "%02d:%02d", h, min); calendar.set(Calendar.HOUR_OF_DAY, h); calendar.set(Calendar.MINUTE, min); selectedMillis = calendar.timeInMillis
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Icon(Icons.Default.Timer, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(selectedTime)
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    if (selectedMillis == 0L || title.isBlank()) return@Button
                    val capsule = Capsule(
                        id = "",
                        title = title,
                        message = message,
                        type = selectedType,
                        accessType = selectedAccessType,
                        unlockKey = unlockKey.ifBlank { null },
                        sharedUsers = if (selectedAccessType != "Personal" && sharedEmails.isNotBlank()) {
                            sharedEmails.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        } else emptyList(),
                        openTime = selectedMillis
                    )
                    viewModel.addCapsule(capsule)
                    AlarmHelper.scheduleAlarm(context, selectedMillis, "Time's Up! 🎁", "Your capsule '$title' is ready!")
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE94560))
            ) {
                Text("Create Capsule", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}