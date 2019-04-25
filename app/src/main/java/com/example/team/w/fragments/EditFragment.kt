package com.example.team.w.fragments

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.team.w.MainActivity
import com.example.team.w.R
import com.example.team.w.adapters.EventAdapter
import com.example.team.w.databinding.EditFragmentBinding
import com.example.team.w.models.AnimationManager
import com.example.team.w.models.Document
import com.example.team.w.models.Event
import com.example.team.w.models.FirebaseRepository
import java.io.IOException


class EditFragment : Fragment() {

    companion object {
        fun newInstance() = EditFragment()

        const val REQUEST_IMAGE = 0
    }

    private lateinit var viewModel: EditViewModel

    private lateinit var adapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)

        viewModel = ViewModelProviders.of(this).get(EditViewModel::class.java)

        val binding = DataBindingUtil.inflate<EditFragmentBinding>(inflater, R.layout.edit_fragment, container, false)

        adapter = EventAdapter(requireContext())

        adapter.setOnClickListener(object : EventAdapter.OnClickListener {

            override fun onClickSetImage(position: Int) {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.type = "image/*"
                startActivityForResult(intent, REQUEST_IMAGE)
            }
        })

        binding.listEvent.layoutManager = LinearLayoutManager(requireContext())
        binding.listEvent.setHasFixedSize(true)
        binding.listEvent.adapter = adapter

        binding.buttonAddEvent.setOnClickListener {
            adapter.documents.add(
                0,
                Document(
                    event = Event(
                        device_id = requireContext().getSharedPreferences(
                            MainActivity.PREF_UNIQUE_ID,
                            Context.MODE_PRIVATE
                        ).getString("UUID", "") ?: "", name = getString(R.string.new_event)
                    )
                )
            )
            adapter.notifyItemInserted(0)
            binding.listEvent.scrollToPosition(0)
        }

        binding.buttonPlayEvents.setOnClickListener {
            val action = EditFragmentDirections.actionEditToPlay(adapter.documents.toTypedArray())
            findNavController().navigate(action)
        }

        binding.buttonSaveEvents.setOnClickListener {
            binding.buttonSaveEvents.isEnabled = false
            binding.progress.visibility = View.VISIBLE
            binding.listEvent.visibility = View.GONE
            viewModel.saveEvents(adapter.documents, adapter.needDeleteDocuments)
        }

        viewModel.getEvents().observe(viewLifecycleOwner, Observer {
            it?.also {
                binding.buttonSaveEvents.isEnabled = true
                binding.progress.visibility = View.GONE
                binding.listEvent.visibility = View.VISIBLE
                AnimationManager.appearEditEventAnimation(binding.listEvent){}

                val documents = ArrayList<Document>()

                it.forEach { document ->
                    val event = document.toObject(Event::class.java)
                    event?.also {
                        documents.add(Document(id = document.id, event = event))
                    }
                }

                adapter.documents = ArrayList(documents.sortedBy { it.event.wareki * -1})
                adapter.needDeleteDocuments = ArrayList()
            }
        })

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, result: Intent?) {
        if (resultCode != Activity.RESULT_OK) return
        if (result == null) return

        when (requestCode) {
            REQUEST_IMAGE -> {
                val uri = result.data

                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)

                    val url = "${FirebaseRepository.uuid}/${System.currentTimeMillis()}"

                    viewModel.uploadImage(bitmap,url,endListener = {
                        adapter.setImageURL("https://storage.googleapis.com/eoh-team-w.appspot.com/$url")
                    })

                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.e("error", "can't set image")
                }

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_eop, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.action_credit -> {
                findNavController().navigate(R.id.action_edit_to_credit)
            }
        }

        return super.onOptionsItemSelected(item)
    }

}
