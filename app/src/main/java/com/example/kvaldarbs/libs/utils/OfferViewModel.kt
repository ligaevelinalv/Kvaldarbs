package com.example.kvaldarbs.libs.utils

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kvaldarbs.dialogs.TAG


class OfferViewModel : ViewModel() {

    val sharedImgUri: MutableLiveData<MutableList<Uri?>> by lazy {
        MutableLiveData<MutableList<Uri?>>()
    }


    fun getList(): MutableLiveData<MutableList<Uri?>> {
        return sharedImgUri
    }

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

    fun getLast(): Uri? {
        val temp = sharedImgUri.value

        if (temp != null) {
            return temp.get(temp.size -1)
        }
        else return null
    }

    fun getCount(): String {
        val temp = sharedImgUri.value

        if(temp != null) {
            return temp.count().toString()
        } else {
            return "0"
        }
    }



}