package com.puxxbu.PatuliApp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puxxbu.PatuliApp.data.model.UserDataModel
import com.puxxbu.PatuliApp.data.repository.DataRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val dataRepository: DataRepository) : ViewModel() {

    val isLoading: LiveData<Boolean> = dataRepository.isLoading

    fun getSessionData(): LiveData<UserDataModel> = dataRepository.getSessionData()

    fun logout() {
        viewModelScope.launch {
            dataRepository.logout()
        }
    }


}