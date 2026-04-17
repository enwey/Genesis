package com.genesis.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.genesis.app.data.model.FeedItem

@Entity(tableName = "feed_items")
data class FeedEntity(
    @PrimaryKey val id: String,
    val creatorName: String,
    val assetImageUrl: String,
    val description: String,
    val likeCount: Int,
    val isLiked: Boolean,
    val isFavorited: Boolean,
    val timestamp: Long
)

fun FeedEntity.toDomainModel(): FeedItem {
    return FeedItem(
        id = id,
        creatorName = creatorName,
        assetImageUrl = assetImageUrl,
        description = description,
        likeCount = likeCount,
        isLiked = isLiked,
        isFavorited = isFavorited,
        timestamp = timestamp
    )
}

fun FeedItem.toEntity(): FeedEntity {
    return FeedEntity(
        id = id,
        creatorName = creatorName,
        assetImageUrl = assetImageUrl,
        description = description,
        likeCount = likeCount,
        isLiked = isLiked,
        isFavorited = isFavorited,
        timestamp = timestamp
    )
}
