package com.example.team.w.fragments

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.team.w.R
import com.example.team.w.databinding.PlayFragmentBinding
import com.example.team.w.models.AnimationManager
import com.example.team.w.models.Event

class PlayFragment : Fragment() {

    companion object {
        fun newInstance() = PlayFragment()
        const val SHOW_EVENT_TIME: Long = 1000
    }

    private lateinit var viewModel: PlayViewModel

    private lateinit var binding: PlayFragmentBinding

    private var eventPosition = 0

    private var events: Array<Event> = arrayOf(
        Event(name = "イベント1",wareki = 0), Event(name = "イベント2",wareki = 8), Event(name = "イベント3",wareki = 10), Event(name = "イベント4",wareki = 15)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.play_fragment, container, false)

        viewModel = ViewModelProviders.of(this).get(PlayViewModel::class.java)

        val size = Point()
        activity?.windowManager?.defaultDisplay?.getSize(size)
        AnimationManager.screenwidth = size.x

        appear()

        return binding.root
    }

    private fun appear() {

        if (eventPosition >= events.size) return

        binding.textEvent.text = events[eventPosition].name
        AnimationManager.arrowAnimation(binding.textArrow,events[eventPosition].wareki)
        eventPosition++

        AnimationManager.appearAnimation(binding.cardEvent, endListener = {

            Handler().postDelayed({
                disappear()
            }, SHOW_EVENT_TIME)

        })
    }

    private fun disappear() {
        AnimationManager.disappearAnimation(binding.cardEvent, endListener = {
            appear()
        })
    }
}
