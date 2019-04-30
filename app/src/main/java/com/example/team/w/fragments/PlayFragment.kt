package com.example.team.w.fragments

import android.animation.ObjectAnimator
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
import com.example.team.w.R
import com.example.team.w.databinding.PlayFragmentBinding
import com.example.team.w.models.AnimationManager
import com.example.team.w.models.Document
import kotlinx.android.synthetic.main.play_fragment.*

class PlayFragment : Fragment() {

    private lateinit var viewModel: PlayViewModel

    private lateinit var binding: PlayFragmentBinding

    private var eventPosition = 0

    private var playingAnimation: ObjectAnimator? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.play_fragment, container, false)

        viewModel = ViewModelProviders.of(this).get(PlayViewModel::class.java)

        val size = Point()
        activity?.windowManager?.defaultDisplay?.getSize(size)
        AnimationManager.screenwidth = size.x

        binding.buttonReplay.setOnClickListener {
            findNavController().navigate(PlayFragmentDirections.actionReplay(viewModel.livedata.value?.toTypedArray() ?: emptyArray()))
        }

        binding.buttonEditEvent.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.buttonStop.setOnClickListener {
            viewModel.livedata.postValue(null)
            findNavController().popBackStack()
        }

        binding.buttonPause.setOnClickListener {

            if(playingAnimation?.isPaused == true){
                playingAnimation?.resume()
                binding.buttonPause.setImageResource(R.drawable.ic_action_playback_pause)
            }else{
                playingAnimation?.pause()
                binding.buttonPause.setImageResource(R.drawable.ic_action_playback_play)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.livedata.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                appear(it)
            }
        })
        AnimationManager.previousPosition = 0
        text_arrow.translationX = 0f
        viewModel.start(PlayFragmentArgs.fromBundle(arguments ?: return).documentList.toList())
    }

    private fun appear(item: List<Document>) {
        if (eventPosition >= viewModel.getDocumentsSize()) {

            binding.layoutMenu.visibility = View.VISIBLE
            AnimationManager.appearAnimation(binding.layoutMenu){}
            binding.textPlayingYear.text = getString(R.string.year_play, YEAR_MAX)
            AnimationManager.arrowAnimation(binding.textArrow, YEAR_MAX)
            return
        }

        context?.also {
            if(viewModel.getImage(eventPosition).isNotEmpty()){
                Glide.with(it).load(viewModel.getImage(eventPosition)).into(binding.playCardImage)
            }
        }

        binding.playCardTitle.text = item[eventPosition].event.name
        binding.playCardDesc.text = item[eventPosition].event.desc
        AnimationManager.arrowAnimation(binding.textArrow, item[eventPosition].event.wareki - 1)
        binding.textPlayingYear.text = "${getString(R.string.year_play,item[eventPosition].event.wareki)} "
        eventPosition++

        playingAnimation = AnimationManager.appearAnimation(binding.cardEvent, endListener = {

            playingAnimation = AnimationManager.stayAnimation(binding.cardEvent){
                disappear(item)
            }
        })
    }

    private fun disappear(item: List<Document>) {
        playingAnimation = AnimationManager.disappearAnimation(binding.cardEvent) {
            appear(item)
        }
    }

    companion object {
        const val YEAR_MAX = 31
    }

}
