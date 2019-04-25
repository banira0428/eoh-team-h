package com.example.team.w.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.team.w.GlideApp
import com.example.team.w.R
import com.example.team.w.databinding.PlayFragmentBinding
import com.example.team.w.models.AnimationManager
import com.example.team.w.models.Document
import kotlinx.android.synthetic.main.play_fragment.*

class PlayFragment : Fragment() {

    companion object {
        fun newInstance() = PlayFragment()
        const val SHOW_EVENT_TIME: Long = 1000
    }

    private lateinit var viewModel: PlayViewModel

    private lateinit var binding: PlayFragmentBinding

    private var eventPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.play_fragment, container, false)

        viewModel = ViewModelProviders.of(this).get(PlayViewModel::class.java)

        val size = Point()
        activity?.windowManager?.defaultDisplay?.getSize(size)
        AnimationManager.screenwidth = size.x

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.livedata.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                appear(it)
            }
        })
        AnimationManager.arrowPosition = 0f
        AnimationManager.previousPosition = 0
        text_arrow.translationX = 0f
        viewModel.start(PlayFragmentArgs.fromBundle(arguments ?: return).documentList.toList())
    }

    private fun appear(item: List<Document>) {
        if (eventPosition >= viewModel.getDocumentsSize()) {
            findNavController().popBackStack()
            return
        }

        Glide.with(requireContext()).load(viewModel.getImage(eventPosition)).into(binding.playCardImage)
        binding.playCardTitle.text = "${item[eventPosition].event.name} "
        binding.playCardDesc.text = "${item[eventPosition].event.desc} "
        AnimationManager.arrowAnimation(binding.textArrow, item[eventPosition].event.wareki)
        binding.textPlayingYear.text = "H ${item[eventPosition].event.wareki} "
        eventPosition++

        AnimationManager.appearAnimation(binding.cardEvent, endListener = {

            Handler().postDelayed({
                disappear(item)
            }, SHOW_EVENT_TIME)

        })
    }

    private fun disappear(item: List<Document>) {
        AnimationManager.disappearAnimation(binding.cardEvent) {
            appear(item)
        }
    }

}
