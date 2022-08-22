package uz.novalab.facetap

import android.util.Log
import androidx.annotation.NonNull
import com.android.billingclient.api.*
import com.google.gson.Gson
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.banuba.sdk.ve.data.EXTRA_EXPORTED_SUCCESS
import com.banuba.sdk.ve.data.ExportResult
import com.banuba.sdk.ve.flow.VideoCreationActivity
import com.google.gson.GsonBuilder
import io.flutter.plugins.GeneratedPluginRegistrant
import java.util.*

class MainActivity : FlutterActivity(), PurchasesUpdatedListener {
    private val channelInAppPurchase = "samples.flutter.dev/in_app_purchase"
    private var billingClient: BillingClient? = null
    private var skuDetails: ArrayList<SkuDetails>? = null
    private var productIds: ArrayList<String>? = null
    private var finalResult: MethodChannel.Result? = null


    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, channelInAppPurchase).setMethodCallHandler { call, result ->
            if (call.method == "launchPayment") {
                finalResult = result
                val id = call.argument<String>("id").toString()
                launchPayment(id)
                call.arguments
            } else {
                productIds = call.argument<ArrayList<String>>("product_ids")
                Log.d("===", productIds.toString())
                startBilling(result)
            }
//            result.success("result")
//            lastResult = result
        }
    }

    private fun launchPayment(id: String) {
        var sku: SkuDetails? = null
        if (skuDetails != null) {
            skuDetails!!.forEach {
                if (it.sku == id) {
                    sku = it
                }
            }
        }
        if (sku != null) {
            val flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(sku!!)
                    .build()
           billingClient?.launchBillingFlow(activity, flowParams)!!.responseCode

        }
    }

    private fun startBilling(result: MethodChannel.Result) {
        billingClient = BillingClient.newBuilder(this)
                .setListener(this)
                .enablePendingPurchases()
                .build()
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    querySkuDetails(result)
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    fun querySkuDetails(result: MethodChannel.Result) {
        val skuList = ArrayList<String>()
        if (productIds != null) {
            productIds!!.forEach {
                skuList.add(it)
            }
            val params = SkuDetailsParams.newBuilder()
            params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)

            // leverage querySkuDetails Kotlin extension function
            billingClient?.querySkuDetailsAsync(params.build()) { p0, p1 ->
                runOnUiThread {
                    Log.d("===== size", "${p1?.size}")
                    val list: List<SkuDetails> = p1 as List<SkuDetails>
                    val newList = ArrayList<String>()
                    skuDetails = p1 as ArrayList<SkuDetails>?
                        Log.d("===== list", "$list")
                    for (i in list.indices) {
                        Log.d("===== i", list[i].toString().removeRange(0, 12))
                        newList.add(list[i].toString().removeRange(0, 12))
                    }
                    Log.d("===== newList", "$newList")
                    result.success(newList.toString())
                }
            }
        }
        // Process the result.
    }

    override fun onPurchasesUpdated(p0: BillingResult, p1: MutableList<Purchase>?) {
        if (p0.responseCode == BillingClient.BillingResponseCode.OK
                && p1 != null
        ) {
            for (purchase in p1) {
                handlePurchase(purchase)
            }
        } else if (p0.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            if (finalResult != null) {
                finalResult?.success("canceled")
            }
        } else {
            // Handle any other error codes.
        }
        Log.d("=======", "${p0.responseCode}")
    }

    private fun handlePurchase(purchase: Purchase) {
        val consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
        billingClient?.consumeAsync(consumeParams) { p0, p1 ->
            if (p0.responseCode == BillingClient.BillingResponseCode.OK) {
               runOnUiThread {
                   if (finalResult != null) {
                       finalResult?.success("success")
                   }
               }
            }
        }
    }


    companion object {
        private const val VIDEO_EDITOR_REQUEST_CODE = 7788
    }

    private val channel = "startActivity/VideoEditorChannel"

    private lateinit var _result: MethodChannel.Result

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appFlutterEngine = requireNotNull(flutterEngine)

        GeneratedPluginRegistrant.registerWith(appFlutterEngine)


        MethodChannel(
                appFlutterEngine.dartExecutor.binaryMessenger,
                channel
        ).setMethodCallHandler { call, result ->
            if (call.method.equals("StartBanubaVideoEditor")) {
                _result = result
                startActivityForResult(
                        VideoCreationActivity.startFromCamera(
                                context = this,
                                // setup data that will be acceptable during export flow
                                additionalExportData = null,
                                // set TrackData object if you open VideoCreationActivity with preselected music track
                                audioTrackData = null,
                                // set video uri for Picture in Picture feature
                                pictureInPictureVideo = Uri.EMPTY
                        ), VIDEO_EDITOR_REQUEST_CODE
                )
            } else {
                result.notImplemented()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, result: Int, intent: Intent?) {
        if (requestCode == VIDEO_EDITOR_REQUEST_CODE) {
            val exportedVideoResult = if (result == Activity.RESULT_OK) {
                intent?.getParcelableExtra(EXTRA_EXPORTED_SUCCESS) as? ExportResult.Success
            } else {
                ExportResult.Inactive
            }
            val gsonPretty = GsonBuilder().registerTypeAdapter(Uri::class.java, UriDeserializer()).create()
            val jsonString = gsonPretty.toJson(exportedVideoResult)
            Log.d("2121=======", jsonString)
            _result.success(jsonString)
        } else {
            _result.success(null)
            return super.onActivityResult(requestCode, result, intent)
        }
    }
}
