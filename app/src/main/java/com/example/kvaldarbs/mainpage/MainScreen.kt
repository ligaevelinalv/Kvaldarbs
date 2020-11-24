package com.example.kvaldarbs.mainpage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.kvaldarbs.R
import com.example.kvaldarbs.authentication.Login
import com.example.kvaldarbs.offerflow.OfferFlowScreen
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase



val TAG = "monitor"

class MainScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainscreen)

        setSupportActionBar(findViewById(R.id.toolbar))

        val navController = this.findNavController(R.id.navHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if( (item.itemId == R.id.logout) || (item.itemId == R.id.add) ){
            navigateFromMenu(item)
            true
        }

        else {
            super.onOptionsItemSelected(item)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.navHostFragment)
        return navController.navigateUp()
    }

    fun navigateFromMenu(item: MenuItem){
        if (item.itemId == R.id.logout){
            Firebase.auth.signOut()
            startActivity(Intent(this, Login::class.java))

        }
        else {
            startActivity(Intent(this,
                OfferFlowScreen::class.java))

        }
    }
}



























