package com.merseyside.dropletapp.utils.actionBarHelper

import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.UiThread
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.widget.ActionMenuView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.merseyside.merseyLib.utils.ext.setColor


open class ActionBarHelper(protected val toolbar: Toolbar) {

    fun setBackgroundDrawable(@DrawableRes drawable: Int) {
        toolbar.background = ContextCompat.getDrawable(toolbar.context, drawable)
    }

    fun setBackgroundColor(@ColorInt color: Int) {
        toolbar.setBackgroundColor(color)
    }

    fun setBackgroundResColor(@ColorRes color: Int) {
        toolbar.setBackgroundColor(ContextCompat.getColor(toolbar.context, color))
    }

    fun setTitleColor(@ColorInt color: Int) {
        toolbar.setTitleTextColor(color)
    }

    fun setTitleColorRes(@ColorRes color: Int) {
        toolbar.setTitleTextColor(ContextCompat.getColor(toolbar.context, color))
    }

    fun setIconsColor(@ColorInt color: Int) {
        for (i in 0 until toolbar.childCount) {
            val v = toolbar.getChildAt(i)

            if (v is ImageButton) {
                v.drawable.mutate().setColor(color)
            }
            if (v is ActionMenuView) {
                for (j in 0 until v.childCount) {

                    val innerView = v.getChildAt(j)
                    if (innerView is ActionMenuItemView) {
                        val drawablesCount = innerView.compoundDrawables.size
                        for (k in 0 until drawablesCount) {
                            if (innerView.compoundDrawables[k] != null) {
                                val drawable = innerView.compoundDrawables[k]

                                innerView.post { drawable.setColor(color) }
                            }
                        }
                    }
                }
            }
        }
    }

    fun setIconsColorRes(@ColorRes color: Int) {
        setIconsColor(ContextCompat.getColor(toolbar.context, color))
    }

    fun setViewsBackground(@ColorInt color: Int) {
        for (i in 0 until toolbar.childCount) {
            val v = toolbar.getChildAt(i)

            v.setBackgroundColor(color)
        }
    }

    fun setViewsBackgroundRes(@ColorRes color: Int) {
        setViewsBackground(ContextCompat.getColor(toolbar.context, color))
    }

    protected fun getTitleTextView(): TextView {
        for (i in 0 until toolbar.childCount) {
            if (toolbar.getChildAt(i) is TextView) {
                return toolbar.getChildAt(i) as TextView
            }
        }

        throw Exception()
    }
}