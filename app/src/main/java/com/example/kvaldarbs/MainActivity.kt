package com.example.kvaldarbs

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*



data class Ded(
    var ree: String? = "",
    var ree2: String? = ""
)

class MainActivity : AppCompatActivity() {
    //instance declaration
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth

    //log tag definition
    val TAG = "monitor"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        //init
        database = Firebase.database.reference
        auth = Firebase.auth

        Log.i(TAG, "asfasfasfasf:")

        createall.setOnClickListener {
            insertInDB("aaaaaaa", "beeaaaaaee")

        }

        deletechild.setOnClickListener {
            deleteChildFromDB()

        }

        emailCreateAccountButton.setOnClickListener {
            Toast.makeText(baseContext, "AAAAAAAAA",
                    Toast.LENGTH_SHORT).show()
            Log.i(TAG, "new acc listener called")
            createAccount(fieldEmail.text.toString(), fieldPassword.text.toString())
            Log.i(TAG, "new acc listener executed")
        }
    }


    private fun insertInDB(ree: String?, ree2: String?) {
        val bleee = Ded(ree, ree2)
        database.child("deth").child(ree.toString()).setValue(bleee)
        database.child("deth").child("frik").child(ree.toString()).setValue(bleee)
    }

    private fun deleteChildFromDB(){
        database.child("deth").child("aaaaaaa").removeValue()
    }

    private fun createAccount(email: String, password: String) {
        Log.i(TAG, "createAccount:$email")

        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.i(TAG, "createUserWithEmail:success")
                        val user = auth.currentUser

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.i(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()

                    }

                }
    }
}

