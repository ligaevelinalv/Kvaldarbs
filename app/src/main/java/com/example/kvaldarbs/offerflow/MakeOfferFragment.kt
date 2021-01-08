package com.example.kvaldarbs.offerflow

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kvaldarbs.R
import com.example.kvaldarbs.dialogs.PopUpDialog2Butt
import com.example.kvaldarbs.libs.utils.OfferViewModel
import com.example.kvaldarbs.models.Product
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import kotlinx.android.synthetic.main.fragment_makeoffer.*
import java.time.Year
import java.util.*
import kotlin.collections.HashMap

val TAG: String = "droidsays"
var id: Int = 0

var typedropdownval: String = ""
var deliverydropdownval: String = ""
var weardropdownval:String = ""
var amountnumber:Int = 1
var weightval: Int? = null
var heightval: Int? = null
var widthval: Int? = null
var lengthval: Int? = null
var materialval: String? = null
var colorval: String? = null
var authorval: String? = null
var yearval: Int? = null
var booktitleval: String? = null
var sizedropdownval: String? = null


var currentuserID:String = ""
var imageList = arrayListOf<Uri?>()
var productID: String? = ""

class MakeOfferFragment : Fragment(), AdapterView.OnItemSelectedListener  {
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var storage: FirebaseStorage
    lateinit var storageRef: StorageReference
    lateinit var currentuserID: String
    lateinit var firestore: FirebaseFirestore
    lateinit var keyref: DatabaseReference

    lateinit var adapter: ImageAdapter
    private val pickImage = 1
    private var image_uri: Uri? = null
    lateinit var role: String

    private val model: OfferViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Firebase.database.reference
        auth = Firebase.auth
        currentuserID = auth.currentUser?.uid.toString()
        storage = Firebase.storage
        storageRef = storage.reference
        firestore = Firebase.firestore
        keyref = database.child("users").child(com.example.kvaldarbs.offerflow.currentuserID).child("role")

        val roleQuery = keyref
        roleQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                role = dataSnapshot.value.toString()
//                if (role == "Administrator"){
//
//                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

