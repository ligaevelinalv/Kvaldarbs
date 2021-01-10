package com.example.kvaldarbs.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kvaldarbs.R
import com.example.kvaldarbs.mainpage.MainScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SplashscreenActivity : AppCompatActivity() {
    //database variable declaration
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        //database variable initialising
        database = Firebase.database.reference
        auth = Firebase.auth

        navigateToScreen(auth)
    }

    override fun onResume() {
        super.onResume()
        navigateToScreen(auth)

    }

    private fun onAuthSuccess() {
        // Go to MainActivity
        val intent = Intent(this, MainScreen::class.java)
        startActivity(intent)
    }

    private fun onAuthFailed() {
        // Go to Login Activity
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }

    //navigates to main screen if user is authorised, otherwise navigates to login
    fun navigateToScreen(auth: FirebaseAuth) {
        if(auth.currentUser != null){
            onAuthSuccess()
        } else {
            onAuthFailed()
        }
    }
}