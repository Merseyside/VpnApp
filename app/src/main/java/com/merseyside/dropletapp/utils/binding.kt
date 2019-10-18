package com.merseyside.dropletapp.utils

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.upstream.basemvvmimpl.utils.getColorFromAttr


@BindingAdapter("bind:imageBitmap")
fun loadImageBitmap(iv: ImageView, bitmap: Bitmap) {
    iv.setImageBitmap(bitmap)
}

@BindingAdapter("bind:imageDrawable")
fun loadImageDrawable(iv: ImageView, drawable: Drawable?) {
    iv.setImageDrawable(drawable)
}

@BindingAdapter("bind:vectorDrawable")
fun loadVectorDrawable(iv: ImageView, @DrawableRes resId: Int) {
    iv.setImageResource(resId)
}

@BindingAdapter("bind:attrTextColor")
fun setCustomTextColor(view: TextView, attrId: Int) {
    view.setTextColor(view.context.getColorFromAttr(attrId))
}