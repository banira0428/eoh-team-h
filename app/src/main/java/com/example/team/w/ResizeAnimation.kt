package com.example.team.w

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation


class ResizeAnimation(internal var view: View, internal val addHeight: Int, private var startHeight: Int) :
    Animation() {

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        val newHeight = (startHeight + addHeight * interpolatedTime).toInt()
        view.layoutParams.height = newHeight
        view.requestLayout()
    }

    override fun willChangeBounds(): Boolean {
        return true
    }
}