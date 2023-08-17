package com.ixidev.mobile.ui

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResponseListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.google.common.collect.ImmutableList
import com.ixidev.data.logger.EVENT_EXTRACTS_DEVICE
import com.ixidev.data.logger.EVENT_OPEN_APP
import com.ixidev.data.logger.IAppLogger
import com.ixidev.mobile.R
import com.ixidev.mobile.ui.common.SaveSharedPreference
import com.ixidev.mobile.ui.main.MobileMainActivity

class SubscribeActivity : AppCompatActivity() {

    private var billingClient: BillingClient? = null
    lateinit var logger: IAppLogger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscribe)

        billingClient = BillingClient.newBuilder(this)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        checkUserSubscription()

    }

    private fun subscribeBilling() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
                        .setProductList(
                            ImmutableList.of(
                                QueryProductDetailsParams.Product.newBuilder()
                                    .setProductId("aylik_reklam")
                                    .setProductType(BillingClient.ProductType.SUBS)
                                    .build()
                            )
                        )
                        .build()

                    billingClient?.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
                        // check billingResult
                        // process returned productDetailsList
                        for (productDetails in productDetailsList) {
                            val selectedOfferToken =
                                productDetails.subscriptionOfferDetails?.get(0)?.offerToken
                            selectedOfferToken?.let {
                                val productDetailsParamsList = listOf(
                                    BillingFlowParams.ProductDetailsParams.newBuilder()
                                        // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                                        .setProductDetails(productDetails)
                                        // to get an offer token, call ProductDetails.subscriptionOfferDetails()
                                        // for a list of offers that are available to the user
                                        .setOfferToken(it)
                                        .build()
                                )

                                val billingFlowParams = BillingFlowParams.newBuilder()
                                    .setProductDetailsParamsList(productDetailsParamsList)
                                    .build()

                                // Launch the billing flow
                                billingClient?.launchBillingFlow(
                                    this@SubscribeActivity,
                                    billingFlowParams
                                )
                            }
                        }
                    }

                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    private fun checkUserSubscription(){
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryPurchases()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })

    }

    private val acknowledgePurchaseResponseListener: AcknowledgePurchaseResponseListener = AcknowledgePurchaseResponseListener { billingResult ->
        if(billingResult.responseCode == BillingClient.BillingResponseCode.OK){
            //Toast.makeText(this, "Subscribed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        // Verify the purchase.
        // Ensure entitlement was not already granted for this purchaseToken.
        // Grant entitlement to the user.

        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        val listener = ConsumeResponseListener { billingResult, purchaseToken ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // Handle the success of the consume operation.
            }
        }

        billingClient?.consumeAsync(consumeParams, listener)
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient?.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener)
                SaveSharedPreference.setAdsPref(this, false)
                Toast.makeText(this, "Subscribed", Toast.LENGTH_SHORT).show()
                startActivity(
                    Intent(
                        this,
                        MobileMainActivity::class.java
                    )
                )
                finish()
            }else{
                SaveSharedPreference.setAdsPref(this, false)
                Toast.makeText(this, "Already Subscribed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun queryPurchases() {
        billingClient?.let { bill ->
            if (!bill.isReady) {
                Log.e("TAG", "queryPurchases: BillingClient is not ready")
            }
        }

        // Query for existing subscription products that have been purchased.
        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()
        ) { billingResult, purchaseList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (!purchaseList.isNullOrEmpty()) {
                    Log.e("TAG", "queryPurchases: list is not empty")
                    for (purchase in purchaseList) {
                        // Check purchase state and take appropriate actions
                        when (purchase.purchaseState) {
                            Purchase.PurchaseState.PURCHASED -> {
                                // The user has an active subscription
                                // Handle accordingly
                                Log.e("TAG", "queryPurchases: PURCHASED")
                                SaveSharedPreference.setAdsPref(this, false)
                            }
                            Purchase.PurchaseState.PENDING -> {
                                // The subscription was canceled but is still active until the end of the current billing cycle
                                // Handle accordingly
                                Log.e("TAG", "queryPurchases: PENDING")
                                SaveSharedPreference.setAdsPref(this, true)
                            }
                            Purchase.PurchaseState.UNSPECIFIED_STATE -> {
                                // The subscription has expired
                                // Handle accordingly
                                Log.e("TAG", "queryPurchases: UNSPECIFIED_STATE")
                                SaveSharedPreference.setAdsPref(this, true)
                            }
                        }
                    }
                } else {
                    // this will tell us if user have no package
                    Log.e("TAG", "queryPurchases: BillingClient is not ready no nono")
                    SaveSharedPreference.setAdsPref(this, true)
                }

            } else {
                Log.e("TAG", billingResult.debugMessage)
            }
        }
    }

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        // To be implemented in a later section.
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            // Handle if already purchased.
            Toast.makeText(this, "Already Subscribed", Toast.LENGTH_SHORT).show()
            SaveSharedPreference.setAdsPref(this, false)
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED) {
            // Handle if USER_CANCELED.
            Toast.makeText(this, "Featured Not Supported", Toast.LENGTH_SHORT).show()
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle if USER_CANCELED.
        } else {
            // Handle any other error codes.
            Toast.makeText(this, "Error: ${billingResult.debugMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun subscribe(@Suppress("UNUSED_PARAMETER") view: View){
        subscribeBilling()
    }

    fun close(@Suppress("UNUSED_PARAMETER") view: View){
//        logger.logEvent(
//            EVENT_OPEN_APP,
//            bundleOf(
//                EVENT_EXTRACTS_DEVICE to "mobile"
//            )
//        )
        startActivity(
            Intent(
                this,
                MobileMainActivity::class.java
            )
        )
        finish()
    }

    fun openPrivacy(@Suppress("UNUSED_PARAMETER") view: View) {
        var url = getString(R.string.privacy_policy_url)
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://$url"
        val browserIntent = Intent(Intent.ACTION_VIEW, url.toUri())
        startActivity(browserIntent)
    }
}