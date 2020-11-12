package com.example.kvaldarbs.Navigation

//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import com.example.kvaldarbs.R
//import kotlinx.android.synthetic.main.fragment_register.*
//
//
//class RegisterFragment : Fragment() {
//
//    lateinit var passedval: String
//    lateinit var passedval2: String
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_register, container, false)
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        passedval = arguments?.getString("entrytext").toString()
//        passedval2 = arguments?.getString("password").toString()
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        val message = "Your name is $passedval"
//        val message2 = "Your name is $passedval2"
//        intro1.text = message
//        intro2.text = message2
//    }
//}