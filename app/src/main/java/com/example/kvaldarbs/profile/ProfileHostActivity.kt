package com.example.kvaldarbs.profile

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.kvaldarbs.R
import com.example.kvaldarbs.authentication.Register
import com.example.kvaldarbs.mainpage.MainScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileHostActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_host)
        setSupportActionBar(findViewById(R.id.profile_toolbar))

        auth = Firebase.auth

        //navigation controller initialising
        val navController = this.findNavController(R.id.profileNavHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navController)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    //setup for back arrow navigation
    override fun onSupportNavigateUp(): Boolean {
        startActivity(Intent(this, MainScreen::class.java))
        return true
    }

    //menu setup
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.profile_toolbar, menu)
        return true
    }

}