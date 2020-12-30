package com.example.kvaldarbs.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.kvaldarbs.R
import com.example.kvaldarbs.authentication.Login
import com.example.kvaldarbs.authentication.Register
import com.example.kvaldarbs.dialogs.PopUpDialog1Butt
import com.example.kvaldarbs.dialogs.TAG
import com.example.kvaldarbs.mainpage.MainScreen
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        editProfileImage.setOnClickListener{
//            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
//        }
//
//        editProfileLabel.setOnClickListener{
//            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
//        }
//
//        deleteProfileButton.setOnClickListener {
//            val ree = PopUpDialog1Butt()
//            val bundle = Bundle()
//            ree.aaa = {
//                navigateToConfirm()
//            }
//
//            bundle.putInt("dialogtype", 3)
//            ree.arguments = bundle
//            ree.show(parentFragmentManager, "")
//        }
    }

    fun navigateToConfirm(){
        startActivity(Intent(requireContext(), Login::class.java))

    }

}