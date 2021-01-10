package com.example.kvaldarbs.authentication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AppCompatActivity
import com.example.kvaldarbs.R
import com.example.kvaldarbs.mainpage.MainScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.quickstart.database.kotlin.models.User
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.emailField
import kotlinx.android.synthetic.main.activity_register.passwordField
import java.lang.Integer.parseInt

class Register : AppCompatActivity() {
    //log tag definition
    val TAG = "droidsays"

    //database variable declaration
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        //database variable initialising
        database = Firebase.database.reference
        auth = Firebase.auth

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //button onclicklistener declaration
        createAccountButton.setOnClickListener {
            if (validateForm()){
                createAccount(emailField.text.toString(), passwordField.text.toString(), phoneField.text.toString().toInt())
            }
        }
    }

    private fun createAccount(email: String, password: String, phone: Int) {

        //firebase task for creating a user account with an email and password
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                //write user data to database if account creation was successful
                Log.i(TAG, "createUserWithEmail:success")
                val currentuserID = auth.currentUser?.uid.toString()

                database.child("users").child(currentuserID).setValue(true)

                //creating user object to be pushed into the databse through a hash map
                val user = User(email, phone, "User")
                val userValues = user.toMap()
                val childUpdates = hashMapOf<String, Any>(
                        "/users/$currentuserID" to userValues
                )
                database.updateChildren(childUpdates)

                val intent = Intent(this, MainScreen::class.java)
                startActivity(intent)

            } else {
                //error message display through switch case
                when (task.exception?.message) {
                    "The email address is badly formatted." -> {
                        emailField.error = task.exception?.message
                    }
                    "The email address is already in use by another account."-> {
                        emailField.error = task.exception?.message
                    }
                    "The given password is invalid. [ Password should be at least 6 characters ]"-> {
                        passwordField.error = "Password should be at least 6 characters long."
                    }
                    else -> {
                        Toast.makeText(baseContext, "Action failed, please check your internet connection.", LENGTH_LONG).show()
                    }
                }
        }
        }
    }

    //basic form validation to see if value entry fields are empty
    private fun validateForm(): Boolean {

        var isValid = true

        if (!checkForEmpty(arrayListOf(phoneField, passwordField, emailField))){
            isValid = false
        }
        if (!checkForLength(arrayListOf(phoneField))){
            isValid = false
        }

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

    fun checkForLength(fields: ArrayList<EditText>): Boolean {
        var isValid = true
        for (item in fields) {
            if (item.text.toString().length != 8) {
                item.error = "Length has to be exactly 8 characters."
                isValid = false
            }
        }
        return isValid
    }

}




















