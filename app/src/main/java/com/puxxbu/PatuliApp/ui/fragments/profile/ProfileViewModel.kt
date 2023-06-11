package com.puxxbu.PatuliApp.ui.fragments.profile


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puxxbu.PatuliApp.data.api.response.profile.EditPasswordResponse
import com.puxxbu.PatuliApp.data.api.response.profile.EditProfilePicResponse
import com.puxxbu.PatuliApp.data.api.response.profile.ProfileResponse
import com.puxxbu.PatuliApp.data.model.UserDataModel
import com.puxxbu.PatuliApp.data.repository.DataRepository
import com.puxxbu.PatuliApp.utils.Event
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ProfileViewModel(private val dataRepository: DataRepository) : ViewModel() {
    val profileData: LiveData<ProfileResponse> = dataRepository.profileResponse
    val profileErrorResponse: LiveData<Event<String>> = dataRepository.profileErrorResponse
    val isLoading: LiveData<Boolean> = dataRepository.isLoading
    val editProfilePicResponse: LiveData<EditProfilePicResponse> =
        dataRepository.editProfilePicResponse
    val editPasswordResponse: LiveData<Event<EditPasswordResponse>> =
        dataRepository.editPasswordResponse
    val editPicResponse: LiveData<Event<String>> = dataRepository.profilePicErrorResponse

    fun getSessionData(): LiveData<UserDataModel> = dataRepository.getSessionData()

    fun changePassword(
        token: String,
        oldPassword: String,
        newPassword: String,
        newPasswordConfirmation: String
    ) {
        viewModelScope.launch {
            dataRepository.editPassword(token, oldPassword, newPassword, newPasswordConfirmation)
        }
    }

    fun editProfilePicture(token: String, image: MultipartBody.Part) {
        dataRepository.editProfilePicture(token, image)

    }


    fun logout() {
        viewModelScope.launch {
            dataRepository.logout()
        }
    }

    fun getProfile(token: String) = dataRepository.getProfile(token)
}
