package com.example.team.w.fragments

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
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
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import java.io.IOException


class EditFragment : Fragment() {

    companion object {
        fun newInstance() = EditFragment()

        const val REQUEST_IMAGE = 0
    }

    private lateinit var viewModel: EditViewModel

    private lateinit var adapter: EventAdapter

    private lateinit var binding: EditFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProviders.of(this).get(EditViewModel::class.java)

        binding = DataBindingUtil.inflate(inflater, R.layout.edit_fragment, container, false)

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

        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, p1: Int) {
                adapter.deleteEvent(viewHolder.adapterPosition)
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.listEvent)

        binding.buttonAddEvent.setOnClickListener {
            adapter.documents.add(
                0,
                Document(
                    isEditing = true,
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

//        binding.buttonPlayEvents.setOnClickListener {
//            viewModel.saveEvents(adapter.documents, adapter.needDeleteDocuments)
//            val action = EditFragmentDirections.actionEditToPlay(adapter.documents.toTypedArray())
//            findNavController().navigate(action)
//        }

//        binding.buttonSaveEvents.setOnClickListener {
//            binding.buttonSaveEvents.isEnabled = false
//            binding.progress.visibility = View.VISIBLE
//            binding.listEvent.visibility = View.GONE
//            viewModel.saveEvents(adapter.documents, adapter.needDeleteDocuments)
//        }

        viewModel.getEvents().observe(viewLifecycleOwner, Observer {
            it?.also {

                binding.toolbar.menu.findItem(R.id.action_save)?.also {
                    it.isEnabled = true
                }

                binding.buttonSaveEvents.isEnabled = true
                binding.progress.visibility = View.GONE
                binding.listEvent.visibility = View.VISIBLE
                AnimationManager.appearEditEventAnimation(binding.listEvent) {}

                val documents = ArrayList<Document>()

                it.forEach { document ->
                    val event = document.toObject(Event::class.java)
                    event?.also {
                        documents.add(Document(id = document.id, event = event))
                    }
                }

                adapter.documents = ArrayList(documents.sortedBy { it.event.wareki * -1 })
                adapter.needDeleteDocuments = ArrayList()

            }
        })

        val activity = activity as AppCompatActivity?
        activity?.setSupportActionBar(binding.toolbar)
        setHasOptionsMenu(true)

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
                    adapter.setImageURL("https://storage.googleapis.com/eoh-team-w.appspot.com/$url")
                    //adapter.setImageURI(uri)

                    viewModel.uploadImage(bitmap, url, endListener = {
                        adapter.downloadImage()
                    })

                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.e("error", "can't set image")
                }

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_eoh, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.action_credit -> {
                startActivity(Intent(requireContext(), OssLicensesMenuActivity::class.java))
            }
            R.id.action_play -> {
                viewModel.saveEvents(adapter.documents, adapter.needDeleteDocuments, endListener = {})
                val action = EditFragmentDirections.actionEditToPlay(adapter.documents.toTypedArray())
                findNavController().navigate(action)
            }
            R.id.action_save -> {

                item.isEnabled = false

                binding.buttonSaveEvents.isEnabled = false
                binding.progress.visibility = View.VISIBLE
                binding.listEvent.visibility = View.GONE
                viewModel.saveEvents(adapter.documents, adapter.needDeleteDocuments, endListener = {
                    view?.also {
                        val bar = Snackbar.make(it, "保存しました", Snackbar.LENGTH_SHORT)
                        bar.view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
                        bar.show()
                    }
                })
            }

        }

        return super.onOptionsItemSelected(item)
    }

}
