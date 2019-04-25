package com.example.team.w.fragments

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.example.team.w.models.Document

class PlayViewModel : ViewModel() {
    private lateinit var documents: List<Document>
    var livedata = MutableLiveData<List<Document>>()

    fun start(list: List<Document>) {
        documents = list.sortedBy { it.event.wareki }
        livedata.postValue(documents)
    }

    fun getDocumentsSize(): Int = documents.size

    fun getImage(position: Int): String {
        return documents[position].event.image_url
    }
}
