package com.example.team.w.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.team.w.R
import com.example.team.w.adapters.EventAdapter
import com.example.team.w.databinding.EditFragmentBinding
import com.example.team.w.models.Document
import com.example.team.w.models.Event
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

        adapter = EventAdapter()

        adapter.setOnClickListener(object : EventAdapter.OnClickListener {
            override fun onClickSetDate(position: Int) {
                val dialog = DatePickerDialog(requireContext())

                dialog.datePicker.touchables[0].performClick()
                dialog.setOnDateSetListener { datePicker, year, month, day ->
                    Toast.makeText(requireContext(),"$year/$month/$day",Toast.LENGTH_SHORT).show()
                }
                dialog.show()
            }

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
            adapter.documents.add(0, Document())
            adapter.notifyItemInserted(0)
            binding.listEvent.scrollToPosition(0)
        }

        binding.buttonPlayEvents.setOnClickListener {
            findNavController().navigate(R.id.action_edit_to_play)
        }

        binding.buttonSaveEvents.setOnClickListener {
            viewModel.saveEvents(adapter.documents,adapter.needDeleteDocuments)
        }

        viewModel.getEvents().observe(viewLifecycleOwner, Observer {
            it?.also{
                val documents = ArrayList<Document>()

                it.forEach { document ->
                    val event = document.toObject(Event::class.java)
                    event?.also {
                        documents.add(Document(id = document.id,event = event))
                    }
                }

                adapter.documents = documents
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
                    adapter.setImageResource(uri ?: Uri.EMPTY)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.e("error","can't set image")
                }

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_eop,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId){
            R.id.action_credit ->{
                findNavController().navigate(R.id.action_edit_to_credit)
            }
        }

        return super.onOptionsItemSelected(item)
    }

}
