package com.merseyside.dropletapp.presentation.view.fragment.qr

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.annotation.CallSuper
import com.merseyside.dropletapp.R
import com.merseyside.merseyLib.presentation.fragment.BaseFragment
import net.glxn.qrgen.android.QRCode


class QrFragment : BaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_qr
    }

    override fun getTitle(context: Context): String? {
        return context.getString(R.string.qr_code)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var config: String? = null
        if (arguments?.containsKey(CONFIG_KEY) == true) {
            config = arguments!!.getString(CONFIG_KEY, "")
        }

        if (config != null) {
            val qrImage: ImageView = view.findViewById(R.id.qr) as ImageView
            val vto: ViewTreeObserver = qrImage.viewTreeObserver
            vto.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    qrImage.viewTreeObserver.removeOnPreDrawListener(this)
                    val finalHeight = qrImage.measuredHeight
                    val finalWidth = qrImage.measuredWidth

                    setQrCode(qrImage, config, finalWidth, finalHeight)
                    return true
                }
            })
        }
    }

    override fun performInjection(bundle: Bundle?) {}

    private fun setQrCode(imageView: ImageView, config: String, width: Int, height: Int) {
        val myBitmap: Bitmap = QRCode.from(config).withSize(width, height).bitmap()

        imageView.setImageBitmap(myBitmap)
    }

    @CallSuper
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            baseActivity.onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val CONFIG_KEY = "config"

        fun newInstance(config: String): QrFragment {
            val bundle = Bundle().apply {
                putString(CONFIG_KEY, config)
            }

            return QrFragment().apply {
                arguments = bundle
            }
        }
    }
}