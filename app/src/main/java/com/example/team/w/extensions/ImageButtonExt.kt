package com.example.team.w.extensions

import android.content.Context
import android.graphics.Color
import android.support.v4.widget.CircularProgressDrawable
import android.widget.ImageButton
import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageButton.setImageWithGlide(context: Context, url: String){

    scaleType = ImageView.ScaleType.CENTER_CROP

    val circularProgressDrawable = CircularProgressDrawable(context)
    circularProgressDrawable.setColorSchemeColors(Color.WHITE)
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f
    circularProgressDrawable.start()

    Glide.with(context).load(url)
        .placeholder(circularProgressDrawable).into(this)

}