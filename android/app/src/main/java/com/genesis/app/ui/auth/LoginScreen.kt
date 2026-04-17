package com.genesis.app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.genesis.app.util.Resource

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onLoginSuccess(uiState.email.split("@")[0])
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Genesis", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.primary)
        Text(text = "Enter the Blank Canvas", style = MaterialTheme.typography.bodyLarge)
        
        Spacer(modifier = Modifier.height(48.dp))
        
        OutlinedTextField(
            value = uiState.email,
            onValueChange = { viewModel.onEvent(LoginUiEvent.OnEmailChanged(it)) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
            shape = MaterialTheme.shapes.medium
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedTextField(
            value = uiState.password,
            onValueChange = { viewModel.onEvent(LoginUiEvent.OnPasswordChanged(it)) },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
            shape = MaterialTheme.shapes.medium
        )
        
        if (uiState.error != null) {
            Text(
                text = uiState.error!!, 
                color = MaterialTheme.colorScheme.error, 
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { viewModel.onEvent(LoginUiEvent.OnLoginClicked) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = !uiState.isLoading,
            shape = MaterialTheme.shapes.large
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Login", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
