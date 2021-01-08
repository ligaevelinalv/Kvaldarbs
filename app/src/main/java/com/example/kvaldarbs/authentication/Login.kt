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
    //instance declaration
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth

    //log tag definition
    val TAG = "droidsays"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //init
        database = Firebase.database.reference
        auth = Firebase.auth

        auth.currentUser?.let {
            onAuthSuccess()
        }

        Log.i(TAG, "init passed:")

        logInButton.setOnClickListener {
            Log.i(TAG, "Login listener called")
            signIn(emailField.text.toString(), passwordField.text.toString())
        }


    }

    private fun onAuthSuccess() {

        // Go to MainActivity
        val intent = Intent(this, MainScreen::class.java)
        startActivity(intent)
    }


    private fun signIn(email: String, password: String) {
        Log.d(TAG, "signIn:$email")
        if (!validateForm()) {
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.i(TAG, "signInWithEmail:success")
                    //val user = auth.currentUser
                    startActivity(Intent(this@Login, MainScreen::class.java))

                } else {
                    Log.i(TAG, "signIn:failure", task.exception)
                    val exe = task.exception?.message

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

    private fun validateForm(): Boolean {

        val isValid: Boolean

        isValid = checkForEmpty(arrayListOf(emailField, passwordField))

        return isValid
    }

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

    fun onClick(view: View) {
        Log.i(TAG, "Login listener called")

        startActivity(Intent(this@Login, Register::class.java))
    }

}

