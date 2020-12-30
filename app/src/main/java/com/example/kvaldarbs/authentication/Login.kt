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
    var valid = true


    public override fun onStart() {
        super.onStart()

        // Check auth on Activity start
        auth.currentUser?.let {
            onAuthSuccess()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //init
        database = Firebase.database.reference
        auth = Firebase.auth

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
                    // If sign in fails, display a message to the user.
                    Log.i(TAG, "signIn:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
//                if (!task.isSuccessful) {
//                    //binding.status.setText(R.string.auth_failed)
//                }
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
                item.error = "Required."
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

