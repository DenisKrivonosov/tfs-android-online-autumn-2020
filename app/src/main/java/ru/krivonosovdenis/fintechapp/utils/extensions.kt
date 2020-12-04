package ru.krivonosovdenis.fintechapp.utils

import android.view.View

fun View.postDelayedSafe(delayMillis: Long, block: () -> Unit) {
    val runnable = Runnable { block() }
    postDelayed(runnable, delayMillis)
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(view: View) {}

        override fun onViewDetachedFromWindow(view: View) {
            removeOnAttachStateChangeListener(this)
            view.removeCallbacks(runnable)
        }
    })
}