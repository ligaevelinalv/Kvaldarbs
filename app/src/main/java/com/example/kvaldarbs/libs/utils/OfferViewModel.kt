package com.example.kvaldarbs.libs.utils

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

//viewmodel that shares data between MakeOfferFragment, SelectImageDialog and OfferHostActivity
// to ensure the attachment and uploading of product images
class OfferViewModel : ViewModel() {
    //log tag definition
    var TAG: String = "droidsays"

    //list of mutable live data to refresh the attached images as they are created
    val sharedImgUri: MutableLiveData<MutableList<Uri?>> by lazy {
        MutableLiveData<MutableList<Uri?>>()
    }

    //method for attaching new images to list
    fun setList(list: MutableList<Uri?>) {

        if (sharedImgUri.value!= null) {
            val temp = sharedImgUri.value
            temp?.addAll(list)
            sharedImgUri.value = temp
        }
        else{
            sharedImgUri.value = list
        }
    }

    //returns how many images are attached
    fun getCount(): String {
        val temp = sharedImgUri.value

        if(temp != null) {
            return temp.count().toString()
        } else {
            return "0"
        }
    }
}