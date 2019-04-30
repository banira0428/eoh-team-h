package com.example.team.w.models

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.graphics.Bitmap
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*


object FirebaseRepository {

    private var events: MutableLiveData<List<DocumentSnapshot>>? = null
    var uuid = ""

    fun saveEvents(events: ArrayList<Document>, deleteEvents: ArrayList<Document>, success: () -> Unit) {

        val db = FirebaseFirestore.getInstance()
        val batch: WriteBatch = db.batch()

        events.forEach {

            if (it.id.isEmpty()) { //新規追加
                val ref = db.collection("events")
                    .document()
                batch.set(ref, it.event)
            } else { //更新
                val ref = db.collection("events")
                    .document(it.id)
                batch.set(ref, it.event)
            }
        }

        deleteEvents.forEach {

            if (it.id.isNotEmpty()) { //削除
                val ref = db.collection("events")
                    .document(it.id)
                batch.delete(ref)

                if (it.event.image_url.isNotEmpty()) {
                    deleteImage(it.event.image_url)
                }
            }
        }

        batch.commit().addOnSuccessListener {
            loadEvents()
            success()
        }
    }

    fun saveEvent(document: Document, success: () -> Unit) {

        val db = FirebaseFirestore.getInstance()
        val batch: WriteBatch = db.batch()


        if (document.id.isEmpty()) { //新規追加
            val ref = db.collection("events")
                .document()
            batch.set(ref, document.event)
        } else { //更新
            val ref = db.collection("events")
                .document(document.id)
            batch.set(ref, document.event)
        }

        batch.commit().addOnSuccessListener {
            success()
        }
    }

    fun deleteEvent(document: Document) {

        val db = FirebaseFirestore.getInstance()
        val batch: WriteBatch = db.batch()


        if (document.id.isNotEmpty()) { //削除
            val ref = db.collection("events")
                .document(document.id)
            batch.delete(ref)

            if (document.event.image_url.isNotEmpty()) {
                deleteImage(document.event.image_url)
            }
        }

        batch.commit()
    }

    private fun deleteImage(url: String) {
        val storage: FirebaseStorage = FirebaseStorage.getInstance()
        storage.reference.child(url.substring(54)).delete()
    }

    fun getEvents(): LiveData<List<DocumentSnapshot>> {
        if (events == null) {
            events = MutableLiveData()
            loadEvents()
        }
        return events as MutableLiveData<List<DocumentSnapshot>>
    }

    private fun loadEvents() {
        val db = FirebaseFirestore.getInstance()
        db.collection("events")
            .whereEqualTo("device_id", uuid)
            .get()
            .addOnCompleteListener {
                events?.postValue(it.result?.documents)
            }
    }


    fun sendStamp(stamp: Stamp) {
        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("stamps")
        collection.document().set(stamp).addOnCompleteListener {
            println("stamp pon!")
        }
    }


    fun uploadImage(bitmap: Bitmap, url: String) {
        val storage = FirebaseStorage.getInstance()

        val storageRef = storage.reference.child(url)

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        storageRef.putBytes(data)
    }

}
