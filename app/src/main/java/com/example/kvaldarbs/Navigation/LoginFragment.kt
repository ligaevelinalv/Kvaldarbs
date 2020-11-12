package com.example.kvaldarbs.Navigation

//import android.content.ContentValues
//import android.os.Bundle
//import android.text.TextUtils
//import android.util.Log
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import com.example.kvaldarbs.R
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.ktx.auth
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.ktx.database
//import com.google.firebase.ktx.Firebase
//import kotlinx.android.synthetic.main.fragment_login.emailField
//import kotlinx.android.synthetic.main.fragment_login.logInButton
//import kotlinx.android.synthetic.main.fragment_login.passwordField
//
//
//class LoginFragment : Fragment() {
//
//    //instance declaration
//    lateinit var database: DatabaseReference
//    lateinit var auth: FirebaseAuth
//
//    //log tag definition
//    val TAG = "monitor"
//    var valid = true
//
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        // Inflate the layout for this fragment
//
//        return inflater.inflate(R.layout.fragment_login, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        database = Firebase.database.reference
//        auth = Firebase.auth
//
//        Log.i(TAG, "init passed:")
//
//        logInButton.setOnClickListener {
//            Log.i(TAG, "Login listener called")
//            signIn(emailField.text.toString(), passwordField.text.toString())
//
//            if (valid) {
//                Log.i(TAG, "form is valid")
//            }
//        }
//
//        logInButton.setOnClickListener {
//
//        }
//
//
//    }
//
//    private fun signIn(email: String, password: String) {
//        Log.d(ContentValues.TAG, "signIn:$email")
//        if (!validateForm()) {
//            return
//        }
//
//        auth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener() { task ->
//                    if (task.isSuccessful) {
//                        // Sign in success, update UI with the signed-in user's information
//                        Log.i(ContentValues.TAG, "signInWithEmail:success")
//                        val user = auth.currentUser
//                    } else {
//                        // If sign in fails, display a message to the user.
//                        Log.i(ContentValues.TAG, "signIn:failure", task.exception)
//                        Toast.makeText(activity, "Authentication failed.",
//                                Toast.LENGTH_SHORT).show()
//                    }
////                if (!task.isSuccessful) {
////                    //binding.status.setText(R.string.auth_failed)
////                }
//                }
//    }
//
//    private fun validateForm(): Boolean {
//
//        val email = emailField.text.toString()
//        if (TextUtils.isEmpty(email)) {
//            emailField.error = "Required."
//            valid = false
//        } else {
//            emailField.error = null
//        }
//
//        val password = passwordField.text.toString()
//        if (TextUtils.isEmpty(password)) {
//            passwordField.error = "Required."
//            valid = false
//        } else {
//            passwordField.error = null
//        }
//
//        return valid
//    }
//}


