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
import com.example.kvaldarbs.authentication.Register
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

    var useroffers = arrayListOf<String>()
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var user: FirebaseUser
    lateinit var currentuserID: String
    lateinit var storage: FirebaseStorage
    lateinit var storageRef: StorageReference

    var aaa: () -> Unit = {}
    var dialogtype: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        database = Firebase.database.reference
        auth = Firebase.auth
        currentuserID = auth.currentUser?.uid.toString()
        user = auth.currentUser!!
        storage = Firebase.storage
        storageRef = storage.reference

        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.dialog)

        return inflater.inflate(R.layout.dialog_reauthenticate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

        titleFieldRW.text = getString(R.string.reauth)

        dialogtype = arguments?.getInt("dialogtype")

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

        yesButt.setOnClickListener {

            if (validateForm()) {
                actBasedOnType(dialogtype)
            }

        }

        noButt.setOnClickListener{
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        if (auth.currentUser == null) {
            val intent = Intent(requireContext(), SplashscreenActivity::class.java)
            startActivity(intent)
        }
    }

    fun actBasedOnType(type: Int?) {

        val credential = EmailAuthProvider.getCredential(emailReauthField.text.toString(), passwordReauthField.text.toString())
        user.reauthenticate(credential).addOnCompleteListener {
            Log.i(TAG, "User re-authenticated.")
            if(!it.isSuccessful) {
//                Log.i(TAG, it.exception.toString())
//                Log.i(TAG, it.toString())
//                Toast.makeText(requireContext(), "Reauthentication failed, check internet connection", LENGTH_LONG).show()
                val exe = it.exception?.message
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
                        Toast.makeText(requireContext(), "Action failed, please check your internet connection.", Toast.LENGTH_LONG).show()
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
                else -> {
                    Toast.makeText(requireContext(), "Something went wrong, try again later.",
                        LENGTH_LONG).show()
                }
            }
        }
    }

    fun hasOffers(){
        val allItemsQuery = database.child("users").child(currentuserID).child("offers")

        allItemsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (productSnapshot in dataSnapshot.children) {
//                    Log.i(TAG, productSnapshot.toString())
                    useroffers.add(productSnapshot.key.toString())
                }
                //Log.i(TAG, useroffers.toString())
                queryValueListener()
                
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG, "query fetching error: " + error.toException().toString())
            }
        })
    }

    fun queryValueListener() {
        val allItemsQuery = database.child("products")
        var hasOffers: Boolean by Delegates.observable(false) { _, oldValue, newValue ->
            asyncListener(newValue)
        }

        allItemsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var temp = false
                for (productSnapshot in dataSnapshot.children) {
                    if (useroffers.contains(productSnapshot.key) ) {
                        if (productSnapshot.child("isordered").value.toString().toBoolean()) {
                            Log.i(TAG, productSnapshot.child("title").value.toString() + " has been ordered")
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

    fun asyncListener(value: Boolean){
        when(value) {
            true-> {
                dismiss()
                Toast.makeText(requireContext(), "Cannot delete profile if it has offers that have been accepted.",
                        LENGTH_LONG).show()
            }
            false -> {
                Log.i(TAG, "Account will be deleted")


                deleteUserData()
                aaa()
            }
        }
    }

    fun deleteUserData() {

        var dataDeleted: Boolean by Delegates.observable(false) { _, _, _ ->
            deleteUser()
        }

        //STORAGE
        Log.i(TAG, "user data to be deleted")

        if (useroffers.count() != 0){
            var imgList = storageRef.child("userproductimages").child(currentuserID).listAll()
                .addOnSuccessListener { (items, _) ->
                    items.forEach { item ->
                        val imgRef = storageRef.child("userproductimages").child(currentuserID).child(item.name)
                        Log.i(TAG, imgRef.toString())
                        imgRef.delete().addOnSuccessListener {
                            Log.i(TAG, "Image deleted successfully")

                            //REALTIME DATABASE- PRODUCTS
                            if (useroffers.count()!= 0) {
                                for (item in useroffers) {
                                    Log.i(TAG, "deleting offers")
                                    database.child("products").child(item).removeValue()
                                }
                            }
                            //REALTIME DATABASE- USER DATA
                            database.child("users").child(currentuserID).removeValue()
                            dataDeleted = true

                        }.addOnFailureListener {
                            //Log.i(TAG, "inner " + it.toString())
                        }
                    }
                }
                .addOnFailureListener {
                    //Log.i(TAG, "outer" + it.toString())
                }
        }

        //user has not offered any items
        else {
            //REALTIME DATABASE- USER DATA
            database.child("users").child(currentuserID).removeValue()
            dataDeleted = true
        }
    }

    fun deleteUser(){
        user.delete().addOnCompleteListener { task ->
        }
        Firebase.auth.signOut()
    }

    fun changeEmail(email: String){
        val user = Firebase.auth.currentUser

        user!!.updateEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i(TAG, "User email address updated.")
                    dismiss()
                    aaa()
                }
                else {
                    val exe = task.exception?.message
                    if (task.exception?.message == "The email address is badly formatted.") {
                        Toast.makeText(requireContext(), "The email address is badly formatted.", Toast.LENGTH_LONG).show()
                    }
                    if (task.exception?.message == "The email address is already in use by another account.") {
                        Toast.makeText(requireContext(), "The email address is badly formatted.", Toast.LENGTH_LONG).show()
                    }
                    else {
                        Toast.makeText(requireContext(), "Action failed, please check internet connection.", Toast.LENGTH_LONG).show()
                    }
                }
            }

    }

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












