package com.example.kvaldarbs.dialogs

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.fragment.app.DialogFragment
import com.example.kvaldarbs.R
import com.example.kvaldarbs.authentication.SplashscreenActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.confirmation_alert_dialog_1_butt.content
import kotlinx.android.synthetic.main.confirmation_alert_dialog_1_butt.titleFieldRW
import kotlinx.android.synthetic.main.confirmation_alert_dialog_1_butt.yesButt
import kotlinx.android.synthetic.main.dialog_reauthenticate.*
import kotlin.properties.Delegates
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import kotlinx.android.synthetic.main.activity_login.*


class ReauthenticateDialog: DialogFragment() {
    //log tag definition
    var TAG: String = "droidsays"

    //list that will contain all user offers
    var useroffers = arrayListOf<String>()

    //database variable declaration
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var user: FirebaseUser
    lateinit var currentuserID: String
    lateinit var storage: FirebaseStorage
    lateinit var storageRef: StorageReference

    //callback to do work in the class that the dialog was initialised in
    var callbackreauth: () -> Unit = {}
    var dialogtype: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.dialog)

        //database variable initialising
        database = Firebase.database.reference
        auth = Firebase.auth
        currentuserID = auth.currentUser?.uid.toString()
        user = auth.currentUser!!
        storage = Firebase.storage
        storageRef = storage.reference

        return inflater.inflate(R.layout.dialog_reauthenticate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

        titleFieldRW.text = getString(R.string.reauth)

        //casting values passed through a bundle in navigation
        dialogtype = arguments?.getInt("dialogtype")

        //setting dialog textfields based on type passed in navigation
        when(dialogtype){
            1 -> {
                content.text = getString(R.string.auccount_delete)
            }

            2 -> {
                content.text = getString(R.string.account_edit)
            }

            else -> {
                titleFieldRW.text = getString(R.string.unassigned)
                content.text = getString(R.string.unassigned)
            }
        }

        //button onclicklistener declaration
        yesButt.setOnClickListener {

            if (validateForm()) {
                actBasedOnType(dialogtype)
            }
        }

        noButt.setOnClickListener{
            dismiss()
        }
    }

    //code executed based on type passed in through navigation
    fun actBasedOnType(type: Int?) {

        //both types require firebase reauthentication task to be excecuted
        val credential = EmailAuthProvider.getCredential(emailReauthField.text.toString(), passwordReauthField.text.toString())
        user.reauthenticate(credential).addOnCompleteListener {
            Log.i(TAG, "User re-authenticated.")
            //code excecuted if reauthentication was not successful
            if(!it.isSuccessful) {
                //error message display through switch case
                when(it.exception?.message) {
                    "The email address is badly formatted." -> {
                        emailReauthField.error = it.exception?.message
                    }
                    "The password is invalid or the user does not have a password."-> {
                        passwordReauthField.error = it.exception?.message
                    }
                    "The supplied credentials do not correspond to the previously signed in user." -> {
                        emailReauthField.error = it.exception?.message
                    }
                    "There is no user record corresponding to this identifier. The user may have been deleted." -> {
                        emailReauthField.error = "There is no user record corresponding to this identifier."
                    }
                    else -> {
                        Toast.makeText(requireContext(), "Action failed, please check your internet connection.", LENGTH_LONG).show()
                    }

                }

                return@addOnCompleteListener
            }

            when(type) {
                1-> {
                    hasOffers()
                }
                2-> {
                    val newemail = arguments?.getString("newemail")
                    if (newemail != null) {
                        changeEmail(newemail)
                    }
                }
            }
        }
    }

