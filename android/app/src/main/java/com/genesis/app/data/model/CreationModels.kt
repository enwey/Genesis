package com.genesis.app.data.model

data class CreationRequest(
    val prompt: String,
    val style: String = "low-poly"
)

data class CreationResponse(
    val assetId: String,
    val modelUrl: String,
    val previewImageUrl: String,
    val attributes: Map<String, String>? = null // 例如: {"power": "85", "element": "fire"}
)
