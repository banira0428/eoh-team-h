package com.example.team.w.fragments

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.util.Log
import com.example.team.w.models.Document
import com.example.team.w.models.Event
import com.example.team.w.models.FirebaseRepository
import com.google.firebase.firestore.DocumentSnapshot

class EditViewModel : ViewModel() {

    fun getEvents(): LiveData<List<DocumentSnapshot>> {
        return FirebaseRepository.getEvents()
    }

    fun saveEvents(events: ArrayList<Document>,deleteEvents: ArrayList<Document>,endListener: () -> Unit){
        FirebaseRepository.saveEvents(events,deleteEvents,endListener)
    }

    fun saveEvent(event: Document,endListener: () -> Unit){
        FirebaseRepository.saveEvent(event,endListener)
    }

    fun deleteEvent(event: Document){
        FirebaseRepository.deleteEvent(event)
    }


    fun uploadImage(bitmap: Bitmap,url: String){
        FirebaseRepository.uploadImage(bitmap,url)
    }

}
