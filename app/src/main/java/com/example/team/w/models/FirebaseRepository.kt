package com.example.team.w.models

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch

object FirebaseRepository {

    fun createEvents(events: ArrayList<Event>) {

        val db = FirebaseFirestore.getInstance()

        val batch: WriteBatch = db.batch()

        events.forEach {

            val ref = db.collection("projects")
                .document()
                .collection("events")
                .document()

            batch.set(ref,it)

        }

        batch.commit()

    }


}