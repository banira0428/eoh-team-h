package com.example.team.w.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.team.w.R
import com.example.team.w.models.Event

class EventAdapter() : RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    var events: ArrayList<Event> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var listener: EventAdapter.OnClickListener? = null

    interface OnClickListener {
        fun onClickSaveEvent()
        fun onClickAddEvent()
    }

    fun setOnClickListener(listener: EventAdapter.OnClickListener) {
        this.listener = listener
    }


    override fun getItemCount(): Int {
        return events.size + 1
    }

    override fun onBindViewHolder(holder: EventAdapter.ViewHolder, position: Int) {

        if (holder is EventViewHolder) {
            holder.editEventName.setText(events[position].name)

            holder.buttonSave.setOnClickListener {
                listener?.onClickSaveEvent()
            }

        } else if (holder is AddViewHolder) {
            holder.buttonAdd.setOnClickListener {
                listener?.onClickAddEvent()
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventAdapter.ViewHolder {

        when (viewType) {
            VIEW_TYPE_EVENT -> return EventViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.card_event,
                    parent,
                    false
                )
            )

            VIEW_TYPE_ADD -> return AddViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.card_add_event,
                    parent,
                    false
                )
            )

            else -> return EventViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.card_event,
                    parent,
                    false
                )
            )

        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position < events.size) return VIEW_TYPE_EVENT
        return VIEW_TYPE_ADD
    }

    abstract class ViewHolder(v: View) : RecyclerView.ViewHolder(v)

    class EventViewHolder(v: View) : ViewHolder(v) {
        val editEventName: EditText = v.findViewById(R.id.edit_event_name)
        val buttonSave: Button = v.findViewById(R.id.button_save)
    }

    class AddViewHolder(v: View) : ViewHolder(v) {
        val buttonAdd: Button = v.findViewById(R.id.button_add_event)

    }

    companion object {
        private const val VIEW_TYPE_EVENT = 1
        private const val VIEW_TYPE_ADD = 2
    }


}