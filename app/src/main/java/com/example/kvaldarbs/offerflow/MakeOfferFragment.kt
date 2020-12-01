package com.example.kvaldarbs.offerflow

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kvaldarbs.R
import com.example.kvaldarbs.dialogs.PopUpDialog2Butt
import com.example.kvaldarbs.models.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_detail.offerButton
import kotlinx.android.synthetic.main.fragment_makeoffer.*


val TAG: String = "monitor"
var valid = true
var amountnumber:Int = 1
var id: Int = 0
var typedropdownval:String = ""
var deliverydropdownval:String = ""
var weardropdownval:String = ""
var currentuserID:String = ""


class MakeOfferFragment : Fragment(), AdapterView.OnItemSelectedListener  {
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Firebase.database.reference
        auth = Firebase.auth
        currentuserID = auth.currentUser?.uid.toString()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val rootview = inflater.inflate(R.layout.fragment_makeoffer, container, false)

        val typedropdown: Spinner = rootview.findViewById(R.id.typeDropdown)
        typedropdown.onItemSelectedListener = this

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
                requireContext(),
                R.array.product_type_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            typedropdown.adapter = adapter
        }

        val deliverydropdown: Spinner = rootview.findViewById(R.id.deliveryDropdown)
        deliverydropdown.onItemSelectedListener = this

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
                requireContext(),
                R.array.delivery_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            deliverydropdown.adapter = adapter
        }

        val weardropdown: Spinner = rootview.findViewById(R.id.wearDropdown)
        weardropdown.onItemSelectedListener = this

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
                requireContext(),
                R.array.wear_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            weardropdown.adapter = adapter
        }

        return rootview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        amountNumberText.text = amountnumber.toString()

        val ree = PopUpDialog2Butt()
        val bundle = Bundle()
        ree.aaa = {
            Log.i(TAG, "ree.aaa closure called")

            if (validateForm()) {

                newOffer(titleField.text.toString(),
                        typedropdownval,
                        manufacturerField.text.toString(),
                        weightField.text.toString().toInt(),
                        deliverydropdownval,
                        weardropdownval,
                        amountnumber,
                        descriptionField.text.toString()
                )
                navigateToConfirm()
            }
        }

        offerButton.setOnClickListener {
            //onAlertDialog(view)

            bundle.putInt("dialogtype", 2)
            ree.arguments = bundle
            ree.show(parentFragmentManager, "")
        }

        plusButton.setOnClickListener {
            amountnumber ++
            amountNumberText.text = amountnumber.toString()
        }

        minusButton.setOnClickListener {
            if (amountnumber!=1) {
                amountnumber--
                amountNumberText.text = amountnumber.toString()
            }
        }
    }

    fun navigateToConfirm(){
        Log.i(TAG, "navcontroller to confirmation called")
        findNavController().navigate(R.id.action_makeOfferFragment_to_offerConfirmationFragment)

    }

    private fun newOffer(
            title: String,
            type: String,
            manufacturer: String,
            weight: Int,
            delivery: String,
            wear: String,
            amount: Int,
            description: String
    ) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously

        val key = database.child("products").push().key
        if (key == null) {
            Log.i(TAG, "Couldn't get push key for posts")
            return
        }

        val product = Product(title, type, manufacturer, weight, delivery, wear, amount, description, false)
        val productValues = product.toMap()

//        val offerIDInUsers = Offer(key)
//        val offer = offerIDInUsers.toMap()

        val childUpdates = hashMapOf<String, Any>(
                "/products/$key" to productValues
        )

        database.updateChildren(childUpdates)

        val values: HashMap<String, Any> = HashMap()
        values["$key"] = true
        database.child("users").child(currentuserID).child("offers").updateChildren(values)
    }

    private fun validateForm(): Boolean {

        val title = titleField.text.toString()
        if (TextUtils.isEmpty(title)) {
            titleField.error = "Required."
            valid = false
        }else {
            titleField.error = null
        }

        val manuf = manufacturerField.text.toString()
        if (TextUtils.isEmpty(manuf)) {
            manufacturerField.error = "Required."
            valid = false
        } else {
            manufacturerField.error = null
        }

        val weight = weightField.text.toString()
        if (TextUtils.isEmpty(weight)) {
            weightField.error = "Required."
            valid = false
        }else {
            weightField.error = null
        }

        //some kind of limiter or something, maybe based on the product type
//        val amount = amountnumber
//        if (TextUtils.isEmpty(weight)) {
//            weightField.error = "Required."
//            valid = false
//        }else {
//            weightField.error = null
//        }

        val desc = descriptionField.text.toString()
        if (TextUtils.isEmpty(desc)) {
            descriptionField.error = "Required."
            valid = false
        } else {
            descriptionField.error = null
        }

        return valid
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        val itematpos = parent?.getItemAtPosition(position)
        val sub = parent.toString().substringAfter("/").substringBefore("Dropdown")


        when (sub) {
            "type" -> {
                typedropdownval = itematpos.toString()
                Log.i(TAG, typedropdownval + " ffs")
            }
            "delivery" -> {
                deliverydropdownval = itematpos.toString()
                Log.i(TAG, deliverydropdownval)
            }
            "wear" -> {
                weardropdownval = itematpos.toString()
                Log.i(TAG, weardropdownval)
            }
            else -> {
                Log.i(TAG, "owo who dis spinner")
                Log.i(TAG, parent.toString())
            }
        }
//            Log.i(TAG, position.toString() +" "+ aa + " " + parent.toString())
//            Log.i(TAG, "sub is " + sub)


    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Log.i(TAG, "aaaaaaaaaaaaaaa")
    }


}