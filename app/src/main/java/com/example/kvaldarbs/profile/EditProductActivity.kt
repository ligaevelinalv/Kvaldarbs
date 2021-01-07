package com.example.kvaldarbs.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.*
import com.example.kvaldarbs.R
import com.example.kvaldarbs.mainpage.MainScreen
import com.example.kvaldarbs.models.EditProduct
import com.example.kvaldarbs.models.Product
import com.example.kvaldarbs.offerflow.amountnumber
import com.example.kvaldarbs.offerflow.productID
import com.example.kvaldarbs.offerflow.typedropdownval
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_edit_product.*
import kotlinx.android.synthetic.main.activity_edit_product.amountNumberText
import kotlinx.android.synthetic.main.activity_edit_product.authorField
import kotlinx.android.synthetic.main.activity_edit_product.authorLabel
import kotlinx.android.synthetic.main.activity_edit_product.bookTitleField
import kotlinx.android.synthetic.main.activity_edit_product.bookTitleLabel
import kotlinx.android.synthetic.main.activity_edit_product.colorField
import kotlinx.android.synthetic.main.activity_edit_product.colorLabel
import kotlinx.android.synthetic.main.activity_edit_product.descriptionField
import kotlinx.android.synthetic.main.activity_edit_product.heightField
import kotlinx.android.synthetic.main.activity_edit_product.heightLabel
import kotlinx.android.synthetic.main.activity_edit_product.lengthField
import kotlinx.android.synthetic.main.activity_edit_product.lengthLabel
import kotlinx.android.synthetic.main.activity_edit_product.locationField
import kotlinx.android.synthetic.main.activity_edit_product.manufacturerField
import kotlinx.android.synthetic.main.activity_edit_product.materialField
import kotlinx.android.synthetic.main.activity_edit_product.materialLabel
import kotlinx.android.synthetic.main.activity_edit_product.sizeDropdown
import kotlinx.android.synthetic.main.activity_edit_product.sizeLabel
import kotlinx.android.synthetic.main.activity_edit_product.titleField
import kotlinx.android.synthetic.main.activity_edit_product.weightField
import kotlinx.android.synthetic.main.activity_edit_product.weightLabel
import kotlinx.android.synthetic.main.activity_edit_product.widthField
import kotlinx.android.synthetic.main.activity_edit_product.widthLabel
import kotlinx.android.synthetic.main.activity_edit_product.yearField
import kotlinx.android.synthetic.main.activity_edit_product.yearLabel
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_makeoffer.*
import java.time.Year

class EditProductActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener  {
    val TAG: String = "droidsays"
    lateinit var productKey: String
    lateinit var type: String
    lateinit var database: DatabaseReference
    lateinit var keyref: DatabaseReference
    lateinit var editQuery: DatabaseReference

    var title: String = ""
    var prodType: String = ""
    var manufacturer: String = ""
    var amount: Int = 0
    var location: String = ""
    var description: String = ""

    var weight: Int? = null
    var height: Int? = null
    var width: Int? = null
    var length: Int? = null
    var material: String? = null
    var color: String? = null
    var author: String? = null
    var year: Int? = null
    var booktitle: String? = null
    var size: String? = null

