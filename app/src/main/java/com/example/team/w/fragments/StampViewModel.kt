package com.example.team.w.fragments

import android.arch.lifecycle.ViewModel
import com.example.team.w.models.FirebaseRepository
import com.example.team.w.models.Stamp

class StampViewModel() : ViewModel() {
    fun send(unicode: String) {
        val stamp = Stamp(unicode, false)
        FirebaseRepository.sendStamp(stamp)
    }

}