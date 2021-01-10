package com.example.kvaldarbs.offerflow

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.kvaldarbs.R
import com.example.kvaldarbs.mainpage.MainScreen

class OfferFlowScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer_flow_screen)

        setSupportActionBar(findViewById(R.id.offertoolbar))

        //navigation controller initialising
        val navController = this.findNavController(R.id.offerNavHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navController)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    //menu setup
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.offer_toolbar, menu)
        return true
    }

    //setup for back arrow navigation
    override fun onSupportNavigateUp(): Boolean {
        startActivity(Intent(this, MainScreen::class.java))
        return true
    }

}