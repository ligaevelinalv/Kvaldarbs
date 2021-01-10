package com.example.kvaldarbs.dialogs

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.kvaldarbs.R
import com.example.kvaldarbs.libs.utils.OfferViewModel
import kotlinx.android.synthetic.main.fragment_select_image_dialog.*


class SelectImageDialog : DialogFragment() {
    //log tag definition
    val TAG = "droidsays"

    //callback to do work in the class that the dialog was initialised in
    var imagecallback: () -> Unit = {}
    var setImageInMain: () -> Unit = {}

    var imageUri: Uri? = null
    lateinit var list: MutableList<Uri?>

    //image list handling viewmodel declaration
    private val model: OfferViewModel by activityViewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_image_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //casting values passed through a bundle in navigation
        imageUri = arguments?.getString("imageUri")?.toUri()

        newImage.setImageURI(imageUri)
        list = mutableListOf(imageUri)

        //button onclicklistener declaration
        selectImageButton.setOnClickListener {
            Log.i(TAG, "navcontroller to photo selection called")

            dismiss()
            imagecallback()
            setImageInMain()
            //new image is added to list after the user has selected it
            model.setList(list)
        }

    }





}