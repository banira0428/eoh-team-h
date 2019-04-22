package com.example.team.w.models

import java.util.*

data class Event(val id:String = "",var name: String = "",var desc: String = "",val image_url: String = "",var wareki: Int = 0,val sent_at: Long = Date().time)