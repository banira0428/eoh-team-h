package com.example.team.w.adapters

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.support.constraint.ConstraintLayout
import android.support.v4.widget.CircularProgressDrawable
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.team.w.R
import com.example.team.w.databinding.CardEventBinding
import com.example.team.w.extensions.isHidden
import com.example.team.w.extensions.setImageWithGlide
import com.example.team.w.models.AnimationManager
import com.example.team.w.models.Document


class EventAdapter(private val context: Context) : RecyclerView.Adapter<EventAdapter.BindingHolder>() {

    var documents: ArrayList<Document> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var needDeleteDocuments: ArrayList<Document> = ArrayList()

    private val yearAdapter = ArrayAdapter(
        context,
        android.R.layout.simple_spinner_item,
        Array(LENGTH_YEAR) { i ->
            if (i == 0) context.getString(R.string.year_start) else context.getString(
                R.string.year,
                i + 1
            )
        }
    )

    private var listener: EventAdapter.OnClickListener? = null

    interface OnClickListener {
        fun onClickSetImage(position: Int)
        fun onClickDelete(position: Int)
    }

    fun setOnClickListener(listener: EventAdapter.OnClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return documents.size
    }

    override fun onBindViewHolder(holder: EventAdapter.BindingHolder, position: Int) {

        val document = documents[holder.adapterPosition]

        holder.binding.layoutCardEdit.isHidden = !document.isEditing
        holder.binding.layoutCardNormal.isHidden = document.isEditing

        holder.binding.textEventName.text = document.event.name
        holder.binding.textEventYear.text =
            if (document.event.wareki == 1) context.getString(R.string.year_start)
            else context.getString(R.string.year, document.event.wareki)

        holder.binding.editEventName.setText(document.event.name)
        holder.binding.editEventName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                documents[holder.adapterPosition].event.name = s.toString()
            }

        })

        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.binding.spinnerYear.adapter = yearAdapter
        holder.binding.spinnerYear.setSelection(document.event.wareki - 1)
        holder.binding.spinnerYear.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                documents[holder.adapterPosition].event.wareki = position + 1
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        holder.binding.editEventDesc.setText(document.event.desc)
        holder.binding.editEventDesc.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                documents[holder.adapterPosition].event.desc = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
        holder.binding.imageEvent.setOnClickListener {
            listener?.onClickSetImage(holder.adapterPosition)
        }

        if (document.event.image_url.isNotEmpty()) { //Firebaseから取得

            holder.binding.imageEvent.setImageWithGlide(context, document.event.image_url)
            holder.binding.imageEventPreview.setImageWithGlide(context, document.event.image_url)

        } else {

            holder.binding.imageEventPreview.scaleType = ImageView.ScaleType.CENTER
            holder.binding.imageEvent.scaleType = ImageView.ScaleType.CENTER
            holder.binding.imageEvent.setImageResource(R.drawable.ic_insert_photo)
            holder.binding.imageEventPreview.setImageResource(R.drawable.ic_insert_photo)
        }

        holder.binding.buttonDelete.setOnClickListener {
            listener?.onClickDelete(holder.adapterPosition)
        }

        holder.binding.buttonClose.setOnClickListener {

            if (AnimationManager.animationJobs > 0) return@setOnClickListener

            holder.binding.layoutCardEdit.visibility = View.GONE
            holder.binding.layoutCardNormal.visibility = View.VISIBLE
            documents[holder.adapterPosition].isEditing = false
            notifyDataSetChanged()

        }


        holder.binding.root.setOnClickListener {

            if (AnimationManager.animationJobs > 0) return@setOnClickListener
            if (document.isEditing) return@setOnClickListener


            holder.binding.layoutCardEdit.visibility = View.VISIBLE
            holder.binding.layoutCardNormal.visibility = View.GONE
            documents[holder.adapterPosition].isEditing = true

        }
    }

    override fun onBindViewHolder(holder: EventAdapter.BindingHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.any()) {

            holder.binding.imageEvent.setImageWithGlide(context, documents[holder.adapterPosition].event.image_url)
            holder.binding.imageEventPreview.setImageWithGlide(
                context,
                documents[holder.adapterPosition].event.image_url
            )

        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventAdapter.BindingHolder {
        return BindingHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.card_event, parent, false))
    }

    fun setImageURL(position: Int, url: String) {
        documents[position].event.image_url = url
    }

    fun downloadImage(position: Int) {
        notifyItemChanged(position, "")
    }

    fun deleteEvent(position: Int) {

        needDeleteDocuments.add(documents[position])
        documents.removeAt(position)
        notifyItemRemoved(position)

        Log.d("keita","$position")
    }

    fun restoreEvent(position: Int) {

        documents.add(position, needDeleteDocuments.last())
        needDeleteDocuments.removeAt(needDeleteDocuments.size - 1)
        notifyItemInserted(position)
    }

    class BindingHolder(var binding: CardEventBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        const val LENGTH_YEAR = 31
    }
}