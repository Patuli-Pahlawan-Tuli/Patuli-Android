package com.puxxbu.PatuliApp.ui.fragments.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.puxxbu.PatuliApp.data.api.response.profile.ProfileResponse
import com.puxxbu.PatuliApp.data.model.UserDataModel
import com.puxxbu.PatuliApp.data.repository.DataRepository

class ProfileViewModel(private val dataRepository: DataRepository) : ViewModel() {
    val profileData : LiveData<ProfileResponse> = dataRepository.profileResponse

    fun getSessionData(): LiveData<UserDataModel> = dataRepository.getSessionData()

    fun getProfile(token : String) = dataRepository.getProfile(token)
}
