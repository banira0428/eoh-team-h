package com.example.team.w.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.team.w.R

class YearAdapter() : RecyclerView.Adapter<YearAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return NUM_YEAR
    }

    override fun onBindViewHolder(holder: YearAdapter.ViewHolder, position: Int) {

        holder.textYear.text = if(position < 10) " $position" else "$position"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YearAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_year, parent, false))
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val textYear = v.findViewById<TextView>(R.id.text_year)
    }

    companion object {
        const val NUM_YEAR = 31
    }

}