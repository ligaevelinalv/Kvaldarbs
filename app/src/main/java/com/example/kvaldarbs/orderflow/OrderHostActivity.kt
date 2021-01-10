package com.example.kvaldarbs.orderflow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.kvaldarbs.R
import com.example.kvaldarbs.libs.utils.OfferViewModel
import com.example.kvaldarbs.libs.utils.OrderViewModel
import com.example.kvaldarbs.mainpage.MainScreen

class OrderHostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_host)

        setSupportActionBar(findViewById(R.id.order_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //casting value that was recieved from FrontpageFragment and setting it as the value of the OrderViewModel
        val model: OrderViewModel by viewModels()
        val value = intent.getStringExtra("key")
        if (value != null) {
            model.setValue(value)
        }
    }

    //menu setup
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.order_toolbar, menu)
        return true
    }

    //setup for back arrow navigation
    override fun onSupportNavigateUp(): Boolean {
        startActivity(Intent(this, MainScreen::class.java))
        return true
    }
}