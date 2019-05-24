package com.example.team.w.adapters

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.Animator.AnimatorPauseListener
import android.animation.AnimatorListenerAdapter
import com.example.team.w.models.AnimationManager

abstract class MyAnimatorListenerAdapter : AnimatorListenerAdapter() {
    override fun onAnimationStart(animation: Animator) {
        AnimationManager.animationJobs += 1

    }

    override fun onAnimationEnd(animation: Animator) {
        AnimationManager.animationJobs -= 1

    }
}