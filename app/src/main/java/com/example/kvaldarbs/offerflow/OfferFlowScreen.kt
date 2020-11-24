package com.example.kvaldarbs.offerflow

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

class OfferFlowScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer_flow_screen)

        setSupportActionBar(findViewById(R.id.offertoolbar))


        val navController = this.findNavController(R.id.offerNavHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navController)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.offer_toolbar, menu)
        return true
    }


//    override fun onSupportNavigateUp(): Boolean {
//        val navController = this.findNavController(R.id.offerNavHostFragment)
//        return navController.navigateUp()
//    }

}