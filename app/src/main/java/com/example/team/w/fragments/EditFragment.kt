package com.example.team.w.fragments

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
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
                startActivityForResult(intent, position)
            }

            override fun onClickDelete(position: Int) {
                deleteEvent(position)
            }
        })

        binding.listEvent.setHasFixedSize(true)
        binding.listEvent.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, p1: Int) {
                deleteEvent(viewHolder.adapterPosition)
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

        viewModel.getEvents().observe(viewLifecycleOwner, Observer {
            it?.also {

                binding.toolbar.menu.findItem(R.id.action_save)?.also {
                    it.isEnabled = true
                }

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

        val uri = result.data
        adapter.setImageURI(requestCode,uri)

        try {
            val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)

            val url = "${FirebaseRepository.uuid}/${System.currentTimeMillis()}"
            adapter.setImageURL(requestCode,getString(R.string.img_url,url))

            viewModel.uploadImage(bitmap, url)

        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("error", "can't set image")
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

                if (adapter.documents.isEmpty()) {
                    view?.also {
                        val bar = Snackbar.make(it, getString(R.string.msg_empty), Snackbar.LENGTH_SHORT)
                        bar.view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
                        bar.show()
                    }
                } else {
                    viewModel.saveEvents(adapter.documents, adapter.needDeleteDocuments,{})
                    findNavController().navigate(EditFragmentDirections.actionEditToPlay(adapter.documents.toTypedArray()))
                }
            }
            R.id.action_save -> {

                if(!isConnected()){
                    view?.also {
                        val bar = Snackbar.make(it, getString(R.string.msg_failure_save), Snackbar.LENGTH_SHORT)
                        bar.view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
                        bar.show()
                    }
                }else{
                    item.isEnabled = false

                    binding.progress.visibility = View.VISIBLE
                    binding.listEvent.visibility = View.GONE
                    viewModel.saveEvents(adapter.documents, adapter.needDeleteDocuments, endListener = {
                        view?.also {
                            val bar = Snackbar.make(it, getString(R.string.msg_save), Snackbar.LENGTH_SHORT)
                            bar.view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
                            bar.show()
                        }
                    })
                }
            }
            R.id.action_comment -> {
                findNavController().navigate(EditFragmentDirections.actionEditToStamp())
            }

        }

        return super.onOptionsItemSelected(item)
    }

    private fun deleteEvent(position: Int) {
        adapter.deleteEvent(position)
        view?.also {
            val bar = Snackbar.make(it, getString(R.string.msg_delete), Snackbar.LENGTH_LONG)
            bar.view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
            bar.setAction(getString(R.string.msg_restore)) {
                adapter.restoreEvent(position)
            }
            bar.setActionTextColor(Color.WHITE)
            bar.show()
        }
    }

    private fun isConnected(): Boolean {
        val cm = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

}
