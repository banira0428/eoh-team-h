package com.example.team.w.extensions

import android.view.View

var View.isHidden: Boolean
get() {
    return visibility == View.GONE
}
set(value) {
    visibility = if(value) View.GONE else View.VISIBLE
}