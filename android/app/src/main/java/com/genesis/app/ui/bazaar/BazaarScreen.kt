package com.genesis.app.ui.bazaar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.genesis.app.data.model.CreationResponse
import com.genesis.app.util.Resource

@Composable
fun BazaarScreen(
    viewModel: BazaarViewModel = hiltViewModel()
) {
    val assetsState by viewModel.assetsState.collectAsState()
    var selectedAsset by remember { mutableStateOf<CreationResponse?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "The Bazaar", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        when (assetsState) {
            is Resource.Success -> {
                BazaarGrid(
                    assets = assetsState.data ?: emptyList(),
                    onAssetClick = { selectedAsset = it }
                )
            }
            // ... (loading and error states)
        }
    }

    if (selectedAsset != null) {
        AssetDetailDialog(
            asset = selectedAsset!!,
            onDismiss = { selectedAsset = null }
        )
    }
}

@Composable
fun BazaarGrid(assets: List<CreationResponse>, onAssetClick: (CreationResponse) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(assets) { asset ->
            AssetCard(asset = asset, onClick = { onAssetClick(asset) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetDetailDialog(asset: CreationResponse, onDismiss: () -> Unit) {
    var offerPrice by remember { mutableStateOf("") }
    var sellPrice by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Asset Details") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // 3D Model Rendering (simplified for brevity, use existing placeholder)
                AsyncImage(model = asset.previewImageUrl, contentDescription = null, modifier = Modifier.height(150.dp).fillMaxWidth())
                
                Text(text = "Attributes:", style = MaterialTheme.typography.titleSmall)
                asset.attributes?.forEach { (k, v) ->
                    Text(text = "$k: $v", style = MaterialTheme.typography.bodySmall)
                }

                Divider()

                if (asset.attributes?.get("is_owner") == "true") {
                    OutlinedTextField(
                        value = sellPrice,
                        onValueChange = { sellPrice = it },
                        label = { Text("Set Sale Price (StarCoin)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(text = "Current Price: ${asset.attributes?.get("price") ?: "Not for sale"}")
                    OutlinedTextField(
                        value = offerPrice,
                        onValueChange = { offerPrice = it },
                        label = { Text("Your Offer (Escrow)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            if (asset.attributes?.get("is_owner") == "true") {
                Button(onClick = { /* Call viewModel.listAsset */ onDismiss() }) {
                    Text("List for Sale")
                }
            } else {
                Button(onClick = { /* Call viewModel.makeOffer */ onDismiss() }) {
                    Text("Pay & Request Buy")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun AssetCard(asset: CreationResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
    ) {
        Column {
            AsyncImage(
                model = asset.previewImageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentScale = ContentScale.Crop
            )
            Text(
                text = "Asset #${asset.assetId}",
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}
