package com.example.team.w.extensions

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.LinearLayoutManager



fun RecyclerView.setOrientation(orientation: Int){
    val manager = LinearLayoutManager(context)
    manager.orientation = orientation // ここで横方向に設定
    setLayoutManager(manager)
}