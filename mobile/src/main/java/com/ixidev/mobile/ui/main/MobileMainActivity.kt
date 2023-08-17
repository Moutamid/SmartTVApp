package com.ixidev.mobile.ui.main

import android.app.Dialog
import android.content.Intent
import android.content.IntentSender
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResponseListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.fxn.stash.Stash
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult.RESULT_IN_APP_UPDATE_FAILED
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.review.ReviewManagerFactory.create
import com.google.common.collect.ImmutableList
import com.ixidev.data.common.openPlayStore
import com.ixidev.data.common.rateApp
import com.ixidev.data.logger.*
import com.ixidev.data.model.MenuPlayListItem
import com.ixidev.mobile.Constant
import com.ixidev.mobile.R
import com.ixidev.mobile.databinding.ActivityMobileMainBinding
import com.ixidev.mobile.di.DrawerItemsProvider
import com.ixidev.mobile.di.MenuDrawerDi
import com.ixidev.mobile.ui.SubscribeActivity
import com.ixidev.mobile.ui.common.SaveSharedPreference
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome
import com.mikepenz.materialdrawer.holder.BadgeStyle
import com.mikepenz.materialdrawer.holder.ColorHolder
import com.mikepenz.materialdrawer.holder.DimenHolder
import com.mikepenz.materialdrawer.iconics.iconicsIcon
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.*
import com.mikepenz.materialdrawer.util.*
import com.mikepenz.materialdrawer.widget.AccountHeaderView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class
MobileMainActivity : AppCompatActivity(R.layout.activity_mobile_main) {

    private val mainBinding: ActivityMobileMainBinding by viewBinding()

    private val viewModel: MainViewModel by viewModels()

    private var billingClient: BillingClient? = null

    @Inject
    lateinit var menuItemsProvider: DrawerItemsProvider

    @Inject
    lateinit var logger: IAppLogger

    private val UPDATE_REQUEST_CODE = 333
    private val appUpdateManager by lazy {
        AppUpdateManagerFactory.create(this)
    }

    private fun navController() = findNavController(R.id.mobile_nav_host_fragment)

    //lateinit var nativeAdView : Yodo1MasNativeAdView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        billingClient = BillingClient.newBuilder(this)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        checkUserSubscription()

        if (Stash.getBoolean("FIRST_TIME", true)) {
            showDialog();
        }

        setSupportActionBar(mainBinding.toolbar)
        Constant.checkApp(this)
        inAppUpdate()
        setupMenuDrawer(savedInstanceState)
        setupNavController()
        initObservers()
        //enablePlayStoreIAP()
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.guide_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        val close = dialog.findViewById<Button>(R.id.okk)
        close.setOnClickListener {
            dialog.dismiss()
            Stash.put("FIRST_TIME", false)
        }

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.show()
    }

    // Yodo1


    // End Yodo1

    private fun setupMenuDrawer(savedInstanceState: Bundle?) {

        mainBinding.slider.setSavedInstance(savedInstanceState)
        val profile = ProfileDrawerItem().apply {
            nameText = getString(R.string.app_name)
            iconRes = com.ixidev.data.R.mipmap.ic_launcher_round
        }
        // add header view to slider menu
        val identifier =
            resources.getIdentifier("material_drawer_ico_menu_down", "drawable", packageName)
        if (identifier != 0)
            mainBinding.slider.accountHeader = AccountHeaderView(this).apply {
                addProfile(profile, 0)
                selectionListEnabledForSingleProfile = false
                onAccountHeaderProfileImageListener = { _: View, _: IProfile, _: Boolean ->
                    navigateToAbout()
                    true
                }
            }
        mainBinding.slider.addItems(*menuItemsProvider.items.toTypedArray())
        mainBinding.slider.addStickyDrawerItems(*menuItemsProvider.stickyItems.toTypedArray())
        mainBinding.slider.setupWithNavController(navController(), ::onSlideItemSelected)

    }

    private fun navigateToAbout() {
        mainBinding.mainRoot.closeDrawers()
        val libsBuilder = LibsBuilder()
            .withAboutAppName(getString(R.string.app_name))
            .withFields(R.string::class.java.fields)
            .withSearchEnabled(true)
        navController().navigate(R.id.action_about_app, Bundle().apply {
            putSerializable("data", libsBuilder)
        })
    }

    private fun setupNavController() {
        mainBinding.toolbar.setupWithNavController(
            navController(),
            AppBarConfiguration(
                setOf(R.id.homeFragment),
                mainBinding.mainRoot
            )
        )
        // fix menu item selection state
        navController().addOnDestinationChangedListener { _, destination, _ ->
            val drawerItemTag = when (destination.id) {
                R.id.homeFragment -> {
                    mainBinding.toolbar.setNavigationIcon(R.drawable.menus)
                    mainBinding.toolbar.setNavigationContentDescription(R.string.material_drawer_open)
                    MenuDrawerDi.MenuItemTag.Home
                }

                R.id.favoritesFragment -> {
                    MenuDrawerDi.MenuItemTag.Favorites

                }

                R.id.playListsFragment -> {
                    MenuDrawerDi.MenuItemTag.Playlists
                }

                else -> {
                    null
                }
            }
            val item =
                if ((drawerItemTag == MenuDrawerDi.MenuItemTag.Home) || (drawerItemTag == null))
                    mainBinding.slider.getDrawerItem(viewModel.getSelectedPlayListId())
                else
                    mainBinding.slider.getDrawerItem(drawerItemTag)

            item?.let {
                mainBinding.slider.selectExtension.deselect()
                mainBinding.slider.setSelection(it.identifier, false)
            }
        }

    }

    private fun initObservers() {
        viewModel.menuDrawerItems.observe(this) {
            it?.let {
                lifecycleScope.launch(Dispatchers.Main) {
                    val items = it.map { playList ->
                        playList.toMenuItem()
                    }
                    onDrawerItemsChange(items)
                }
            }
        }
        viewModel.currentPlayList.observe(this) {
            it?.let { playlist ->
                viewModel.setFilterByPlayList(playlist.id)
            }
        }
    }

    private fun menuItems() = mainBinding.slider.itemAdapter.itemList.items

    private fun onDrawerItemsChange(items: List<IDrawerItem<*>>) {

        val filter = menuItems().filter {
            (!menuItemsProvider.contains(it)) && !(items.contains(it))
        }
        if (filter.isNotEmpty()) {
            mainBinding.slider.removeItems(filter.maxOf {
                it.identifier
            })
        }

        val newItems = items.filter {
            mainBinding.slider.getDrawerItem(it.identifier) == null
        }
        if (newItems.isNotEmpty()) {
            mainBinding.slider.itemAdapter.add(newItems)
        }
        items.find { it.isSelected }?.let {
            mainBinding.slider.selectExtension.deselect()
            mainBinding.slider.setSelection(it.identifier, false)
        }
    }

    @Deprecated(
        "Deprecated in Java", ReplaceWith(
            "super.onActivityResult(requestCode, resultCode, data)",
            "androidx.appcompat.app.AppCompatActivity"
        )
    )
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPDATE_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    Log.d(TAG, "" + "Result Ok")
                    //  handle user's approval }
                }

                RESULT_CANCELED -> {
                    //if you want to request the update again just call checkUpdate()
                    Log.d(TAG, "" + "Result Cancelled")
                    //inAppUpdate()
                    //  handle user's rejection  }
                }

                RESULT_IN_APP_UPDATE_FAILED -> {
                    //if you want to request the update again just call checkUpdate()
                    Log.d(TAG, "" + "Update Failure")
                    //inAppUpdate()
                    //  handle update failure
                }
            }
        }

    }

    @Suppress("UNUSED_PARAMETER")
    private fun onSlideItemSelected(view: View?, item: IDrawerItem<*>, position: Int): Boolean {

        when (item.tag as? MenuDrawerDi.MenuItemTag) {
            MenuDrawerDi.MenuItemTag.Playlists -> {
                logger.logEvent(EVENT_OPEN_PLAYLISTS)
                navigateToDispensation(R.id.playListsFragment)
            }

            MenuDrawerDi.MenuItemTag.Favorites -> {
                logger.logEvent(EVENT_OPEN_FAVORITES)
                navigateToDispensation(R.id.favoritesFragment)
            }

            MenuDrawerDi.MenuItemTag.Settings -> {
                navigateToDispensation(R.id.action_open_settings)
            }

            MenuDrawerDi.MenuItemTag.RateApp -> {
                logger.logEvent(EVENT_RATE_APP)
                openPlayStore(this, getString(R.string.app_package_name))
            }

            MenuDrawerDi.MenuItemTag.StopAds -> {
                logger.logEvent(EVENT_STOP_ADS)
                //bp.subscribe(this, "aylik_reklam")
                // TODO subscribeBilling()

                startActivity(
                    Intent(
                        this,
                        SubscribeActivity::class.java
                    )
                )

                finish()
            }

            MenuDrawerDi.MenuItemTag.Donation -> {
                //startPurchase()
            }

            MenuDrawerDi.MenuItemTag.DarkMode -> {
                // do nothing
            }

            MenuDrawerDi.MenuItemTag.Home, null -> {
                viewModel.setSelectedPlayList(item.identifier.toInt())
                navigateToDispensation(R.id.homeFragment)
            }
        }
        return true
    }

    private fun navigateToDispensation(distention: Int) {
        if (navController().currentDestination?.id == distention)
            return
        if (distention == R.id.homeFragment)
            navController().popBackStack(distention, false)
        else
            navController().navigate(distention)
    }


    override fun onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (mainBinding.root.isDrawerOpen(mainBinding.slider)) {
            mainBinding.root.closeDrawer(mainBinding.slider)
        } else {
            super.onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        rateApp(this, create(this),
            onSuccess = {
                Log.d(TAG, "onStart: rate success")
            },
            onError = { exception ->
                Log.e(TAG, "onStart: rate error", exception)
            }

        )
    }

    companion object {
        @Suppress("unused")
        private const val TAG = "MobileMainActivity"
    }

    /**
     * Google In App Billing
     * */

    private fun checkUserSubscription() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    queryPurchases()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })

    }

    private fun subscribeBilling() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingResponseCode.OK) {
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
                                    this@MobileMainActivity,
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
            if (billingResult.responseCode == BillingResponseCode.OK) {
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
        if (billingResult.responseCode == BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingResponseCode.ITEM_ALREADY_OWNED) {
            // Handle if already purchased.
            Toast.makeText(this, "Already Subscribed", Toast.LENGTH_SHORT).show()
            SaveSharedPreference.setAdsPref(this, false)
        } else if (billingResult.responseCode == BillingResponseCode.FEATURE_NOT_SUPPORTED) {
            // Handle if USER_CANCELED.
            Toast.makeText(this, "Featured Not Supported", Toast.LENGTH_SHORT).show()
        } else if (billingResult.responseCode == BillingResponseCode.USER_CANCELED) {
            // Handle if USER_CANCELED.
        } else {
            // Handle any other error codes.
            Toast.makeText(this, "Error: ${billingResult.debugMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    private val acknowledgePurchaseResponseListener: AcknowledgePurchaseResponseListener =
        AcknowledgePurchaseResponseListener { billingResult ->
            if (billingResult.responseCode == BillingResponseCode.OK) {
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
            if (billingResult.responseCode == BillingResponseCode.OK) {
                // Handle the success of the consume operation.
            }
        }

        billingClient?.consumeAsync(consumeParams, listener)
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient?.acknowledgePurchase(
                    acknowledgePurchaseParams,
                    acknowledgePurchaseResponseListener
                )
                SaveSharedPreference.setAdsPref(this, false)
                Toast.makeText(this, "Subscribed", Toast.LENGTH_SHORT).show()
            } else {
                SaveSharedPreference.setAdsPref(this, false)
                Toast.makeText(this, "Already Subscribed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->

                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    // If an in-app update is already running, resume the update.
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        UPDATE_REQUEST_CODE
                    )
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        appUpdateManager.unregisterListener(listener)
        billingClient?.endConnection()
    }


    private fun inAppUpdate() {
        val updateTask = appUpdateManager.appUpdateInfo
        updateTask.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && it.isUpdateTypeAllowed(
                    AppUpdateType.IMMEDIATE
                )
            ) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        it, AppUpdateType.IMMEDIATE, this@MobileMainActivity,
                        UPDATE_REQUEST_CODE
                    )
                } catch (ex: IntentSender.SendIntentException) {
                    ex.printStackTrace()
                    Log.d("++update++", "${ex.message}")
                }

            }
        }
        appUpdateManager.registerListener(listener)
    }

    private val listener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            // After the update is downloaded, show a notification
            // and request user confirmation to restart the app.
            popupSnackbarForCompleteUpdate()
        }

    }

    // Displays the snackbar notification and call to action.
    private fun popupSnackbarForCompleteUpdate() {
        Snackbar.make(
            mainBinding.root,
            "An update has just been downloaded.",
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction("RESTART") { appUpdateManager.completeUpdate() }
            setActionTextColor(ContextCompat.getColor(this@MobileMainActivity, R.color.white))
            show()
        }
    }


}

private fun MenuPlayListItem.toMenuItem(): IDrawerItem<*> {
    val badge = BadgeStyle().apply {
        textColor = ColorHolder.fromColorRes(R.color.white)
        corners = DimenHolder.fromDp(5)
        color = ColorHolder.fromColorRes(R.color.black)
    }
    return PrimaryDrawerItem().apply {
        nameText = playlist.name
        identifier = playlist.id.toLong()
        iconicsIcon = FontAwesome.Icon.faw_file
        isSelected = playlist.current
        badgeText = "$count"
        badgeStyle = badge

    }
}
