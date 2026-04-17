package com.genesis.app.data.model

data class FeedItem(
    val id: String,
    val creatorName: String,
    val assetImageUrl: String,
    val description: String,
    val likeCount: Int,
    val isLiked: Boolean = false,
    val isFavorited: Boolean = false,
    val timestamp: Long
)
