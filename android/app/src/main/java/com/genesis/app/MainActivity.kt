package com.genesis.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.genesis.app.ui.auth.LoginScreen
import com.genesis.app.ui.bazaar.BazaarScreen
import com.genesis.app.ui.creation.CreationEngineScreen
import com.genesis.app.ui.social.SocialScreen
import com.genesis.app.ui.theme.GenesisTheme
import com.genesis.app.ui.wallet.WalletScreen
import dagger.hilt.android.AndroidEntryPoint

import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.genesis.app.data.websocket.SocialWebSocketClient
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var webSocketClient: SocialWebSocketClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GenesisTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                var isLoggedIn by remember { mutableStateOf(false) }

                // 監聽 WebSocket
                LaunchedEffect(isLoggedIn) {
                    if (isLoggedIn) {
                        // 生產環境應從 TokenManager 獲取真實 Token
                        webSocketClient.connect("dummy_token")
                        webSocketClient.messages.collectLatest { message ->
                            snackbarHostState.showSnackbar(message)
                        }
                    }
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { padding ->
                    Surface(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        if (!isLoggedIn) {
                            LoginScreen(onLoginSuccess = { isLoggedIn = true })
                        } else {
                            MainContent()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainContent() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(
        TabItem("Create", Icons.Default.Add),
        TabItem("Bazaar", Icons.Default.ShoppingCart),
        TabItem("Social", Icons.Default.Person),
        TabItem("Wallet", Icons.Default.List)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { it } + fadeIn() togetherWith
                                slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                                slideOutHorizontally { it } + fadeOut()
                    }.using(
                        SizeTransform(clip = false)
                    )
                }, label = ""
            ) { targetTab ->
                when (targetTab) {
                    0 -> CreationEngineScreen()
                    1 -> BazaarScreen()
                    2 -> SocialScreen()
                    3 -> WalletScreen()
                }
            }
        }
    }
}

data class TabItem(val title: String, val icon: ImageVector)

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Welcome to $name!",
        modifier = modifier
    )
}