                Log.i(com.example.kvaldarbs.mainpage.TAG, "role loading failed "+ databaseError.toException())
            }
        })

        val fddfs = Observer<List<Uri?>> {
            //Log.i(TAG, model.getList().toString())
            //attachedImage2.setImageURI(model.getLast())
            val imgAmount = model.getCount()
            val imgCount = imgAmount + "/10"
            imageAmountLabel.text = imgCount
            imageAmountLabel.visibility = View.VISIBLE

            if ((imgAmount.toInt() > 0) and (imgAmount.toInt() < 11)) {
                errorLabel.visibility = View.GONE
            }
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

        val sizedropdown: Spinner = rootview.findViewById(R.id.sizeDropdown)
        sizedropdown.onItemSelectedListener = this

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
                requireContext(),
                R.array.size_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            sizedropdown.adapter = adapter
        }

        val recyclerView: RecyclerView = rootview.findViewById(R.id.attachedImagesRV)


        recyclerView.layoutManager = LinearLayoutManager(this.requireContext(), LinearLayoutManager.HORIZONTAL, false)
        adapter = ImageAdapter(this.requireContext(), fetchList())
        recyclerView.adapter = adapter

        return rootview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideModuleFields()

        amountNumberText.text = amountnumber.toString()

        val ree = PopUpDialog2Butt()
        val bundle = Bundle()
        ree.aaa = {
            Log.i(TAG, "ree.aaa closure called")

                parseForm()

                newOffer(titleField.text.toString(),
                        typedropdownval,
                        manufacturerField.text.toString(),
                        deliverydropdownval,
                        weardropdownval,
                        amountnumber,
                        locationField.text.toString(),
                        descriptionField.text.toString(),

                        weightval,
                        heightval,
                        widthval,
                        lengthval,
                        materialval,
                        colorval,
                        authorval,
                        yearval,
                        booktitleval,
                        sizedropdownval
                )

                val temp = model.sharedImgUri.value
                uploadFileToStorage(temp as ArrayList<Uri?>)

                navigateToConfirm()
        }

        offerButton.setOnClickListener {
            //onAlertDialog(view)

            if (validateForm()) {
                bundle.putInt("dialogtype", 2)
                ree.arguments = bundle
                ree.show(parentFragmentManager, "")
            }
        }

        plusButton.setOnClickListener {
            if (amountnumber <= 10){
                amountnumber ++
                amountNumberText.text = amountnumber.toString()
            }
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
            //attachedImage.setImageURI(image_uri)
        }
    }

    fun navigateToConfirm(){
        Log.i(TAG, "navcontroller to confirmation called")
        findNavController().navigate(R.id.action_makeOfferFragment_to_offerConfirmationFragment)

    }

    fun newOffer(title: String, type: String, manufacturer: String,
                 delivery: String, wear: String, amount: Int, location: String,
                 description: String, weight: Int?, height: Int?, width: Int?, length: Int?,
                 material: String?, color: String?, author: String?, year: Int?,
                 book_title: String?, size: String?
    ) {

        val key = database.child("products").push().key
        productID = key
        if (key == null) {

            Log.i(TAG, "Couldn't get push key for posts")
            return
        }

        val product = Product(title, type, manufacturer, delivery,
                wear, amount, location, description,
                false, null,
                weight, height, width, length,
                material, color, author, year,
                book_title, size, true, ""
        )
        val productValues = product.toMap()

        val childUpdates = hashMapOf<String, Any>(
                "/products/$key" to productValues
        )

        database.updateChildren(childUpdates)

        val values: HashMap<String, Any> = HashMap()
        values["$key"] = true
        database.child("users").child(currentuserID)
                .child("offers").updateChildren(values)

        database.child("products").child(key).child("offerer").setValue(currentuserID)
    }

    private fun validateForm(): Boolean {

        var isValid: Boolean

        if (checkForEmpty(arrayListOf(titleField, manufacturerField, locationField, descriptionField))) {
            when (typedropdownval) {

                "Furniture" -> {
                    isValid = checkForEmpty(arrayListOf(weightField, heightField, widthField, lengthField, materialField, colorField))
                }

                "Book" -> {
                    val year = yearField.text.toString()

                    if(year == "") {
                        yearField.error = "Field cannot be empty."
                        isValid = false
                    }
                    else if ((year.toInt() > Year.now().value) or (year.toInt() <= 1700)) {
                        yearField.error = "Year has to be between 1700 and current year."
                        isValid = false
                    }
                    isValid = checkForEmpty(arrayListOf(authorField, yearField, bookTitleField))
                }

                "Decorations" -> {
                    isValid = checkForEmpty(arrayListOf(materialField, colorField))
                }
            }
            isValid = checkImageCount()

        } else {
            isValid = false
            when (typedropdownval) {
                "Furniture" -> {
                    checkForEmpty(arrayListOf(weightField, heightField, widthField, lengthField, materialField, colorField))
                }
                "Book" -> {
                    val year = yearField.text.toString()
                    if(year == "") {
                        yearField.error = "Field cannot be empty."
                    }
                    else if ((year.toInt() > Year.now().value) or (year.toInt() <= 1700)) {
                        yearField.error = "Year has to be between 1700 and current year."
                    }
                    checkForEmpty(arrayListOf(authorField, yearField, bookTitleField))
                }
                "Decorations" -> {
                    checkForEmpty(arrayListOf(materialField, colorField))
                }
            }
            checkImageCount()
        }

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

    fun checkImageCount(): Boolean{
        val count = model.getCount().toInt()
        if ((count > 10) or (count == 0)){
            errorLabel.text = getString(R.string.image_error)
            errorLabel.visibility = View.VISIBLE

            return false
        }
        else {
            errorLabel.visibility = View.GONE
            return true
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        val itematpos = parent?.getItemAtPosition(position)
        val sub = parent.toString().substringAfter("/").substringBefore("Dropdown")


        when (sub) {
            "type" -> {
                typedropdownval = itematpos.toString()
                Log.i(TAG, typedropdownval)
                if (typedropdownval == "Furniture") {
                    setVisibility(weightLabel, weightField)
                    setVisibility(heightLabel, heightField)
                    setVisibility(widthLabel, widthField)
                    setVisibility(lengthLabel, lengthField)
                    setVisibility(materialLabel, materialField)
                    setVisibility(colorLabel, colorField)

                    setInvisibility(authorLabel, authorField)
                    setInvisibility(yearLabel, yearField)
                    setInvisibility(bookTitleLabel, bookTitleField)
                    sizeLabel.visibility = View.GONE
                    sizeDropdown.visibility = View.GONE
                }

                if (typedropdownval == "Book") {
                    setVisibility(authorLabel, authorField)
                    setVisibility(yearLabel, yearField)
                    setVisibility(bookTitleLabel, bookTitleField)

                    setInvisibility(weightLabel, weightField)
                    setInvisibility(heightLabel, heightField)
                    setInvisibility(widthLabel, widthField)
                    setInvisibility(lengthLabel, lengthField)
                    setInvisibility(materialLabel, materialField)
                    setInvisibility(colorLabel, colorField)
                    sizeLabel.visibility = View.GONE
                    sizeDropdown.visibility = View.GONE
                }

                if (typedropdownval == "Decorations") {
                    setVisibility(materialLabel, materialField)
                    setVisibility(colorLabel, colorField)
                    sizeLabel.visibility = View.VISIBLE
                    sizeDropdown.visibility = View.VISIBLE

                    setInvisibility(weightLabel, weightField)
                    setInvisibility(heightLabel, heightField)
                    setInvisibility(widthLabel, widthField)
                    setInvisibility(lengthLabel, lengthField)
                    setInvisibility(authorLabel, authorField)
                    setInvisibility(yearLabel, yearField)
                    setInvisibility(bookTitleLabel, bookTitleField)
                }
            }
            "delivery" -> {
                deliverydropdownval = itematpos.toString()
                Log.i(TAG, deliverydropdownval)
            }
            "wear" -> {
                weardropdownval = itematpos.toString()
                Log.i(TAG, weardropdownval)
            }

            "size" -> {
                sizedropdownval = itematpos.toString()
                Log.i(TAG, sizedropdownval!!)
            }

            else -> {
                Log.i(TAG, "owo who dis spinner")
                Log.i(TAG, parent.toString())
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Log.i(TAG, "aaaaaaaaaaaaaaa")
    }

    fun uploadFileToStorage(uri: ArrayList<Uri?>){
        //val file = uri
        val metadata = storageMetadata {
            contentType = "image/jpg"
        }

        var count = 0

        for (file in uri) {
            val uploadRef = storageRef.child("userproductimages/$currentuserID/${file?.lastPathSegment}")
            val uploadTask = file?.let { uploadRef.putFile(it, metadata) }

            // Register observers to listen for when the download is done or if it fails
            uploadTask?.addOnFailureListener {
                Log.i(TAG, "File upload unsuccessful, $it")
            }?.addOnSuccessListener { taskSnapshot ->

                uploadRef.downloadUrl.addOnSuccessListener {
                    Log.i(TAG, "downloadurl: ${it} ")

                    val values: HashMap<String, Any> = HashMap()

                    values[count.toString()] = it.toString()

                    productID?.let { it1 ->
                        database.child("products").child(it1)
                                .child("images").updateChildren(values)
                        count++
                    }

                }.addOnFailureListener{
                    Log.i(TAG, "URL retrieval unsuccessful, $it")
                }

        }
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
    }

fun parseForm(){

    weightval = textToString(weightField)?.toInt()
    heightval = textToString(heightField)?.toInt()
    widthval = textToString(widthField)?.toInt()
    lengthval = textToString(lengthField)?.toInt()
    materialval = textToString(materialField)
    colorval = textToString(colorField)
    authorval = textToString(authorField)
    yearval = textToString(yearField)?.toInt()
    booktitleval = textToString(bookTitleField)

}

fun textToString(toparse: EditText): String? {
    if (!toparse.text.isNullOrBlank()){
        return toparse.text.toString()
    }
    else return null
}

fun hideModuleFields() {
    setInvisibility(weightLabel, weightField)
    setInvisibility(heightLabel, heightField)
    setInvisibility(widthLabel, widthField)
    setInvisibility(lengthLabel, lengthField)
    setInvisibility(materialLabel, materialField)
    setInvisibility(colorLabel, colorField)
    setInvisibility(authorLabel, authorField)
    setInvisibility(yearLabel, yearField)
    setInvisibility(bookTitleLabel, bookTitleField)
    sizeLabel.visibility = View.GONE
    sizeDropdown.visibility = View.GONE

    //not module field but requires the same functionality
    imageAmountLabel.visibility = View.GONE
    errorLabel.visibility = View.GONE
}

fun setInvisibility(label: TextView, text: EditText){
    label.visibility = View.GONE
    text.visibility = View.GONE
}

fun setVisibility(label: TextView, text: EditText){
    label.visibility = View.VISIBLE
    text.visibility = View.VISIBLE
}

}


