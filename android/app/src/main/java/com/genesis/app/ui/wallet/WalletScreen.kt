package com.genesis.app.ui.wallet

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.genesis.app.util.Resource

@Composable
fun WalletScreen(
    viewModel: WalletViewModel = hiltViewModel()
) {
    val balanceState by viewModel.balanceState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "My Wallet", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        when (balanceState) {
            is Resource.Success -> {
                BalanceCard(
                    title = "Mana",
                    amount = balanceState.data?.mana ?: 0,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                BalanceCard(
                    title = "StarCoin",
                    amount = balanceState.data?.starCoin ?: 0,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            is Resource.Error -> {
                Text(text = balanceState.message ?: "Error", color = MaterialTheme.colorScheme.error)
            }
            is Resource.Loading -> {
                CircularProgressIndicator()
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
