package com.example.kvaldarbs.libs.utils

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

//viewmodel that shares data between the DetailFragment and OrderHostActivity to pass the selected
//product key in OrderHostActivity's recyclerview to DetailFragment where the selected product is displayed in detail
class OrderViewModel : ViewModel() {
    //key value of the product that the user has clicked on
    private val onclick: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    //returns the set value
    fun getValue(): String{
        return onclick.value.toString()
    }

    fun setValue(key: String) {
        onclick.value = key
    }

}