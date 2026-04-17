package com.genesis.app.data.api

import com.genesis.app.data.model.CreationRequest
import com.genesis.app.data.model.CreationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GenesisApiService {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/v1/auth/register")
    suspend fun register(@Body request: com.genesis.app.data.model.RegisterRequest): Response<com.genesis.app.data.model.LoginResponse>

    @POST("api/v1/creation/generate")
    suspend fun generate3DAsset(@Body request: CreationRequest): Response<CreationResponse>

    @retrofit2.http.GET("bazaar/assets")
    suspend fun getBazaarAssets(): Response<List<CreationResponse>>

    @retrofit2.http.GET("wallet/balance")
    suspend fun getWalletBalance(): Response<com.genesis.app.data.model.WalletResponse>

    @retrofit2.http.GET("social/feed")
    suspend fun getSocialFeed(): Response<List<com.genesis.app.data.model.FeedItem>>

    @retrofit2.http.POST("assets/{assetId}/like")
    suspend fun likeAsset(@retrofit2.http.Path("assetId") assetId: String): Response<Unit>

    @retrofit2.http.POST("api/v1/assets/{assetId}/favorite")
    suspend fun favoriteAsset(@retrofit2.http.Path("assetId") assetId: String): Response<Unit>

    @retrofit2.http.POST("api/v1/market/{assetId}/list")
    suspend fun listAsset(@retrofit2.http.Path("assetId") assetId: String, @retrofit2.http.Query("price") price: Int): Response<Unit>

    @retrofit2.http.POST("api/v1/market/{assetId}/offer")
    suspend fun makeOffer(@retrofit2.http.Path("assetId") assetId: String, @retrofit2.http.Query("bid_price") bidPrice: Int): Response<Unit>

    @retrofit2.http.POST("api/v1/market/offers/{offerId}/respond")
    suspend fun respondToOffer(@retrofit2.http.Path("offerId") offerId: String, @retrofit2.http.Query("accept") accept: Boolean): Response<Unit>
    }
