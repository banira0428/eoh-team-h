package com.example.team.w.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Document(val id: String = "",var event: Event = Event()) : Parcelable