/**----------------------CODE BLOCK FOR DELETING USER----------------------**/
    //firebase listener for retreiving all offers that the user has
    fun hasOffers(){
        val allItemsQuery = database.child("users").child(currentuserID).child("offers")

        allItemsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (productSnapshot in dataSnapshot.children) {
                    //adding all offers to list
                    useroffers.add(productSnapshot.key.toString())
                }

                queryValueListener()
                
            }
            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG, "query fetching error: " + error.toException().toString())
            }
        })
    }

    //function that checks if any of the offers have been ordered by another user
    fun queryValueListener() {
        val allItemsQuery = database.child("products")
        //boolean variable with an observer, triggers if there is at least one offer that has been ordered
        //or the async query has finished
        var hasOffers: Boolean by Delegates.observable(false) { _, oldValue, newValue ->
            asyncListener(newValue)
        }

        allItemsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var temp = false
                for (productSnapshot in dataSnapshot.children) {
                    if (useroffers.contains(productSnapshot.key) ) {
                        //query breaks if there is at least one offer that has been ordered
                        if (productSnapshot.child("isordered").value.toString().toBoolean()) {
                            temp = true
                            break
                        }
                    }
                }
                hasOffers = temp
                Log.i(TAG, useroffers.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG, "query fetching error: " + error.toException().toString())
            }
        })
    }

    //listener function that excecutes when hasOffers value changes
    fun asyncListener(value: Boolean){
        when(value) {
            true-> {

                //warning message when user profile has ordered offers
                Toast.makeText(requireContext(), "Cannot delete profile if it has offers that have been accepted.",
                        LENGTH_LONG).show()
                dismiss()
            }
            false -> {
                Log.i(TAG, "Account will be deleted")

                deleteUserData()
                callbackreauth()
            }
        }
    }

    fun deleteUserData() {

        //boolean variable with an observer, triggers when user attached images and products
        //have successfully been deleted
        var dataDeleted: Boolean by Delegates.observable(false) { _, _, _ ->
            deleteUser()
        }

        //User added image deletion from Firebase Storage
        Log.i(TAG, "user data to be deleted")

        //delete offers from Firebase Realtime database if user has any
        if (useroffers.count() != 0){
            var imgList = storageRef.child("userproductimages").child(currentuserID).listAll()
                .addOnSuccessListener { (items, _) ->

                    items.forEach { item ->
                        val imgRef = storageRef.child("userproductimages").child(currentuserID).child(item.name)
                        Log.i(TAG, imgRef.toString())

                        imgRef.delete().addOnSuccessListener {
                            Log.i(TAG, "Image deleted successfully")

                                for (item in useroffers) {
                                    Log.i(TAG, "deleting offers")
                                    database.child("products").child(item).removeValue()
                                }

                            //delete user information from Firebase Realtime database
                            database.child("users").child(currentuserID).removeValue()
                            dataDeleted = true

                        }.addOnFailureListener {Log.i(TAG, "inner " + it.toString())}
                    }
                }
                .addOnFailureListener {Log.i(TAG, "outer" + it.toString())}
        }

        //delete user information from Firebase Realtime database for user that has no offers
        else {
            database.child("users").child(currentuserID).removeValue()
            dataDeleted = true
        }
    }

    //listener function that excecutes when dataDeleted value changes
    //user is deleted from Firebase Authentication
    fun deleteUser(){

        user.delete().addOnCompleteListener{}
        Firebase.auth.signOut()
    }

/**----------------------CODE BLOCK FOR CHANGING EMAIL----------------------**/
    fun changeEmail(email: String){
        val user = Firebase.auth.currentUser

    //update user email in Firebase Authentication
        user!!.updateEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i(TAG, "User email address updated.")
                    dismiss()
                    callbackreauth()
                }
                else {
                    //error message display through switch case
                    if (task.exception?.message == "The email address is badly formatted.") {
                        Toast.makeText(requireContext(), "The email address is badly formatted.", LENGTH_LONG).show()
                    }
                    if (task.exception?.message == "The email address is already in use by another account.") {
                        Toast.makeText(requireContext(), "The email address is badly formatted.", LENGTH_LONG).show()
                    }
                    else {
                        Toast.makeText(requireContext(), "Action failed, please check internet connection.", LENGTH_LONG).show()
                    }
                }
            }

    }

    //basic form validation to see if value entry fields are empty
    private fun validateForm(): Boolean {

        val isValid: Boolean

        isValid = checkForEmpty(arrayListOf(emailReauthField, passwordReauthField))

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
}












