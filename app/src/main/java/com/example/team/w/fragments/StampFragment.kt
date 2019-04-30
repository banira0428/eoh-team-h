package com.example.team.w.fragments

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.text.emoji.widget.EmojiAppCompatButton
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.team.w.R
import com.example.team.w.models.AnimationManager
import kotlinx.android.synthetic.main.stamp_fragment.*

class StampFragment : Fragment() {

    private lateinit var viewModel: StampViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(StampViewModel::class.java)
        return inflater.inflate(R.layout.stamp_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        stamp_smile.text = "\uD83E\uDD23"
        stamp_thinking.text = "\uD83E\uDD14"
        stamp_cry.text = "\uD83D\uDE30"
        stamp_funny.text = "\uD83E\uDD2A"
        stamp_crap.text = "\uD83D\uDC4F"
        stamp_heart.text = "\uD83D\uDC9E"


        stamp_smile.setOnClickListener {
            send(stamp_smile)
        }
        stamp_thinking.setOnClickListener {
            send(stamp_thinking)
        }
        stamp_cry.setOnClickListener {
            send(stamp_cry)
        }
        stamp_crap.setOnClickListener {
            send(stamp_crap)
        }
        stamp_heart.setOnClickListener {
            send(stamp_heart)
        }
        stamp_funny.setOnClickListener {
            send(stamp_funny)
        }
    }

    fun send(v: EmojiAppCompatButton) {

        if (AnimationManager.animationJobs > 0) return

        AnimationManager.sendAnimation(v, endListener = {
            AnimationManager.resetAnimation(v, endListener = {})
        })

        viewModel.send(v.text.toString())

    }
}
