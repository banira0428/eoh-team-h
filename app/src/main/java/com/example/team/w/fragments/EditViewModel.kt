package com.example.team.w.fragments

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel;
import com.example.team.w.models.Document
import com.example.team.w.models.Event
import com.example.team.w.models.FirebaseRepository
import com.google.firebase.firestore.DocumentSnapshot

class EditViewModel : ViewModel() {

    fun getEvents(): LiveData<List<DocumentSnapshot>> {
        return FirebaseRepository.getEvents()
    }

    fun saveEvents(events: ArrayList<Document>){
        FirebaseRepository.saveEvents(events)
    }

}
