package com.example.team.w.fragments

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.example.team.w.models.Document
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class PlayViewModel : ViewModel() {
    private lateinit var documents: List<Document>
    var livedata = MutableLiveData<List<Document>>()

    fun start(list: List<Document>) {
        documents = list.sortedBy { it.event.wareki }
        livedata.postValue(documents)
    }

    fun getDocumentsSize(): Int = documents.size

    fun getImage(position: Int): StorageReference? {
        return try {
            FirebaseStorage.getInstance().reference.child(documents[position].event.image_url)
        } catch (e: Exception) {
            null
        }
    }
}
