package com.ixidev.data.common

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.google.android.play.core.review.ReviewManager

/**
 * Created by ABDELMAJID ID ALI on 2/10/21.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */
private const val TAG = "Utils"

/**
 * Send report via email
 * @param context :context
 * @param sendTo : receiver email
 * @param appName : App name as email subject
 */
fun contactUs(context: Context, sendTo: String, appName: String) {
    val i = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_EMAIL, arrayOf(sendTo))
        putExtra(Intent.EXTRA_SUBJECT, "Report( $appName )")
        putExtra(Intent.EXTRA_TEXT, "How can i help u ?")
    }
    try {
        context.startActivity(Intent.createChooser(i, "Send mail..."))
    } catch (ex: ActivityNotFoundException) {
        Log.e("Utils", " contactUs: ", ex)
        Toast.makeText(context, "Can't send email", Toast.LENGTH_SHORT).show()
    }
}

/**
 * Open PlayStore
 * @param [context]: context
 * @param [packageName] : app packageName
 */
fun openPlayStore(context: Context, packageName: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = "market://details?id=$packageName".toUri()
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        intent.data = "https://play.google.com/store/apps/details?id=$packageName".toUri()
        context.startActivity(intent)
    }
}


fun rateApp(
    activity: Activity,
    manager: ReviewManager,
    onComplete: () -> Unit = {},
    onSuccess: () -> Unit = {},
    onError: (error: Exception?) -> Unit = {},
) {
    val request = manager.requestReviewFlow()
    request.addOnSuccessListener { reviewInfo ->
        val flow = manager.launchReviewFlow(activity, reviewInfo)
        flow.addOnSuccessListener {
            onSuccess.invoke()
        }
        flow.addOnCompleteListener {
            onComplete.invoke()
        }
        flow.addOnFailureListener { exception ->
            onError.invoke(exception)
        }
    }
    request.addOnFailureListener { exception ->
        onError.invoke(exception)
    }
}