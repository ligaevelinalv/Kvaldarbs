package com.example.kvaldarbs.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.kvaldarbs.R
import com.example.kvaldarbs.authentication.SplashscreenActivity
import com.example.kvaldarbs.dialogs.ReauthenticateDialog
import com.example.kvaldarbs.dialogs.TAG
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
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var keyref: DatabaseReference
    lateinit var user: FirebaseUser
    lateinit var currentuserID: String
    lateinit var storage: FirebaseStorage
    lateinit var storageRef: StorageReference
    lateinit var profileQuery: DatabaseReference

    var email: String = ""
    var role: String = ""
    var phone: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        database = Firebase.database.reference
        auth = Firebase.auth
        storage = Firebase.storage
        storageRef = storage.reference
        currentuserID = auth.currentUser?.uid.toString()
        keyref = database.child("users").child(currentuserID)

        profileQuery= keyref

        profileQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val usersnapshot = dataSnapshot.getValue<User>()

                usersnapshot?.let {
                    email = it.email
                    role = it.phone.toString()
                    phone = it.role
                }
                refreshUI()
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

        editProfileImage.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        editProfileLabel.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        deleteProfileButton.setOnClickListener {
            val ree = ReauthenticateDialog()
            val bundle = Bundle()
            ree.aaa = {
                navigateToConfirm()
            }

            bundle.putInt("dialogtype", 1)
            ree.arguments = bundle
            ree.show(parentFragmentManager, "")
        }
    }

    fun navigateToConfirm(){
        val intent = Intent(requireContext(), SplashscreenActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    fun refreshUI(){
        profEmailField.text = email
        profPhoneField.text = phone
        profRoleField.text = role
    }

}