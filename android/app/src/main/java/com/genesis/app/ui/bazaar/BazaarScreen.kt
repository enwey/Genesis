package com.genesis.app.ui.bazaar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.genesis.app.data.model.CreationResponse
import com.genesis.app.ui.creation.InfiniteGridBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BazaarScreen(
    viewModel: BazaarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.selectedAsset) {
        showBottomSheet = uiState.selectedAsset != null
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // 1. 環境背景保持一致
        InfiniteGridBackground(null)

        Scaffold(
            containerColor = Color.Transparent, // 讓背景網格透出來
            topBar = {
                CenterAlignedTopAppBar(
                    title = { 
                        Text(
                            "THE BAZAAR", 
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 4.sp
                            )
                        ) 
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White.copy(alpha = 0.8f)
                    )
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                if (uiState.isLoading && uiState.assets.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    // 橫屏下使用更多列 (4列) 的網格
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        contentPadding = PaddingValues(24.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        items(uiState.assets) { asset ->
                            GalleryAssetCard(
                                asset = asset, 
                                onClick = { viewModel.onEvent(BazaarUiEvent.OnAssetClicked(asset)) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showBottomSheet && uiState.selectedAsset != null) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.onEvent(BazaarUiEvent.OnDismissDialog) },
            sheetState = sheetState,
            containerColor = Color.White.copy(alpha = 0.9f)
        ) {
            BazaarDetailContent(uiState.selectedAsset!!)
        }
    }
}

@Composable
fun GalleryAssetCard(asset: CreationResponse, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.5f))
            .border(1.dp, Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            // 物品下方的「懸浮底座」
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(100.dp, 8.dp)
                    .offset(y = (-10).dp),
                color = Color.LightGray.copy(alpha = 0.3f),
                shape = CircleShape
            ) {}

            AsyncImage(
                model = asset.previewImageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentScale = ContentScale.Fit
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // 物品標籤風格
        Text(
            text = "ID: ${asset.assetId.takeLast(4)}",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
        
        val price = asset.attributes?.get("price") ?: "MINT"
        Text(
            text = "💎 $price",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00B4FF)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1B1F))
        ) {
            Text("VIEW", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BazaarDetailContent(asset: CreationResponse) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
            .navigationBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左側：大幅 3D 預覽/圖片
        Box(
            modifier = Modifier
                .weight(1f)
                .height(300.dp)
                .background(Color.FfmSurface, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(model = asset.previewImageUrl, contentDescription = null, modifier = Modifier.fillMaxSize())
        }
        
        Spacer(modifier = Modifier.width(32.dp))
        
        // 右側：詳細標籤與購買
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "GENESIS ARCHIVE", style = MaterialTheme.typography.labelMedium, color = Color(0xFF00B4FF))
            Text(
                text = "Object #${asset.assetId.takeLast(8)}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            
            Spacer(modifier = Modifier.height(16.dp))
            
            asset.attributes?.forEach { (k, v) ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text(text = "$k".uppercase(), modifier = Modifier.width(100.dp), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(text = "$v", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {}, 
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("ACQUIRE ASSET", fontWeight = FontWeight.Bold)
            }
        }
    }
}

private val Color.Companion.FfmSurface get() = Color(0xFFF8F8F8)
