package com.ixidev.player

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager


/**
 * Created by ABDELMAJID ID ALI on 2/16/21.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */


fun View.toggleVisibilityWithAnimation(show: Boolean) {
    val transition: Transition = Slide(Gravity.TOP)
    transition.duration = 300
    transition.addTarget(this)
    TransitionManager.beginDelayedTransition(parent as ViewGroup, transition)
    visibility = if (show) View.VISIBLE else View.GONE
}