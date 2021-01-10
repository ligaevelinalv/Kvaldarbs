package com.example.kvaldarbs.mainpage

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.kvaldarbs.R
import com.example.kvaldarbs.authentication.Login
import com.example.kvaldarbs.offerflow.OfferFlowScreen
import com.example.kvaldarbs.offerflow.currentuserID
import com.example.kvaldarbs.profile.OffersActivity
import com.example.kvaldarbs.profile.OrdersActivity
import com.example.kvaldarbs.profile.ProfileHostActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_mainscreen.*
import java.io.File

//CameraX library implementation contants
const val KEY_EVENT_ACTION = "key_event_action"
const val KEY_EVENT_EXTRA = "key_event_extra"

class MainScreen : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    //log tag definition
    val TAG = "droidsays"

    //database reference declaration
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var keyref: DatabaseReference

    //toolbar variable declaration
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    lateinit var role: String
    lateinit var nav_Menu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainscreen)
        setSupportActionBar(findViewById(R.id.toolbar))

        //database variable initialising
        database = Firebase.database.reference
        auth = Firebase.auth
        currentuserID = auth.currentUser?.uid.toString()
        keyref = database.child("users").child(currentuserID).child("role")

        //toolbar initialising
        drawer = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)

        //menu initialising
        nav_Menu = nav_view.getMenu()

        //hiding navigation drawer items until role retrieval
        nav_Menu.findItem(R.id.myoffers).setVisible(false)
        nav_Menu.findItem(R.id.myorders).setVisible(false)

        //navigation drawer initialising
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        //toolbar setup
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        //navigation controller initialising
        val navController = this.findNavController(R.id.navHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navController)


    }

    //retrieves user role and sets specific toolbar based on role
    override fun onCreateOptionsMenu(menu: Menu): Boolean {

            val roleQuery = keyref
            roleQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    role = dataSnapshot.value.toString()
                    //inflating role specific toolbar
                    if (role == "Administrator"){
                        menuInflater.inflate(R.menu.admin_toolbar, menu)
                    }
                    if (role == "User") {
                        menuInflater.inflate(R.menu.toolbar, menu)
                        //show navigation drawer items for role
                        nav_Menu.findItem(R.id.myoffers).setVisible(true)
                        nav_Menu.findItem(R.id.myorders).setVisible(true)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.i(TAG, "role loading failed "+ databaseError.toException())
                }
            })

        return true
    }

    //toolbar icon onclick method handling
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if( (item.itemId == R.id.logout) || (item.itemId == R.id.add) ){
            navigateFromMenu(item)
            true
        }

        else {
            Log.i(TAG, item.toString())
            super.onOptionsItemSelected(item)
        }
    }

    //setup for back arrow navigation
    override fun onSupportNavigateUp(): Boolean {
        startActivity(Intent(this, MainScreen::class.java))
        return true
    }

    //toolbar menu navigation setup
    fun navigateFromMenu(item: MenuItem){
        if (item.itemId == R.id.logout){
            Firebase.auth.signOut()
            startActivity(Intent(this, Login::class.java))

        }
        else {
            startActivity(Intent(this, OfferFlowScreen::class.java))
        }
    }

    //companion object for saving images taken with camera
    companion object {

        /** Use external media if it is available, our app's file directory otherwise */
        fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() } }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else appContext.filesDir
        }
    }

    //navigation drawer setup
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    //navigation drawer navigation setup
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.i(TAG, item.itemId.toString())

        //oclick navigation for menu items
        if (item.itemId == R.id.myorders) {
            Log.i(TAG, "order")
            startActivity(Intent(this, OrdersActivity::class.java))
            return true
        }

        if (item.itemId == R.id.myoffers) {
            Log.i(TAG, "offer")
            startActivity(Intent(this, OffersActivity::class.java))
            return true
        }

        if (item.itemId == R.id.myprofile) {
            Log.i(TAG, "profile")
            startActivity(Intent(this, ProfileHostActivity::class.java))
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}




























