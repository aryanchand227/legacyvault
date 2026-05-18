package com.timecapsule.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timecapsule.app.viewmodel.CapsuleViewModel
import com.timecapsule.app.data.model.Capsule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CapsuleDetailScreen(
    capsuleId: String,
    navController: androidx.navigation.NavController
) {

    val viewModel: CapsuleViewModel = viewModel()
    val capsules by viewModel.capsules.collectAsState()
    val capsule = remember(capsules, capsuleId) { capsules.find { it.id == capsuleId } }
    var unlockedByKey by remember { mutableStateOf(false) }
    var inputKey by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Capsule Details", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            capsule?.let {
                Text(it.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                if (it.isOpened || unlockedByKey || it.unlockKey == null) {
                    Text(it.message, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = {
                        navController.navigate("emotion/${it.id}")
                    }) {
                        Text("Rate Your Emotion")
                    }
                } else {
                    Text("This capsule is locked.", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = inputKey,
                        onValueChange = { inputKey = it },
                        label = { Text("Enter Secret Key") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        if (inputKey == it.unlockKey) {
                            unlockedByKey = true
                            errorMessage = null
                        } else {
                            errorMessage = "Incorrect key."
                        }
                    }) {
                        Text("Unlock")
                    }
                    errorMessage?.let { msg ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(msg, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}