package com.example.kvaldarbs.libs.utils

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.firebase.quickstart.database.kotlin.models.User


class OfferViewModel : ViewModel() {
    private var _sharedImgUrl = MutableLiveData<Uri?>()
    val sharedImgUrl : MutableLiveData<Uri?>
        get() = _sharedImgUrl




}