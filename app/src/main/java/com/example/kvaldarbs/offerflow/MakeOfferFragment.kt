package com.example.kvaldarbs.offerflow

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kvaldarbs.R
import com.example.kvaldarbs.dialogs.PopUpDialog2Butt
import com.example.kvaldarbs.libs.utils.OfferViewModel
import com.example.kvaldarbs.mainpage.Adapter
import com.example.kvaldarbs.models.Product
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import kotlinx.android.synthetic.main.fragment_detail.offerButton
import kotlinx.android.synthetic.main.fragment_makeoffer.*
import kotlin.collections.HashMap


val TAG: String = "droidsays"
var valid = true
var amountnumber:Int = 1
var id: Int = 0
var typedropdownval:String = ""
var deliverydropdownval:String = ""
var weardropdownval:String = ""
var currentuserID:String = ""
val REQUEST_IMAGE_CAPTURE = 1
var cameraImgUri: Uri? = null
var imageList = arrayListOf<Uri?>()




class MakeOfferFragment : Fragment(), AdapterView.OnItemSelectedListener  {
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var storage: FirebaseStorage
    lateinit var storageRef: StorageReference
    lateinit var currentuserID: String

    lateinit var adapter: ImageAdapter
    private val pickImage = 1
    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    var image_uri: Uri? = null
    val imgShow = SelectImageFragment()

    private val model: OfferViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Firebase.database.reference
        auth = Firebase.auth
        currentuserID = auth.currentUser?.uid.toString()
        storage = Firebase.storage
        storageRef = storage.reference
        currentuserID = auth.currentUser?.uid.toString()

        val fddfs = Observer<List<Uri?>> {
            Log.i(TAG, model.getList().toString())
            attachedImage2.setImageURI(model.getLast())
        }

        model.sharedImgUri.observe(this, fddfs)


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

        val recyclerView: RecyclerView = rootview.findViewById(R.id.attachedImagesRV)


        recyclerView.layoutManager = LinearLayoutManager(this.requireContext(), LinearLayoutManager.HORIZONTAL, false)
        adapter = ImageAdapter(this.requireContext(), fetchList())
        recyclerView.adapter = adapter

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
            uploadFileToStorage(model.sharedImgUri.value?.get(0))
        }

        minusButton.setOnClickListener {
            if (amountnumber!=1) {
                amountnumber--
                amountNumberText.text = amountnumber.toString()
            }
        }

        browseDeviceButton.setOnClickListener {
            askPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE){
                //all of your permissions have been accepted by the user
                val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(gallery, pickImage)
            }.onDeclined { e ->

                if (e.hasDenied()) {
                    Log.i(TAG, "Denied: ")
                    //the list of denied permissions
                    e.denied.forEach {
                        Log.i(TAG, e.toString())
                    }

                    AlertDialog.Builder(requireContext())
                        .setMessage("Please accept our permissions")
                        .setPositiveButton("yes") { dialog, which ->
                            e.askAgain()
                        } //ask again
                        .setNegativeButton("no") { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()
                }

                if(e.hasForeverDenied()) {
                    Log.i(TAG, "ForeverDenied : ")
                    //the list of forever denied permissions, user has check 'never ask again'
                    e.foreverDenied.forEach {
                        Log.i(TAG, e.toString())
                    }
                    // you need to open setting manually if you really need it
                    //e.goToSettings();
                }

            }
        }

        takePictureButton.setOnClickListener {
            askPermission(android.Manifest.permission.CAMERA){
                //all of your permissions have been accepted by the user

                Log.i(TAG, "navcontroller to camera called")
                findNavController().navigate(R.id.action_makeOfferFragment_to_cameraFragment)


            }.onDeclined { e ->

                if (e.hasDenied()) {
                    Log.i(TAG, "Denied: ")
                    //the list of denied permissions
                    e.denied.forEach {
                        Log.i(TAG, e.toString())
                    }

                    AlertDialog.Builder(requireContext())
                            .setMessage("Please accept our permissions")
                            .setPositiveButton("yes") { dialog, which ->
                                e.askAgain()
                            } //ask again
                            .setNegativeButton("no") { dialog, which ->
                                dialog.dismiss()
                            }
                            .show()
                }

                if(e.hasForeverDenied()) {
                    Log.i(TAG, "ForeverDenied : ")
                    //the list of forever denied permissions, user has check 'never ask again'
                    e.foreverDenied.forEach {
                        Log.i(TAG, e.toString())
                    }
                    // you need to open setting manually if you really need it
                    //e.goToSettings();
                }
            }
        }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //called when image was captured from camera intent
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            image_uri = data?.data
            attachedImage.setImageURI(image_uri)
        }
    }

    fun navigateToConfirm(){
        Log.i(TAG, "navcontroller to confirmation called")
        findNavController().navigate(R.id.action_makeOfferFragment_to_offerConfirmationFragment)

    }

    fun newOffer(title: String, type: String, manufacturer: String, weight: Int, delivery: String, wear: String, amount: Int, description: String) {
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

    fun uploadFileToStorage(uri: Uri?){
        val file = uri
        val metadata = storageMetadata {
            contentType = "image/jpg"
        }

        val riversRef = storageRef.child("userproductimages/$currentuserID/${file?.lastPathSegment}")
        val uploadTask = file?.let { riversRef.putFile(it, metadata) }

        // Register observers to listen for when the download is done or if it fails
        uploadTask?.addOnFailureListener {
            Log.i(TAG, "File upload unsuccessful")
        }?.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.

        }
    }

    fun fetchList(): ArrayList<Uri?> {

        val temp: MutableList<Uri?>? = model.sharedImgUri.value

        imageList.clear()

        if (temp != null) {
            for (item in temp) {
                imageList.add(item)
            }
        }


        Log.i(TAG, "fetchlist called")

        for (item in imageList) {
            Log.i(TAG, item.toString())
        }
        return imageList

//        val list = arrayListOf<RVData>()
//
//
//        for (i in 0..20) {
//            val model = RVData(R.drawable.furniturebackground, "Title : $i", "Subtitle : $i")
//            list.add(model)
//        }
//        return list
    }

}
























