package com.example.team.w.adapters

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.NumberPicker
import com.example.team.w.R
import com.example.team.w.models.Document

class EventAdapter() : RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    var documents: ArrayList<Document> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var needDeleteDocuments: ArrayList<Document> = ArrayList()

    var selectedItemPosition: Int = 0

    private var listener: EventAdapter.OnClickListener? = null

    interface OnClickListener {
        fun onClickSetImage(position: Int)
        fun onClickSetDate(position: Int)
    }

    fun setOnClickListener(listener: EventAdapter.OnClickListener) {
        this.listener = listener
    }


    override fun getItemCount(): Int {
        return documents.size
    }

    override fun onBindViewHolder(holder: EventAdapter.ViewHolder, position: Int) {

        if (holder is EventViewHolder) {
            holder.editEventName.setText(documents[holder.adapterPosition].event.name)
            holder.editEventName.addTextChangedListener(object: TextWatcher{
                override fun afterTextChanged(s: Editable?) {
                    documents[holder.adapterPosition].event.name = s.toString()
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

            })
            holder.imageEvent.setOnClickListener {
                selectedItemPosition = holder.adapterPosition
                listener?.onClickSetImage(holder.adapterPosition)
            }
            holder.buttonYear.setOnClickListener {
                listener?.onClickSetDate(holder.adapterPosition)
            }
//            if(events[position].imageURI != Uri.EMPTY){
//                holder.imageEvent.setImageURI(events[position].imageURI)
//            }
            holder.pickerYear.minValue = 1
            holder.pickerYear.maxValue = 31

            holder.buttonDelete.setOnClickListener {
                needDeleteDocuments.add(documents[holder.adapterPosition])
                documents.removeAt(holder.adapterPosition)
                notifyItemRemoved(holder.adapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventAdapter.ViewHolder {

        return EventViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.card_event,
                parent,
                false
            )
        )
    }

    override fun getItemViewType(position: Int): Int {
        if (position < documents.size) return VIEW_TYPE_EVENT
        return VIEW_TYPE_ADD
    }

    fun setImageResource(uri: Uri){
        //events[selectedItemPosition].imageURI = uri
        notifyDataSetChanged()
    }

    abstract class ViewHolder(v: View) : RecyclerView.ViewHolder(v)

    class EventViewHolder(v: View) : ViewHolder(v) {
        val editEventName: EditText = v.findViewById(R.id.edit_event_name)
        val imageEvent: ImageButton = v.findViewById(R.id.image_event)
        val buttonYear: Button = v.findViewById(R.id.button_year)
        val pickerYear: NumberPicker = v.findViewById(R.id.picker_year)
        val buttonDelete: Button = v.findViewById(R.id.button_delete)
    }

    companion object {
        private const val VIEW_TYPE_EVENT = 1
        private const val VIEW_TYPE_ADD = 2
    }


}