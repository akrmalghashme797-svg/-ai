package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.ChatRepository
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Persistent SQLite Room backed Repository initialization
        val repository = ChatRepository(this)

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "splash"
                ) {
                    // 1. SplashScreen Stage (3s Bismillah + Calligraphed Fatihah)
                    composable("splash") {
                        SplashScreen(
                            onNavigateNext = {
                                // Direct to chat list if already authenticated (simulated caching)
                                val currentPhone = repository.currentUserPhoneNumber.value
                                if (currentPhone != null && currentPhone.isNotBlank()) {
                                    navController.navigate("home") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                } else {
                                    navController.navigate("login") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            }
                        )
                    }

                    // 2. OTP Phone Validation Step
                    composable("login") {
                        LoginScreen(
                            repository = repository,
                            onLoginSuccess = {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }

                    // 3. Main Feeds (All/Groups/Channels/Archive tabs + Game triggers)
                    composable("home") {
                        ChatListScreen(
                            repository = repository,
                            onChatClick = { chatId ->
                                navController.navigate("chat/$chatId")
                            },
                            onNavigateSettings = {
                                navController.navigate("settings")
                            },
                            onNavigateGame = {
                                navController.navigate("game")
                            }
                        )
                    }

                    // 4. Secure Individual/Channel messaging room
                    composable("chat/{chatId}") { backStackEntry ->
                        val chatId = backStackEntry.arguments?.getString("chatId") ?: "chat_1"
                        ChatScreen(
                            repository = repository,
                            chatId = chatId,
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    // 5. Military encryption settings, book library, resistance stickers
                    composable("settings") {
                        SettingsScreen(
                            repository = repository,
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    // 6. Playable offline 2D arcade game
                    composable("game") {
                        SamedGameScreen(
                            repository = repository,
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}

