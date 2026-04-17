package com.genesis.app.ui.wallet

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.genesis.app.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    viewModel: WalletViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("My Wallet") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.isLoading && uiState.wallet == null) {
                CircularProgressIndicator(modifier = Modifier.padding(32.dp))
            } else if (uiState.error != null) {
                Text(text = uiState.error!!, color = MaterialTheme.colorScheme.error)
            } else {
                BalanceCard(
                    title = "Mana",
                    amount = uiState.wallet?.mana ?: 0,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                BalanceCard(
                    title = "StarCoin",
                    amount = uiState.wallet?.starCoin ?: 0,
                    color = MaterialTheme.colorScheme.secondary
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                Text("Store", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { viewModel.onEvent(WalletUiEvent.OnBuyManaClicked(1000)) },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Buy 1000 Mana ($9.99)")
                }
            }
        }
    }
}

@Composable
fun BalanceCard(title: String, amount: Int, color: androidx.compose.ui.graphics.Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(text = amount.toString(), style = MaterialTheme.typography.headlineLarge, color = color)
            }
        }
    }
}
