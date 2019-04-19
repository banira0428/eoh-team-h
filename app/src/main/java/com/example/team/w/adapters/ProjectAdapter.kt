package com.example.team.w.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.team.w.R
import com.example.team.w.models.Project

class ProjectAdapter() : RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {

    var projects: ArrayList<Project> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var listener: ProjectAdapter.OnClickListener? = null

    interface OnClickListener {
        fun onClickPlayProject()
        fun onClickEditProject()
        fun onClickDeleteProject()
        fun onClickAddProject()
    }

    fun setOnClickListener(listener: ProjectAdapter.OnClickListener) {
        this.listener = listener
    }


    override fun getItemCount(): Int {
        return projects.size + 1
    }

    override fun onBindViewHolder(holder: ProjectAdapter.ViewHolder, position: Int) {

        if(holder is ProjectViewHolder){
            holder.textName.text = projects[position].name

            holder.buttonPlay.setOnClickListener {
                listener?.onClickPlayProject()
            }

            holder.buttonEdit.setOnClickListener {
                listener?.onClickEditProject()
            }

            holder.buttonDelete.setOnClickListener {
                listener?.onClickDeleteProject()
            }
        }else if (holder is AddViewHolder){
            holder.buttonAdd.setOnClickListener { 
                listener?.onClickAddProject()
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectAdapter.ViewHolder {

        when (viewType) {
            VIEW_TYPE_PROJECT -> {
                return ProjectViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_project, parent, false))
            }
            VIEW_TYPE_ADD -> {
                return AddViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_add, parent, false))
            }
        }
        return ProjectViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_project, parent, false))
    }

    override fun getItemViewType(position: Int): Int {
        if (position < projects.size) return VIEW_TYPE_PROJECT
        return VIEW_TYPE_ADD
    }

    abstract class ViewHolder(v: View) : RecyclerView.ViewHolder(v)

    class ProjectViewHolder(v: View) : ViewHolder(v) {
        val textName: TextView = v.findViewById(R.id.text_name)
        val buttonPlay: Button = v.findViewById(R.id.button_play)
        val buttonEdit: Button = v.findViewById(R.id.button_edit)
        val buttonDelete: Button = v.findViewById(R.id.button_delete)
    }

    class AddViewHolder(v: View) : ViewHolder(v) {
        val buttonAdd: Button = v.findViewById(R.id.button_add)

    }

    companion object {
        private const val VIEW_TYPE_PROJECT = 1
        private const val VIEW_TYPE_ADD = 2
    }


}