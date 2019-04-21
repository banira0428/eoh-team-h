package com.example.team.w.adapters

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.NumberPicker
import com.example.team.w.R
import com.example.team.w.models.Event

class EventAdapter() : RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    var events: ArrayList<Event> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

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
        return events.size
    }

    override fun onBindViewHolder(holder: EventAdapter.ViewHolder, position: Int) {

        if (holder is EventViewHolder) {
            holder.editEventName.setText(events[position].name)
            holder.imageEvent.setOnClickListener {
                selectedItemPosition = position
                listener?.onClickSetImage(position)
            }
            holder.buttonYear.setOnClickListener {
                listener?.onClickSetDate(position)
            }
            if(events[position].imageURI != Uri.EMPTY){
                holder.imageEvent.setImageURI(events[position].imageURI)
            }
            holder.pickerYear.minValue = 1
            holder.pickerYear.maxValue = 31
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
        if (position < events.size) return VIEW_TYPE_EVENT
        return VIEW_TYPE_ADD
    }

    fun setImageResource(uri: Uri){
        events[selectedItemPosition].imageURI = uri
        notifyDataSetChanged()
    }

    abstract class ViewHolder(v: View) : RecyclerView.ViewHolder(v)

    class EventViewHolder(v: View) : ViewHolder(v) {
        val editEventName: EditText = v.findViewById(R.id.edit_event_name)
        val imageEvent: ImageButton = v.findViewById(R.id.image_event)
        val buttonYear: Button = v.findViewById(R.id.button_year)
        val pickerYear: NumberPicker = v.findViewById(R.id.picker_year)
    }

    companion object {
        private const val VIEW_TYPE_EVENT = 1
        private const val VIEW_TYPE_ADD = 2
    }


}