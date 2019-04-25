package com.example.team.w.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Document(val id: String = "", var isEditing: Boolean = false, var event: Event = Event()) : Parcelable