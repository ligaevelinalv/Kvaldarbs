package com.example.kvaldarbs.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.example.kvaldarbs.R
import com.example.kvaldarbs.dialogs.ReauthenticateDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.quickstart.database.kotlin.models.User
import kotlinx.android.synthetic.main.fragment_edit_profile.*

class EditProfileFragment : Fragment() {
    //log tag definition
    var TAG: String = "droidsays"

    //database variable declaration
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var currentUser: String
    lateinit var keyref: DatabaseReference

    var emailValue: String = ""
    var phoneValue: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //database variable initialising
        database = Firebase.database.reference
        auth = Firebase.auth
        currentUser = auth.currentUser?.uid.toString()
        keyref = database.child("users").child(currentUser)

        val profileQuery = keyref
        //query for loading user data into the layout
        profileQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val product = dataSnapshot.getValue<User>()

                product?.let {
                    emailEditField.setText(it.email)
                    phoneEditField.setText(it.phone.toString())
                    emailValue = it.email
                    phoneValue = it.phone
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG, "query fetching error: " + error.toException().toString())
            }
        })

        (activity as ProfileHostActivity).supportActionBar?.title = "Edit Profile"

        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //button onclicklistener declaration
        editProfileButton.setOnClickListener {
            if (validateForm()) {
                if (emailValue != emailEditField.text.toString()){

                    navigateToEdit()
                }
                if (phoneValue != phoneEditField.text.toString().toInt()){
                    database.child("users").child(currentUser).child("phone").setValue(phoneEditField.text.toString().toInt())
                }
            }
        }
    }

    //basic form validation to see if value entry fields are empty
    private fun validateForm(): Boolean {

        var isValid = true

        if (!checkForEmpty(arrayListOf(emailEditField, phoneEditField))){
            isValid = false
        }
        if (!checkForLength(arrayListOf(phoneEditField))){
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

    fun checkForLength(fields: ArrayList<EditText>): Boolean{
        var isValid = true
        for (item in fields) {
            if (item.text.toString().length != 8){
                item.error = "Length has to be exactly 8 characters."
                isValid = false
            }
        }
        return isValid
    }

    //callback that excecutes when user has reauthenticated to update the user data in the database
    fun navigateToEdit(){
        val reauthdialog = ReauthenticateDialog()
        val bundle = Bundle()
        reauthdialog.callbackreauth = {
            database.child("users").child(currentUser).child("email").setValue(emailEditField.text.toString())
        }

        bundle.putInt("dialogtype", 2)
        bundle.putString("newemail", emailEditField.text.toString())
        reauthdialog.arguments = bundle
        reauthdialog.show(parentFragmentManager, "")
    }

}