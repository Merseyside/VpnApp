package com.merseyside.dropletapp.utils

import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.merseyside.mvvmcleanarch.utils.ext.getColorFromAttr


@BindingAdapter("bind:imageBitmap")
fun loadImageBitmap(iv: ImageView, bitmap: Bitmap) {
    iv.setImageBitmap(bitmap)
}

@BindingAdapter("bind:imageRes")
fun loadImageDrawable(iv: ImageView, @DrawableRes drawableRes: Int?) {
    if (drawableRes != null) {
        iv.setImageDrawable(ContextCompat.getDrawable(iv.context, drawableRes))
    }
}

@BindingAdapter("bind:vectorDrawable")
fun loadVectorDrawable(iv: ImageView, @DrawableRes resId: Int) {
    iv.setImageResource(resId)
}

@BindingAdapter("bind:attrTextColor")
fun setAttrTextColor(view: TextView, attrId: Int?) {
    if (attrId != null) {
        view.setTextColor(view.context.getColorFromAttr(attrId))
    }
}

@BindingAdapter("bind:resTextColor")
fun setTextColor(view: TextView, @ColorRes colorRes: Int?) {
    if (colorRes != null) {
        view.setTextColor(ContextCompat.getColor(view.context, colorRes))
    }
}

