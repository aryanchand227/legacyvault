package com.timecapsule.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.timecapsule.app.data.datastore.PreferenceManager
import kotlinx.coroutines.launch

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    object Create : BottomNavItem("create_nav", Icons.Default.Add, "Create")
    object Vault : BottomNavItem("vault", Icons.Default.Storage, "Vault")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    var selectedItem by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Home) }
    
    val context = LocalContext.current
    val prefManager = remember { PreferenceManager(context) }
    val scope = rememberCoroutineScope()
    val darkMode by prefManager.darkModeFlow.collectAsState(initial = false)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "LegacyVault",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A2E)
                ),
                actions = {
                    // Quick Dark Mode Toggle
                    IconButton(onClick = {
                        scope.launch {
                            prefManager.setDarkMode(!darkMode)
                        }
                    }) {
                        Icon(
                            imageVector = if (darkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = Color.White
                        )
                    }
                    
                    // Profile Button (Top Right)
                    IconButton(onClick = {
                        navController.navigate("profile")
                    }) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFFE94560), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Settings Button (Gear)
                    IconButton(onClick = {
                        navController.navigate("settings")
                    }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF1A1A2E),
                contentColor = Color.White
            ) {
                val items = listOf(
                    BottomNavItem.Home,
                    BottomNavItem.Vault,
                    BottomNavItem.Create
                )
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label, tint = if (selectedItem == item) Color(0xFFE94560) else Color.Gray) },
                        label = { Text(item.label, color = if (selectedItem == item) Color.White else Color.Gray) },
                        selected = selectedItem == item,
                        onClick = { 
                            if (item == BottomNavItem.Create) {
                                navController.navigate("create")
                            } else {
                                selectedItem = item 
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color(0xFFE94560).copy(alpha = 0.2f)
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFF16213E))
        ) {
            when (selectedItem) {
                is BottomNavItem.Home -> HomeScreen(navController)
                is BottomNavItem.Vault -> TabScreen(navController)
                else -> HomeScreen(navController)
            }
        }
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to LegacyVault",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Hello, ${user?.displayName ?: user?.email?.split("@")?.get(0) ?: "User"}",
            fontSize = 18.sp,
            color = Color.LightGray
        )
        Spacer(modifier = Modifier.height(36.dp))
        Card(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFFE94560), modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Your digital legacy is fully secured",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Create time-locked memory capsules and share them with the people you love. They will unlock only at your designated time.",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}