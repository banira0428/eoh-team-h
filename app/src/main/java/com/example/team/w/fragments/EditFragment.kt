package com.example.team.w.fragments

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.team.w.R
import com.example.team.w.adapters.EventAdapter
import com.example.team.w.databinding.EditFragmentBinding
import com.example.team.w.extensions.setOrientation
import com.example.team.w.models.Event

class EditFragment : Fragment() {

    companion object {
        fun newInstance() = EditFragment()
    }

    private lateinit var viewModel: EditViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<EditFragmentBinding>(inflater, R.layout.edit_fragment, container, false)

        val adapter = EventAdapter()
        adapter.events = arrayListOf(Event(name = "イベント1"), Event(name = "イベント2"))

        adapter.setOnClickListener(object: EventAdapter.OnClickListener{
            override fun onClickSaveEvent() {
            }

            override fun onClickAddEvent() {
                adapter.events.add(Event())
                adapter.notifyItemInserted(adapter.itemCount-1)

                binding.listEvent.scrollToPosition(adapter.itemCount-1)
            }

        })

        binding.listEvent.layoutManager = LinearLayoutManager(requireContext())
        binding.listEvent.setHasFixedSize(true)
        binding.listEvent.adapter = adapter

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(EditViewModel::class.java)
    }

}
