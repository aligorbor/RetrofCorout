package com.example.retrofcorout.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.retrofcorout.data.api.ApiHelper
import com.example.retrofcorout.data.repository.MainRepository
import com.example.retrofcorout.ui.viewmodel.MainViewModel

class ViewModelFactory(private val apiHelper: ApiHelper): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(MainRepository(apiHelper)) as T
        }
        throw java.lang.IllegalArgumentException("Unknown class name")
    }
}