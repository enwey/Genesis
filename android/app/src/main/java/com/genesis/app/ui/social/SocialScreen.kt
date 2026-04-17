package com.genesis.app.ui.social

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.genesis.app.data.model.FeedItem
import com.genesis.app.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialScreen(
    viewModel: SocialViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Social Feed") })
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
            if (uiState.isLoading && uiState.feedItems.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text(text = uiState.error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
            } else {
                FeedList(
                    items = uiState.feedItems,
                    onLike = { viewModel.onEvent(SocialUiEvent.OnLikeClicked(it)) },
                    onFavorite = { viewModel.onEvent(SocialUiEvent.OnFavoriteClicked(it)) }
                )
            }
        }
    }
}

@Composable
fun FeedList(
    items: List<FeedItem>,
    onLike: (String) -> Unit,
    onFavorite: (String) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items) { item ->
            FeedCard(item, onLike, onFavorite)
        }
    }
}

@Composable
fun FeedCard(
    item: FeedItem,
    onLike: (String) -> Unit,
    onFavorite: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = item.creatorName, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "2h ago", style = MaterialTheme.typography.labelSmall)
            }

            AsyncImage(
                model = item.assetImageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            Text(
                text = item.description,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                IconButton(onClick = { onLike(item.id) }) {
                    Icon(
                        imageVector = if (item.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (item.isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "${item.likeCount}",
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(onClick = { onFavorite(item.id) }) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Favorite",
                        tint = if (item.isFavorited) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
