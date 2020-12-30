package com.example.kvaldarbs.libs.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OrderViewModel : ViewModel() {
    private val onclick: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun getValue(): String{
        return onclick.value.toString()
    }

    fun setValue(key: String) {
        onclick.value = key
    }

}