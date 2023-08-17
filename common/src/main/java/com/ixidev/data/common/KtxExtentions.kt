@file:Suppress("unused")

package com.ixidev.data.common

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import android.util.Patterns
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.annotation.RequiresPermission
import androidx.fragment.app.Fragment
import kotlin.properties.ReadOnlyProperty


/**
 * Created by ABDELMAJID ID ALI on 04/11/2020.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */


fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun EditText.toText(): String {
    return text.toString()
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

@MainThread
inline fun <reified Arg : Any> Fragment.args(key: String? = null): ReadOnlyProperty<Fragment, Arg> {
    return ReadOnlyProperty { thisRef, property ->
        thisRef.arguments?.get(key ?: property.name) as Arg
    }
}

@MainThread
inline fun <reified T : Any?> Activity.extracts(key: String? = null): ReadOnlyProperty<Activity, T?> {

    return ReadOnlyProperty { thisRef, property ->
        thisRef.intent?.extras?.get(key ?: property.name) as? T
    }
}

fun Long.bytesToMeg(): Long {
    val megabyte = 1024L * 1024L
    return this / (megabyte)
}

fun String?.removeHttps(): String? {
    if (this == null)
        return null
    return this.replaceFirst("https", "http", true)
}


fun String.isUrl(): Boolean {
    //  and (this.startsWith("http", true) or this.startsWith("https", true))
    return Patterns.WEB_URL.matcher(this).matches()
}

@Suppress("DEPRECATION")
@RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
fun Activity.hasNetworkConnection(): Boolean {

    val con = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return con.activeNetworkInfo?.isConnected == true
}

fun getMimeType(url: String?): String? {
    var type: String? = null
    val extension = MimeTypeMap.getFileExtensionFromUrl(url)
    if (extension != null) {
        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
    return type
}

fun Uri.getFileName(): String {
    val tmp = "tmp_${System.currentTimeMillis()}.${getExtension()}"
    return try {
        path?.split("/")?.lastOrNull() ?: tmp
    } catch (e: Exception) {
        tmp
    }
}

fun Uri.getExtension(): String {
    return try {
        pathSegments.last()?.split(".")?.last() ?: "m3u"
    } catch (e: Exception) {
        "m3u"
    }
}