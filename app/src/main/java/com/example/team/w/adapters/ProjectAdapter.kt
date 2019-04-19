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

    override fun getItemCount(): Int {
        return projects.size
    }

    override fun onBindViewHolder(holder: ProjectAdapter.ViewHolder, position: Int) {
        holder.textName.text = projects[position].name
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ProjectAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_project, parent, false))
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val textName: TextView = v.findViewById(R.id.text_name)
        val buttonPlay: Button = v.findViewById(R.id.button_play)
        val buttonEdit: Button = v.findViewById(R.id.button_play)
        val buttonDelete: Button = v.findViewById(R.id.button_play)
    }


}