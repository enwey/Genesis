package com.genesis.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.genesis.app.data.model.CreationResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "last_creations")
data class CreationEntity(
    @PrimaryKey val assetId: String,
    val modelUrl: String,
    val previewImageUrl: String,
    val attributesJson: String // Room 不直接支持 Map，存儲為 JSON
)

fun CreationResponse.toEntity(): CreationEntity {
    return CreationEntity(
        assetId = assetId,
        modelUrl = modelUrl,
        previewImageUrl = previewImageUrl,
        attributesJson = Gson().toJson(attributes)
    )
}

fun CreationEntity.toDomainModel(): CreationResponse {
    val type = object : TypeToken<Map<String, String>>() {}.type
    return CreationResponse(
        assetId = assetId,
        modelUrl = modelUrl,
        previewImageUrl = previewImageUrl,
        attributes = Gson().fromJson(attributesJson, type)
    )
}
