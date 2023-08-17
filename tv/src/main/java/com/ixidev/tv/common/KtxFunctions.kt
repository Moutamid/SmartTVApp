package com.ixidev.tv.common

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ixidev.tv.R


fun Fragment.showError(error: String) {
    findNavController().navigate(
        R.id.action_show_error,
        Bundle().also {
            it.putString("errorMessage", error)
        }
    )
}

fun Fragment.showError(error: Exception) {
    showError(error.message ?: getString(R.string.error_fragment_message))
}

