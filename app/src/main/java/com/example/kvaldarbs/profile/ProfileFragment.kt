package com.example.kvaldarbs.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.kvaldarbs.R
import com.example.kvaldarbs.authentication.Login
import com.example.kvaldarbs.authentication.SplashscreenActivity
import com.example.kvaldarbs.dialogs.PopUpDialog1Butt
import com.example.kvaldarbs.dialogs.ReauthenticateDialog
import com.example.kvaldarbs.mainpage.MainScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.quickstart.database.kotlin.models.User
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.*
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {
    //log tag definition
    var TAG: String = "droidsays"

    //database variable declaration
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var keyref: DatabaseReference
    lateinit var user: FirebaseUser
    lateinit var currentuserID: String
    lateinit var storage: FirebaseStorage
    lateinit var storageRef: StorageReference
    lateinit var profileQuery: DatabaseReference
    lateinit var connectivityref:DatabaseReference

    var email: String = ""
    var role: String = ""
    var phone: String = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        //database variable initialising
        database = Firebase.database.reference
        auth = Firebase.auth
        storage = Firebase.storage
        storageRef = storage.reference
        currentuserID = auth.currentUser?.uid.toString()
        keyref = database.child("users").child(currentuserID)
        user = auth.currentUser!!

        connectivityref = Firebase.database.getReference(".info/connected")
        profileQuery= keyref


        //query to monitor if the device is connected to the internet
        connectivityref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val connected = dataSnapshot.getValue(Boolean::class.java) ?: false

                //query excecuted if device is connected to the internet
                if (connected) {

                    //query to retrieve user data
                    profileQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {

                            val usersnapshot = dataSnapshot.getValue<User>()
                            usersnapshot?.let {
                                email = it.email
                                role = it.role
                                if (role == "Administrator") {
                                    editProfileLabel.visibility = View.GONE
                                    editProfileImage.visibility = View.GONE
                                }
                                phone = it.phone.toString()
                            }
                            refreshUI()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.i(TAG, "query fetching error: " + error.toException().toString())
                        }
                    })
                } else {
                    Log.i(TAG, "not connected")

                    Toast.makeText(requireContext(), "Couldn't load fresh profile data, please check your internet connection.", Toast.LENGTH_LONG).show()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG, "query fetching error: " + error.toException().toString())
            }
        })

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profEmailField.text = email
        profPhoneField.text = phone
        profRoleField.text = role

        //element onclicklistener declaration
        editProfileImage.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        editProfileLabel.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        deleteProfileButton.setOnClickListener {
            val reauthdialog = ReauthenticateDialog()
            val bundle = Bundle()
            reauthdialog.callbackreauth = {
                navigateToConfirm()
            }

            bundle.putInt("dialogtype", 1)
            reauthdialog.arguments = bundle
            reauthdialog.show(parentFragmentManager, "")
        }

        changePasswordButton.setOnClickListener {
            //Firebase Authorisation task that sends a password reset email to the user
            user.email?.let { it1 ->
                Firebase.auth.sendPasswordResetEmail(it1).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.i(TAG, "Email sent.")
                        val ree = PopUpDialog1Butt()
                        val passbundle = Bundle()
                        ree.callback1butt = {
                            onChangeSuccess()
                        }

                        passbundle.putInt("dialogtype", 4)
                        passbundle.putString("reset", user.email)
                        ree.arguments = passbundle
                        ree.show(parentFragmentManager, "")
                    }
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Action failed, please check your internet connection.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    //callback that executes if password reset email has been sent successfully
    fun onChangeSuccess() {
        Log.i(TAG, "Password reset email sent successfully")
    }

    //callback that executes when user deletes profile
    fun navigateToConfirm(){
        val intent = Intent(requireContext(), Login::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        (activity as MainScreen).finish()
        (activity as ProfileHostActivity).finish()
    }

    //resresh layout with updated data
    fun refreshUI(){
        profEmailField.text = email
        profPhoneField.text = phone
        profRoleField.text = role
    }

}