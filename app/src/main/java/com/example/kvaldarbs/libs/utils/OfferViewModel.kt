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

//            Log.i(TAG, "list length: " + sharedImgUri.value?.size.toString())
//            if (temp != null) {
//                for (item in temp) {
//                    Log.i(TAG, "temp item: " + item.toString())
//                }
//            }
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




}