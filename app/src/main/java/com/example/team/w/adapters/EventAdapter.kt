package com.example.team.w.adapters

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
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
import com.bumptech.glide.Glide
import com.example.team.w.GlideApp
import com.example.team.w.R
import com.example.team.w.ResizeAnimation
import com.example.team.w.models.AnimationManager
import com.example.team.w.models.Document
import com.google.firebase.storage.FirebaseStorage


class EventAdapter(private val context: Context) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    var documents: ArrayList<Document> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var needDeleteDocuments: ArrayList<Document> = ArrayList()

    var selectedItemPosition: Int = -1

    private val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    private val yearAdapter = ArrayAdapter(
        context,
        android.R.layout.simple_spinner_item, Array(31) { i -> "平成${if (i == 0) "元" else i + 1}年" }
    )

    private var listener: EventAdapter.OnClickListener? = null

    interface OnClickListener {
        fun onClickSetImage(position: Int)
    }

    fun setOnClickListener(listener: EventAdapter.OnClickListener) {
        this.listener = listener
    }


    override fun getItemCount(): Int {
        return documents.size
    }

    override fun onBindViewHolder(holder: EventAdapter.EventViewHolder, position: Int) {


        if (documents[holder.adapterPosition].isEditing) {
            holder.layoutEdit.visibility = View.VISIBLE
            holder.layoutNormal.visibility = View.GONE
        } else {
            holder.layoutEdit.visibility = View.GONE
            holder.layoutNormal.visibility = View.VISIBLE
        }

        holder.textEventName.text = documents[holder.adapterPosition].event.name
        holder.textEventYear.text =
            "平成${if (documents[holder.adapterPosition].event.wareki == 1) "元" else documents[holder.adapterPosition].event.wareki}年"

        holder.editEventName.setText(documents[holder.adapterPosition].event.name)

        holder.editEventName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                documents[holder.adapterPosition].event.name = s.toString()
            }

        })

        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.spinnerYear.adapter = yearAdapter
        holder.spinnerYear.setSelection(documents[holder.adapterPosition].event.wareki - 1)
        holder.spinnerYear.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                documents[holder.adapterPosition].event.wareki = position + 1
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        holder.editEventDesc.setText(documents[holder.adapterPosition].event.desc)
        holder.editEventDesc.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                documents[holder.adapterPosition].event.desc = s.toString()
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

        if (documents[holder.adapterPosition].image_uri != Uri.EMPTY) {

            holder.imageEventPreview.scaleType = ImageView.ScaleType.CENTER_CROP
            holder.imageEvent.scaleType = ImageView.ScaleType.CENTER_CROP

            holder.imageEventPreview.setImageURI(documents[holder.adapterPosition].image_uri)
            holder.imageEvent.setImageURI(documents[holder.adapterPosition].image_uri)

        } else if (documents[holder.adapterPosition].event.image_url.isNotEmpty()) { //Firebaseから取得

            holder.imageEventPreview.scaleType = ImageView.ScaleType.CENTER_CROP
            holder.imageEvent.scaleType = ImageView.ScaleType.CENTER_CROP

            val circularProgressDrawable = CircularProgressDrawable(context)
            circularProgressDrawable.setColorSchemeColors(Color.WHITE)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()

            Glide.with(context).load(documents[holder.adapterPosition].event.image_url)
                .placeholder(circularProgressDrawable).into(holder.imageEventPreview)
            Glide.with(context).load(documents[holder.adapterPosition].event.image_url)
                .placeholder(circularProgressDrawable).into(holder.imageEvent)

        } else {

            holder.imageEventPreview.scaleType = ImageView.ScaleType.CENTER
            holder.imageEvent.scaleType = ImageView.ScaleType.CENTER
            holder.imageEvent.setImageResource(R.drawable.ic_insert_photo)
            holder.imageEventPreview.setImageResource(R.drawable.ic_insert_photo)
        }

        holder.buttonDelete.setOnClickListener {
            deleteEvent(holder.adapterPosition)
        }

        holder.buttonClose.setOnClickListener {

            if (AnimationManager.animationJobs > 0) return@setOnClickListener

            //AnimationManager.expandAnimation(holder.cardView, -300)

            holder.layoutEdit.visibility = View.GONE
            holder.layoutNormal.visibility = View.VISIBLE
            notifyDataSetChanged()
            documents[position].isEditing = false

//            AnimationManager.disappearEditEventAnimation(holder.cardEditView, endListener = {
//
//            })
        }


        holder.itemView.setOnClickListener {

            if (AnimationManager.animationJobs > 0) return@setOnClickListener
            if (documents[holder.adapterPosition].isEditing) return@setOnClickListener

            if (holder.layoutEdit.visibility == View.GONE) {
                holder.layoutEdit.visibility = View.VISIBLE
                //AnimationManager.appearEditEventAnimation(holder.cardEditView, endListener = {})
                holder.layoutNormal.visibility = View.GONE
            }

            documents[holder.adapterPosition].isEditing = true

            //AnimationManager.expandAnimation(holder.cardView, 300)

        }
    }

    override fun onBindViewHolder(holder: EventAdapter.EventViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.any()) {
            holder.imageEventPreview.scaleType = ImageView.ScaleType.CENTER_CROP
            holder.imageEvent.scaleType = ImageView.ScaleType.CENTER_CROP

            val circularProgressDrawable = CircularProgressDrawable(context)
            circularProgressDrawable.setColorSchemeColors(Color.WHITE)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()

            Glide.with(context).load(documents[holder.adapterPosition].event.image_url)
                .placeholder(circularProgressDrawable).into(holder.imageEventPreview)
            Glide.with(context).load(documents[holder.adapterPosition].event.image_url)
                .placeholder(circularProgressDrawable).into(holder.imageEvent)
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventAdapter.EventViewHolder {

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

    fun setImageURL(url: String) {
        documents[selectedItemPosition].event.image_url = url
    }

    fun setImageURI(uri: Uri) {
        documents[selectedItemPosition].image_uri = uri
        notifyDataSetChanged()
    }

    fun downloadImage() {
        notifyItemChanged(selectedItemPosition, "")
    }

    fun deleteEvent(position: Int) {
        needDeleteDocuments.add(documents[position])
        documents.removeAt(position)
        notifyItemRemoved(position)
    }

    class EventViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val cardEditView: CardView = v.findViewById(R.id.card_edit_event)
        val imageEventPreview: ImageButton = v.findViewById(R.id.image_event_preview)
        val textEventName: TextView = v.findViewById(R.id.text_event_name)
        val textEventYear: TextView = v.findViewById(R.id.text_event_year)
        val editEventName: EditText = v.findViewById(R.id.edit_event_name)
        val editEventDesc: EditText = v.findViewById(R.id.edit_event_desc)
        val imageEvent: ImageButton = v.findViewById(R.id.image_event)
        val spinnerYear: Spinner = v.findViewById(R.id.spinner_year)
        val buttonDelete: ImageButton = v.findViewById(R.id.button_delete)
        val buttonClose: Button = v.findViewById(R.id.button_close)

        val layoutNormal: ConstraintLayout = v.findViewById(R.id.layout_card_normal)
        val layoutEdit: ConstraintLayout = v.findViewById(R.id.layout_card_edit)

    }

    companion object {
        private const val VIEW_TYPE_EVENT = 1
        private const val VIEW_TYPE_ADD = 2
    }


}