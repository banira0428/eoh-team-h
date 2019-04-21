package com.example.team.w.models

import android.net.Uri

data class Event(val id:String = "",val name: String = "",val desc: String = "",val imageURL: String = "",val wareki: Int = 0,var imageURI: Uri = Uri.EMPTY)