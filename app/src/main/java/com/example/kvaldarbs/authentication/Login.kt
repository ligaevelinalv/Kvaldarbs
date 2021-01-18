package com.example.kvaldarbs.authentication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kvaldarbs.R
import com.example.kvaldarbs.mainpage.MainScreen

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.quickstart.database.kotlin.models.User
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.emailField
import kotlinx.android.synthetic.main.activity_login.passwordField
import kotlinx.android.synthetic.main.activity_register.*


class Login : AppCompatActivity() {
    //database variable declaration
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth

    //log tag definition
    val TAG = "droidsays"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //database variable initialising
        database = Firebase.database.reference
        auth = Firebase.auth


        Log.i(TAG, "init passed:")

        //button onclicklistener declaration
        logInButton.setOnClickListener {
            Log.i(TAG, "Login listener called")
            signIn(emailField.text.toString(), passwordField.text.toString())
        }
    }

    //method for signing into the app
    private fun signIn(email: String, password: String) {
        Log.d(TAG, "signIn:$email")
        if (!validateForm()) {
            return
        }

        //firebase task for signing in with email and password
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // navigate to mainscreen if authentication was successful
                    Log.i(TAG, "signInWithEmail:success")
                    //navigate to main screen activity
                    startActivity(Intent(this@Login, MainScreen::class.java))

                } else {
                    Log.i(TAG, "signIn:failure", task.exception)

                    //error message display through switch case
                    when (task.exception?.message) {
                        "The email address is badly formatted." -> {
                            emailField.error = task.exception?.message
                        }
                        "The password is invalid or the user does not have a password."-> {
                            passwordField.error = task.exception?.message
                        }
                        "There is no user record corresponding to this identifier. The user may have been deleted."-> {
                            emailField.error = "There is no user record corresponding to this identifier."
                        }
                        else -> {
                            Toast.makeText(baseContext, "Action failed, please check your internet connection.", Toast.LENGTH_LONG).show()
                        }
                    }

                }
            }
    }

    //basic form validation to see if value entry fields are empty
    private fun validateForm(): Boolean {
        val isValid: Boolean
        isValid = checkForEmpty(arrayListOf(emailField, passwordField))

        return isValid
    }

    //if a text field is empty, display error indicator
    fun checkForEmpty(fields: ArrayList<EditText>): Boolean{
        var isValid = true
        for (item in fields) {
            if (item.text.toString() == "") {
                item.error = "Field cannot be empty."
                isValid = false
            }
        }

        return isValid
    }

    //onclick function to open registration screen
    fun onClick(view: View) {
        Log.i(TAG, "Login listener called")
        startActivity(Intent(this@Login, Register::class.java))
    }

    override fun onBackPressed() {
        
    }

}

