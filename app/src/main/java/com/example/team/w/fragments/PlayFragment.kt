package com.example.team.w.fragments

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.team.w.R
import com.example.team.w.adapters.YearAdapter
import com.example.team.w.databinding.MainFragmentBinding
import com.example.team.w.databinding.PlayFragmentBinding
import com.example.team.w.extensions.setOrientation

class PlayFragment : Fragment() {

    companion object {
        fun newInstance() = PlayFragment()
    }

    private lateinit var viewModel: PlayViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding =
            DataBindingUtil.inflate<PlayFragmentBinding>(inflater, R.layout.play_fragment, container, false)

        viewModel = ViewModelProviders.of(this).get(PlayViewModel::class.java)

        binding.listYear.layoutManager = LinearLayoutManager(requireContext())
        binding.listYear.setHasFixedSize(true)
        binding.listYear.setOrientation(LinearLayoutManager.HORIZONTAL)
        binding.listYear.adapter = YearAdapter()

        return binding.root
    }
}
