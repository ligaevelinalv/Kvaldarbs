package com.example.kvaldarbs.offerflow

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


class SelectImageFragment : DialogFragment() {

    var callback2: () -> Unit = {}
    var setImageInMain: () -> Unit = {}
    var imageUri: Uri? = null

    private val model: OfferViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_image_dialog, container, false)
    }

    override fun onStart() {
        super.onStart()
         imageUri = arguments?.getString("imageUri")?.toUri()
        newImage.setImageURI(imageUri)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        selectImageButton.setOnClickListener {
            Log.i(TAG, "navcontroller to photo selection called")
            dismiss()
            callback2()
            setImageInMain()
            model.sharedImgUrl.value = imageUri
            Log.i(TAG, "callback called and finished")
        }

    }





}