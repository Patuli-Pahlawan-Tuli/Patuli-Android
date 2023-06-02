package com.puxxbu.PatuliApp.ui.fragments.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.puxxbu.PatuliApp.data.api.response.profile.ProfileResponse
import com.puxxbu.PatuliApp.data.model.UserDataModel
import com.puxxbu.PatuliApp.data.repository.DataRepository
import com.puxxbu.PatuliApp.utils.Event

class HomeViewModel(private val dataRepository: DataRepository) : ViewModel() {
    val profileData : LiveData<ProfileResponse> = dataRepository.profileResponse
    val profileErrorResponse: LiveData<Event<String>> = dataRepository.profileErrorResponse
    val isLoading: LiveData<Boolean> = dataRepository.isLoading

    fun getSessionData(): LiveData<UserDataModel> = dataRepository.getSessionData()

    fun getProfile(token : String) = dataRepository.getProfile(token)
}