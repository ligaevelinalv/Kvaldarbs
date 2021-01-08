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
            if (validateForm()){
                Log.i(TAG, "new acc listener called")
                createAccount(emailField.text.toString(), passwordField.text.toString(), phoneField.text.toString().toInt())
                Log.i(TAG, "new acc listener executed")
            }
        }

        val bundle: Bundle? = intent.extras
        if (bundle != null){
            val id = bundle.get("id_value")
            val language = bundle.get("language_value")
            Toast.makeText(applicationContext,id.toString()+" "+language,Toast.LENGTH_LONG).show()
        }

    }

    private fun createAccount(email: String, password: String, phone: Int) {
        Log.i(TAG, "createAccount:$email")

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.i(TAG, "createUserWithEmail:success")
                    val currentuserID = auth.currentUser?.uid.toString()

                    database.child("users").child(currentuserID).setValue(true)

                    val user = User(email, phone, "User")
                    val userValues = user.toMap()
                    val childUpdates = hashMapOf<String, Any>(
                            "/users/$currentuserID" to userValues
                    )
                    database.updateChildren(childUpdates)
                    Log.i(TAG, "push user data to db:success")

                    val intent = Intent(this, MainScreen::class.java)
                    startActivity(intent)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.i(TAG, "createUserWithEmail:failure", task.exception)

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

//                    if (task.exception?.message == "The email address is badly formatted.") {
//                        emailField.error = task.exception?.message
//                    }
//                    if (task.exception?.message == "The email address is already in use by another account.") {
//                        emailField.error = task.exception?.message
//                    }
//                    if (task.exception?.message == "The given password is invalid. [ Password should be at least 6 characters ]") {
//                        passwordField.error = "Password should be at least 6 characters long."
//                    }
//                    else {
//                        Toast.makeText(baseContext, "Action failed, please check internet connection.", Toast.LENGTH_LONG).show()
//                    }
                }
            }

    }

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




















