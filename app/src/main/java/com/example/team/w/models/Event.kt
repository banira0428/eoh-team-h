package com.example.team.w.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Event(
    val device_id: String = "",
    var name: String = "",
    var desc: String = "",
    var image_url: String = "",
    var wareki: Int = 1,
    val sent_at: Long = Date().time
) : Parcelable
