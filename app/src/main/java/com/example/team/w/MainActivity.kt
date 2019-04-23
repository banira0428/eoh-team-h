package com.example.team.w

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.navigation.findNavController
import com.example.team.w.fragments.PlayFragment
import com.example.team.w.models.FirebaseRepository
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val share = getSharedPreferences(PREF_UNIQUE_ID, Context.MODE_PRIVATE)

        if(share.getString("UUID","").isNullOrEmpty()){
            share.edit().putString("UUID", UUID.randomUUID().toString()).apply()
        }

        FirebaseRepository.uuid = share.getString("UUID","") ?: ""

    }

    companion object {
        const val PREF_UNIQUE_ID = "PREF_UNIQUE_ID"
    }

    override fun onBackPressed() {
        if(main_nav_host.childFragmentManager.fragments[0] !is PlayFragment){
            super.onBackPressed()
        }
    }
}
