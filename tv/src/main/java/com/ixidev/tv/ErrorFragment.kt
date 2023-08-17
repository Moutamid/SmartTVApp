package com.ixidev.tv

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat.getDrawable
import androidx.leanback.app.ErrorSupportFragment
import androidx.navigation.fragment.findNavController
import com.ixidev.data.common.args

class ErrorFragment : ErrorSupportFragment() {

    private val errorMessage: String? by args()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showErrorContent()
    }

    private fun showErrorContent() {
        imageDrawable = getDrawable(requireContext(), R.drawable.lb_ic_sad_cloud)
        message = errorMessage ?: resources.getString(R.string.error_fragment_message)
        backgroundDrawable = getDrawable(requireContext(), R.drawable.home_layout_background)
        buttonText = resources.getString(R.string.dismiss_error)
        buttonClickListener = View.OnClickListener {
            findNavController().navigateUp()
        }
    }

}