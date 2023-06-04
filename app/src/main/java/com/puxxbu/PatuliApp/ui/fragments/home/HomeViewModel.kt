package com.puxxbu.PatuliApp.ui.fragments.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puxxbu.PatuliApp.data.api.response.profile.ProfileResponse
import com.puxxbu.PatuliApp.data.model.UserDataModel
import com.puxxbu.PatuliApp.data.repository.DataRepository
import com.puxxbu.PatuliApp.utils.Event
import kotlinx.coroutines.launch

class HomeViewModel(private val dataRepository: DataRepository) : ViewModel() {
    val profileData : LiveData<ProfileResponse> = dataRepository.profileResponse
    val profileErrorResponse: LiveData<Event<String>> = dataRepository.profileErrorResponse
    val isLoading: LiveData<Boolean> = dataRepository.isLoading

    fun getSessionData(): LiveData<UserDataModel> = dataRepository.getSessionData()

    fun logout(){
        viewModelScope.launch {
            dataRepository.logout()
        }
    }

    fun getProfile(token : String) {
        viewModelScope.launch {
            dataRepository.getProfile(token)
        }
    }
}