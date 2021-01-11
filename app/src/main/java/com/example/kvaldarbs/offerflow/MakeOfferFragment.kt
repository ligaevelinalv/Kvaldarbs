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
import com.example.kvaldarbs.mainpage.MainScreen
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

var id: Int = 0
var currentuserID: String = ""

class MakeOfferFragment : Fragment(), AdapterView.OnItemSelectedListener  {
    //log tag definition
    val TAG: String = "droidsays"

    //variables for product input fields
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

    //list of Uris that is passed to the recyclerview adapter
    var imageList = arrayListOf<Uri?>()
    var productID: String? = ""

    //database variable declaration
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

    //offerview model declaration
    private val model: OfferViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //database variable initialising
        database = Firebase.database.reference
        auth = Firebase.auth
        currentuserID = auth.currentUser?.uid.toString()
        storage = Firebase.storage
        storageRef = storage.reference
        firestore = Firebase.firestore
        keyref = database.child("users").child(com.example.kvaldarbs.offerflow.currentuserID).child("role")

        //query to find what role the user has
        val roleQuery = keyref
        roleQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                role = dataSnapshot.value.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.i(TAG, "role loading failed "+ databaseError.toException())
            }
        })

        //uri list with oserver that monitors the image count
        val imageCountObserver = Observer<List<Uri?>> {
            val imgAmount = model.getCount()
            val imgCount = imgAmount + "/10"
            imageAmountLabel.text = imgCount
            imageAmountLabel.visibility = View.VISIBLE

            //error text shown if too few or too many images have been attached
            if ((imgAmount.toInt() > 0) and (imgAmount.toInt() < 11)) {
                errorLabel.visibility = View.GONE
            }

            if (imgAmount.toInt() > 9) {
                takePictureButton.visibility = View.GONE
            }
        }

        model.sharedImgUri.observe(this, imageCountObserver)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //inflate the layout for this fragment
        val rootview = inflater.inflate(R.layout.fragment_makeoffer, container, false)

        //dropdown field setup
        val typedropdown: Spinner = rootview.findViewById(R.id.typeDropdown)
        typedropdown.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
                requireContext(),
                R.array.product_type_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            typedropdown.adapter = adapter
        }

        val deliverydropdown: Spinner = rootview.findViewById(R.id.deliveryDropdown)
        deliverydropdown.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
                requireContext(),
                R.array.delivery_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            deliverydropdown.adapter = adapter
        }

        val weardropdown: Spinner = rootview.findViewById(R.id.wearDropdown)
        weardropdown.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
                requireContext(),
                R.array.wear_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            weardropdown.adapter = adapter
        }

        val sizedropdown: Spinner = rootview.findViewById(R.id.sizeDropdown)
        sizedropdown.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
                requireContext(),
                R.array.size_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sizedropdown.adapter = adapter
        }

        //recyclerview setup
        val recyclerView: RecyclerView = rootview.findViewById(R.id.attachedImagesRV)
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext(), LinearLayoutManager.HORIZONTAL, false)
        adapter = ImageAdapter(this.requireContext(), fetchList())
        recyclerView.adapter = adapter

        (activity as OfferFlowScreen).supportActionBar?.title = "Make offer"

        return rootview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideTypeFields()

        //initialising default product amount
        amountNumberText.text = amountnumber.toString()

        val confirmationdialog = PopUpDialog2Butt()
        val bundle = Bundle()
        confirmationdialog.callback2butt = {

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

                //upload final list of images to Firebase torage
                val finalList = model.sharedImgUri.value
                uploadFileToStorage(finalList as ArrayList<Uri?>)

                navigateToConfirm()
        }

        //button onclicklistener declaration
        offerButton.setOnClickListener {

            if (validateForm()) {
                bundle.putInt("dialogtype", 2)
                confirmationdialog.arguments = bundle
                confirmationdialog.show(parentFragmentManager, "")
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
            extStoragePerms()
        }

        takePictureButton.setOnClickListener {
            cameraPerms()
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as OfferFlowScreen).supportActionBar?.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //called when image was captured from camera intent
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            image_uri = data?.data
        }
    }

    fun navigateToConfirm(){
        findNavController().navigate(R.id.action_makeOfferFragment_to_offerConfirmationFragment)
    }

    //method for creating a new product object
    fun newOffer(title: String, type: String, manufacturer: String,
                 delivery: String, wear: String, amount: Int, location: String,
                 description: String, weight: Int?, height: Int?, width: Int?, length: Int?,
                 material: String?, color: String?, author: String?, year: Int?,
                 book_title: String?, size: String?
    ) {

        //new key value creation, product data will be pushed as the value to this key
        val key = database.child("products").push().key
        productID = key

        if (key == null) {
            return
        }

        val product = Product(title, type, manufacturer, delivery,
                wear, amount, location, description,
                false, null,
                weight, height, width, length,
                material, color, author, year,
                book_title, size, true, ""
        )

        //creating hash map with user input data
        val productValues = product.toMap()

        //defining a path in the nosql structure that the data will be stored in
        val childUpdates = hashMapOf<String, Any>(
                "/products/$key" to productValues
        )

        //pushing hasmap to the database
        database.updateChildren(childUpdates)

        //adding newly created product key value the user's list
        val values: HashMap<String, Any> = HashMap()
        values["$key"] = true
        database.child("users").child(currentuserID)
                .child("offers").updateChildren(values)

        database.child("products").child(key).child("offerer").setValue(currentuserID)
    }

    //form validation, checks required fields and shows error messages in case criteria is not met
    private fun validateForm(): Boolean {
        var isValid: Boolean

        //first checks if required fields for all types pass validation
        if (checkForEmpty(arrayListOf(titleField, manufacturerField, locationField, descriptionField))) {
            when (typedropdownval) {
                //checks type specific fields and shows error messages if any of them do not pass validation
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
                //only shows validation error messages
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

    //shows error message text if there are no images attached or too many images attached
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

    //method is called when the user selects a value in one of the dropdowns
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        //string operation to determine which dropdown was selected
        val itematpos = parent?.getItemAtPosition(position)
        val sub = parent.toString().substringAfter("/").substringBefore("Dropdown")


        when (sub) {

            "type" -> {
                //controlling visibility based on the value selected and setting the type value that will be
                //pushed to newOffer()
                typedropdownval = itematpos.toString()
                Log.i(TAG, typedropdownval)
                if (typedropdownval == "Furniture") {
                    setVisibilityLabel(arrayListOf(weightLabel, heightLabel, widthLabel, lengthLabel, materialLabel, colorLabel))
                    setVisibilityText(arrayListOf(weightField, heightField, widthField, lengthField, materialField, colorField))

                    setInvisibilityLabel(arrayListOf(authorLabel, yearLabel, bookTitleLabel))
                    setInvisibilityText(arrayListOf(authorField, yearField, bookTitleField))

                    sizeLabel.visibility = View.GONE
                    sizeDropdown.visibility = View.GONE
                }

                if (typedropdownval == "Book") {
                    setVisibilityLabel(arrayListOf(authorLabel, yearLabel, bookTitleLabel))
                    setVisibilityText(arrayListOf(authorField, yearField, bookTitleField))

                    setInvisibilityLabel(arrayListOf(weightLabel, heightLabel, widthLabel, lengthLabel, materialLabel, colorLabel))
                    setInvisibilityText(arrayListOf(weightField, heightField, widthField, lengthField, materialField, colorField))

                    sizeLabel.visibility = View.GONE
                    sizeDropdown.visibility = View.GONE
                }

                if (typedropdownval == "Decorations") {
                    setVisibilityLabel(arrayListOf(materialLabel, colorLabel))
                    setVisibilityText(arrayListOf(materialField, colorField))

                    setInvisibilityLabel(arrayListOf(weightLabel, heightLabel, widthLabel, lengthLabel, authorLabel, yearLabel, bookTitleLabel))
                    setInvisibilityText(arrayListOf(weightField, heightField, widthField, lengthField, authorField, yearField, bookTitleField))

                    sizeLabel.visibility = View.VISIBLE
                    sizeDropdown.visibility = View.VISIBLE
                }
            }
            "delivery" -> {
                //setting the delivery type value that will be pushed to newOffer()
                deliverydropdownval = itematpos.toString()
                Log.i(TAG, deliverydropdownval)
            }
            "wear" -> {
                //setting the wear type value that will be pushed to newOffer()
                weardropdownval = itematpos.toString()
                Log.i(TAG, weardropdownval)
            }

            "size" -> {
                //setting the size type value that will be pushed to newOffer()
                sizedropdownval = itematpos.toString()
                Log.i(TAG, sizedropdownval!!)
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Log.i(TAG, "nothing selected in spinner")
    }

    fun uploadFileToStorage(uri: ArrayList<Uri?>){
        val metadata = storageMetadata {
            contentType = "image/jpg"
        }

        var count = 0

        //uploading each attached image to Firebase Storage
        for (file in uri) {
            //setting the filepath and name of file
            val uploadRef = storageRef.child("userproductimages/$currentuserID/${file?.lastPathSegment}")
            val uploadTask = file?.let { uploadRef.putFile(it, metadata) }

            // register observers to listen for when the download is done or if it fails
            uploadTask?.addOnFailureListener {
                Log.i(TAG, "File upload unsuccessful, $it")
            }?.addOnSuccessListener { taskSnapshot ->

                //adding the uploaded image uris under the new product in the database
                uploadRef.downloadUrl.addOnSuccessListener {

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

    //method returns list to recyclerview when the value in the viewmodel has changed
    fun fetchList(): ArrayList<Uri?> {

        val temp: MutableList<Uri?>? = model.sharedImgUri.value
        //clearing old data
        imageList.clear()

        if (temp != null) {
            for (item in temp) {
                imageList.add(item)
            }
        }

        return imageList
    }

    //gets the text values from a field id there are any and populates the corresponding
    // variable that is pushed to newOffer()
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

    //if the textfield has a value, returns the value in string format
    fun textToString(toparse: EditText): String? {
        if (!toparse.text.isNullOrBlank()){
            return toparse.text.toString()
        }
        else return null
    }

    //hides type fields on activity startup
    fun hideTypeFields() {

        setInvisibilityLabel(arrayListOf(weightLabel, heightLabel, widthLabel, lengthLabel, materialLabel, colorLabel, authorLabel, yearLabel, bookTitleLabel))
        setInvisibilityText(arrayListOf(weightField, heightField, widthField, lengthField, materialField, colorField, authorField, yearField, bookTitleField))

        sizeLabel.visibility = View.GONE
        sizeDropdown.visibility = View.GONE

        imageAmountLabel.visibility = View.GONE
        errorLabel.visibility = View.GONE
    }

    //visibility changing methods
    fun setInvisibilityLabel(labels: ArrayList<TextView>){
        for (item in labels) {
            item.visibility = View.GONE
        }
    }

    fun setInvisibilityText(text: ArrayList<EditText>){
        for (item in text) {
            item.visibility = View.GONE
        }
    }

    fun setVisibilityLabel(labels: ArrayList<TextView>){
        for (item in labels) {
            item.visibility = View.VISIBLE
        }
    }

    fun setVisibilityText(text: ArrayList<EditText>){
        for (item in text) {
            item.visibility = View.VISIBLE
        }
    }

    //storage permission function, excecuted when user tries attaching an image from local storage
    fun extStoragePerms() {
        askPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE){
            //all permissions have been accepted by the user
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
                e.foreverDenied.forEach {
                    Log.i(TAG, e.toString())
                }
            }
        }
    }

    //camera permission function, excecuted when user tries attaching an image from local storage
    fun cameraPerms() {
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
                e.foreverDenied.forEach {
                    Log.i(TAG, e.toString())
                }
            }
        }
    }
}
