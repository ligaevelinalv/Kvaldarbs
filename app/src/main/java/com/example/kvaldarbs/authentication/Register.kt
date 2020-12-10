package com.example.kvaldarbs.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kvaldarbs.R
import com.example.kvaldarbs.mainpage.MainScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*

class Register : AppCompatActivity() {
    //instance declaration
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth

    //log tag definition
    val TAG = "droidsays"

    override fun onCreate(savedInstanceState: Bundle?) {

        database = Firebase.database.reference
        auth = Firebase.auth

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        createAccountButton.setOnClickListener {
            Toast.makeText(baseContext, "AAAAAAAAA",
                Toast.LENGTH_SHORT).show()
            Log.i(TAG, "new acc listener called")
            createAccount(emailField.text.toString(), passwordField.text.toString())
            Log.i(TAG, "new acc listener executed")
            val intent = Intent(this, MainScreen::class.java)
            startActivity(intent)
        }

        val bundle: Bundle? = intent.extras
        if (bundle != null){
            val id = bundle.get("id_value")
            val language = bundle.get("language_value")
            Toast.makeText(applicationContext,id.toString()+" "+language,Toast.LENGTH_LONG).show()
        }

    }



    private fun createAccount(email: String, password: String) {
        Log.i(TAG, "createAccount:$email")

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.i(TAG, "createUserWithEmail:success")
                    //val user = auth.currentUser

                } else {
                    // If sign in fails, display a message to the user.
                    Log.i(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }

    }
}