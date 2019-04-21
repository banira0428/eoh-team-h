package com.example.team.w.models

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch

object FirebaseRepository {

    private var events: MutableLiveData<List<DocumentSnapshot>>? = null

    fun saveEvents(events: ArrayList<Document>) {

        val db = FirebaseFirestore.getInstance()
        val batch: WriteBatch = db.batch()

        events.forEach {

            if(it.id.isEmpty()){
                val ref = db.collection("events")
                    .document()
                batch.set(ref,it.event)
            }else{
                val ref = db.collection("events")
                    .document(it.id)
                batch.set(ref,it.event)
            }
        }
        batch.commit()
    }

    fun getEvents(): LiveData<List<DocumentSnapshot>>{
        if (events == null) {
            events = MutableLiveData()
            loadEvents()
        }
        return events as MutableLiveData<List<DocumentSnapshot>>
    }

    private fun loadEvents(){
        val db = FirebaseFirestore.getInstance()
        db.collection("events")
            //.whereEqualTo("userId", userId)
            .get()
            .addOnCompleteListener {
                events?.postValue(it.result?.documents)
            }

    }


}