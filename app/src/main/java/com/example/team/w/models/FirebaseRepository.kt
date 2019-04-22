package com.example.team.w.models

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch

object FirebaseRepository {

    private var events: MutableLiveData<List<DocumentSnapshot>>? = null

    fun saveEvents(events: ArrayList<Document>,deleteEvents: ArrayList<Document>) {

        val db = FirebaseFirestore.getInstance()
        val batch: WriteBatch = db.batch()

        events.forEach {

            if(it.id.isEmpty()){ //新規追加
                val ref = db.collection("events")
                    .document()
                batch.set(ref,it.event)
            }else{ //更新
                val ref = db.collection("events")
                    .document(it.id)
                batch.set(ref,it.event)
            }
        }

        deleteEvents.forEach {

            if(it.id.isNotEmpty()){ //削除
                val ref = db.collection("events")
                    .document(it.id)
                batch.delete(ref)
            }
        }

        batch.commit().addOnCompleteListener {
            loadEvents()
        }
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
            .orderBy("wareki")
            .get()
            .addOnCompleteListener {
                events?.postValue(it.result?.documents)
            }

    }


}