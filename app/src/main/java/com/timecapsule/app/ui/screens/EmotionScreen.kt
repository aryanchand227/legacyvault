package com.timecapsule.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timecapsule.app.viewmodel.CapsuleViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmotionScreen(capsuleId: String, navController: NavController) {

    val viewModel: CapsuleViewModel = viewModel()
    var rating by remember { mutableStateOf(0) }
    var reflectionNotes by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf<String?>(null) }

    val emojis = listOf("😊", "😭", "🔥", "🤔", "🥺")
    val emojiLabels = listOf("Happy", "Emotional", "Motivated", "Thoughtful", "Touched")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("How do you feel?", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A2E),
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Reflect on this moment",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Custom Rating Bar (Stars)
            Row {
                for (i in 1..5) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (i <= rating) Color(0xFFFFD700) else Color.DarkGray,
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { rating = i }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Emoji Reactions
            Text(text = "Choose an emoji reaction", color = Color.LightGray, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                emojis.forEachIndexed { index, emoji ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(if (selectedEmoji == emoji) Color(0xFFE94560) else Color(0xFF0F3460))
                                .clickable { selectedEmoji = emoji },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 28.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = emojiLabels[index], fontSize = 12.sp, color = Color.LightGray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Reflection Notes
            OutlinedTextField(
                value = reflectionNotes,
                onValueChange = { reflectionNotes = it },
                label = { Text("Reflection Notes", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF0F3460),
                    unfocusedBorderColor = Color.DarkGray
                ),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    viewModel.updateCapsuleEmotion(capsuleId, rating)
                    // In a real app, we'd also save the emoji and reflectionNotes to Firestore
                    navController.popBackStack("main", inclusive = false)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE94560)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Reflection", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}