    var deliverydropdownval: String = ""
    var sizedropdownval: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)

        productKey = intent.getStringExtra("key").toString()
        type = intent.getStringExtra("type").toString()

        database = Firebase.database.reference
        keyref = database.child("products").child(productKey)

        setSupportActionBar(findViewById(R.id.profile_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val deliverydropdown: Spinner = findViewById(R.id.deliveryDropdown)
        deliverydropdown.onItemSelectedListener = this

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
                this,
                R.array.delivery_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            deliverydropdown.adapter = adapter
        }


        val sizedropdown: Spinner = findViewById(R.id.sizeDropdown)
        sizedropdown.onItemSelectedListener = this

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
                this,
                R.array.size_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            sizedropdown.adapter = adapter
        }

        editQuery = keyref

        editQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val product = dataSnapshot.getValue<Product>()

                product?.let {
                    hideFields(it.type)
                    prodType = it.type

                    titleField.setText(it.title)
                    title = it.title

                    manufacturerField.setText(it.manufacturer)
                    manufacturer = it.manufacturer

                    if (it.delivery == "Pickup From Location") {
                        deliverydropdown.setSelection(0)
                    }
                    if (it.delivery == "Delivery To Orderer") {
                        deliverydropdown.setSelection(1)
                    }

                    amountNumberText.setText(it.amount.toString())
                    amount = it.amount

                    locationField.setText(it.location)
                    location = it.location

                    descriptionField.setText(it.description)
                    description = it.description

                    if (prodType == "Furniture") {
                        weightField.setText(it.weight.toString())
                        weight = it.weight

                        heightField.setText( it.height.toString())
                        height = it.height

                        widthField.setText(it.width.toString())
                        width = it.width

                        lengthField.setText(it.length.toString())
                        length = it.length

                        materialField.setText(it.material)
                        material = it.material

                        colorField.setText(it.color)
                        color = it.color
                    }

                    if (prodType == "Decorations") {
                        materialField.setText(it.material)
                        material = it.material

                        colorField.setText(it.color)
                        color = it.color

                        if (it.size == "Small") {
                            sizedropdown.setSelection(0)
                        }
                        if (it.size == "Medium") {
                            sizedropdown.setSelection(1)
                        }
                        if (it.size == "Large") {
                            sizedropdown.setSelection(2)
                        }
                        size = it.size
                    }
                    if (prodType == "Book") {
                        authorField.setText(it.author)
                        author = it.author

                        yearField.setText(it.year.toString())
                        year = it.year

                        bookTitleField.setText(it.book_title)
                        booktitle = it.book_title
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(com.example.kvaldarbs.dialogs.TAG, "query fetching error: " + error.toException().toString())
            }
        })

        finishEditingButton.setOnClickListener {
            if (validateForm()) {
                Log.i(TAG, "Validation passed")
                updateProduct()
                val intent = Intent(this, OffersActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

        editPlusButton.setOnClickListener {
            if (amount < 11){
                amount ++
                amountNumberText.text = amount.toString()
            }
        }

        editMinusButton.setOnClickListener {
            if (amount !=1) {
                amount--
                amountNumberText.text = amount.toString()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        startActivity(Intent(this, OffersActivity::class.java))
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.profile_toolbar, menu)
        return true
    }

    fun hideFields(type: String?){
        when(type) {

            "Furniture" -> {
                hideLabelBasedOnType(
                    arrayListOf(
                        authorLabel, yearLabel, bookTitleLabel, sizeLabel
                    )
                )

                hideEditTextBasedOnType(
                    arrayListOf(
                        authorField, yearField, bookTitleField
                    )
                )

                sizeDropdown.visibility = View.GONE
            }

            "Book" -> {
                hideLabelBasedOnType(
                    arrayListOf(
                        weightLabel, heightLabel, widthLabel, lengthLabel,
                        materialLabel, colorLabel, sizeLabel
                    )
                )

                hideEditTextBasedOnType(
                    arrayListOf(
                        weightField, heightField, widthField, lengthField,
                        materialField, colorField
                    )
                )

                sizeDropdown.visibility = View.GONE
            }

            "Decorations" -> {
                hideLabelBasedOnType(
                    arrayListOf(
                        weightLabel, heightLabel, widthLabel, lengthLabel,
                        authorLabel, yearLabel, bookTitleLabel, sizeLabel
                    )
                )

                hideEditTextBasedOnType(
                    arrayListOf(
                        weightField, heightField, widthField, lengthField,
                        authorField, yearField, bookTitleField
                    )
                )
            }
        }
    }

    fun hideLabelBasedOnType(type: ArrayList<TextView>) {
        for (item in type) {
            item.visibility = View.GONE
        }
    }

    fun hideEditTextBasedOnType(type: ArrayList<EditText>) {
        for (item in type) {
            item.visibility = View.GONE
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        val itematpos = parent?.getItemAtPosition(position)
        val sub = parent.toString().substringAfter("/").substringBefore("Dropdown")


        when (sub) {
            "delivery" -> {
                deliverydropdownval = itematpos.toString()
                Log.i(com.example.kvaldarbs.offerflow.TAG, deliverydropdownval)
            }
            "size" -> {
                sizedropdownval = itematpos.toString()
                Log.i(com.example.kvaldarbs.offerflow.TAG, sizedropdownval!!)
            }

            else -> {
                Log.i(com.example.kvaldarbs.offerflow.TAG, "owo who dis spinner")
                Log.i(com.example.kvaldarbs.offerflow.TAG, parent.toString())
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Log.i(TAG, "Nothing selected in dropdown $parent")
    }

    private fun validateForm(): Boolean {

        var isValid = true

        //checks if required values are correct, if not, calls the check methods without changing the main function return value while still showing the error label
        if (checkForEmpty(arrayListOf(titleField, manufacturerField, locationField, descriptionField))) {
            when (prodType) {
                "Furniture" -> {
                    isValid = checkForEmpty(arrayListOf(weightField, heightField, widthField, lengthField, materialField, colorField))
                }
                "Book" -> {
                    isValid = checkForEmpty(arrayListOf(authorField, yearField, bookTitleField))
                    val year = yearField.text.toString()
                    if(year == "") {
                        yearField.error = "Required."
                        isValid = false
                    }
                    else if ((year.toInt() > Year.now().value) or (year.toInt() <= 1700)) {
                        yearField.error = "Year has to be between 1700 and current year."
                        isValid = false
                    }
                }
                "Decorations" -> {
                    isValid = checkForEmpty(arrayListOf(materialField, colorField))
                }
            }
        } else {
            isValid = false
            when (prodType) {
                "Furniture" -> {
                    checkForEmpty(arrayListOf(weightField, heightField, widthField, lengthField, materialField, colorField))
                }
                "Book" -> {
                    checkForEmpty(arrayListOf(authorField, yearField, bookTitleField))
                    val year = yearField.text.toString()
                    if(year == "") {
                        yearField.error = "Required."
                    }
                    else if ((year.toInt() > Year.now().value) or (year.toInt() <= 1700)) {
                        yearField.error = "Year has to be between 1700 and current year."
                    }
                }
                "Decorations" -> {
                    checkForEmpty(arrayListOf(materialField, colorField))
                }
            }
        }

        return isValid
    }

    fun checkForEmpty(fields: java.util.ArrayList<EditText>): Boolean{
        var isValid = true

        for (item in fields) {
            if (item.text.toString() == "") {
                item.error = "Required."
                isValid = false
            }
        }

        return isValid
    }

    fun updateProduct(){
        keyref.child("title").setValue(titleField.text.toString())
        keyref.child("manufacturer").setValue(manufacturerField.text.toString())
        keyref.child("delivery").setValue(deliverydropdownval)
        keyref.child("amount").setValue(amountNumberText.text.toString().toInt())
        keyref.child("location").setValue(locationField.text.toString())
        keyref.child("description").setValue(descriptionField.text.toString())
        keyref.child("admincritic").setValue("")
        keyref.child("visible").setValue(true)

        if (prodType == "Furniture") {
            keyref.child("weight").setValue(weightField.text.toString().toInt())
            keyref.child("height").setValue(heightField.text.toString().toInt())
            keyref.child("width").setValue(widthField.text.toString().toInt())
            keyref.child("length").setValue(lengthField.text.toString().toInt())
            keyref.child("material").setValue(materialField.text.toString())
            keyref.child("color").setValue(colorField.text.toString())
        }
        if (prodType == "Decorations") {
            keyref.child("material").setValue(materialField.text.toString())
            keyref.child("color").setValue(colorField.text.toString())
            keyref.child("size").setValue(sizedropdownval)
        }
        if (prodType == "Book") {

            keyref.child("author").setValue(authorField.text.toString())
            keyref.child("year").setValue(yearField.text.toString().toInt())
            keyref.child("book_title").setValue(bookTitleField.text.toString())
        }
    }
}


























