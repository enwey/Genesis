package com.genesis.app.data.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingManager @Inject constructor(
    @ApplicationContext private val context: Context
) : PurchasesUpdatedListener {

    private lateinit var billingClient: BillingClient
    
    // Hardcoded for demo/production testing (e.g., "mana_pack_1")
    private val MANA_PRODUCT_ID = "mana_pack_1000"

    private val _isBillingReady = MutableStateFlow(false)
    val isBillingReady: StateFlow<Boolean> = _isBillingReady

    init {
        setupBillingClient()
    }

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases()
            .build()
            
        startConnection()
    }

    private fun startConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d("BillingManager", "Billing setup successful")
                    _isBillingReady.value = true
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.d("BillingManager", "Billing service disconnected, retrying...")
                // Retry connection in production (with exponential backoff)
                _isBillingReady.value = false
            }
        })
    }

    fun purchaseMana(activity: Activity) {
        if (!billingClient.isReady) {
            startConnection()
            return
        }

        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(MANA_PRODUCT_ID)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                )
            ).build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
                val productDetails = productDetailsList[0]
                
                // Usually there is only one offer per INAPP product
                val productDetailsParamsList = listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build()
                )
                
                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build()
                    
                billingClient.launchBillingFlow(activity, billingFlowParams)
            } else {
                Log.e("BillingManager", "Failed to query products")
            }
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d("BillingManager", "User canceled purchase")
        } else {
            Log.e("BillingManager", "Purchase error: \${billingResult.debugMessage}")
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        // Send purchase token to backend API for validation and Mana crediting
        // e.g. apiService.verifyPurchase(purchase.purchaseToken)
        Log.d("BillingManager", "Purchase successful: \${purchase.purchaseToken}")
        
        // Acknowledge the purchase so it is not refunded automatically
        if (!purchase.isAcknowledged) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d("BillingManager", "Purchase acknowledged")
                }
            }
        }
    }
}
