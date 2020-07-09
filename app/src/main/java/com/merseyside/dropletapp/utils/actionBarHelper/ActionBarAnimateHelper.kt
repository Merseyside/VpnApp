package com.merseyside.dropletapp.utils.actionBarHelper

import android.widget.ImageButton
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.widget.ActionMenuView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.merseyside.animators.AnimatorList
import com.merseyside.animators.Approach
import com.merseyside.animators.animator.ColorAnimator
import com.merseyside.utils.ext.setColor
import com.merseyside.utils.time.TimeUnit

class ActionBarAnimateHelper(
    toolbar: Toolbar,
    private val duration: TimeUnit
) : ActionBarHelper(toolbar) {

    private val animatorList = AnimatorList(Approach.TOGETHER)

    fun setTitleColorAnimated(@ColorInt color: Int) {

        animatorList.apply {
            addAnimator(ColorAnimator(ColorAnimator.Builder(
                view = getTitleTextView(),
                duration = duration,
                propertyName = "textColor"
            ).apply {
                values(color)
            }))
        }
    }

    fun setTitleColorResAnimated(@ColorRes color: Int) {
        setTitleColorAnimated(ContextCompat.getColor(toolbar.context, color))
    }

    fun setBackgroundColorAnimated(@ColorInt color: Int) {
        animatorList.apply {
            addAnimator(ColorAnimator(ColorAnimator.Builder(
                view = toolbar,
                duration = duration
            ).apply {
                values(color)
            }))
        }
    }

    fun setBackgroundColorResAnimated(@ColorRes color: Int) {
        setBackgroundColorAnimated(ContextCompat.getColor(toolbar.context, color))
    }

    fun setViewsBackgroundAnimated(@ColorInt color: Int) {
        for (i in 0 until toolbar.childCount) {
            val view = toolbar.getChildAt(i)

            animatorList.apply {
                addAnimator(ColorAnimator(ColorAnimator.Builder(
                    view = view,
                    duration = duration
                ).apply {
                    values(color)
                }))
            }
        }
    }

    fun setViewsBackgroundResAnimated(@ColorRes color: Int) {
        setViewsBackgroundAnimated(ContextCompat.getColor(toolbar.context, color))
    }

    fun setIconsColorAnimated(@ColorInt color: Int) {
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

                                animatorList.apply {
                                    addAnimator(ColorAnimator(ColorAnimator.Builder(
                                        drawable = drawable,
                                        duration = duration
                                    ).apply {
                                        values(color)
                                    }))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun setIconsColorResAnimated(@ColorRes color: Int) {
        setIconsColorAnimated(ContextCompat.getColor(toolbar.context, color))
    }

    fun clear() {
        animatorList.clear()
    }

    fun start() {
        animatorList.start()
    }

    fun reverse() {
        animatorList.reverse()
    }
}