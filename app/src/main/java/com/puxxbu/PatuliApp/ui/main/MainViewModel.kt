package com.puxxbu.PatuliApp.ui.main


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puxxbu.PatuliApp.data.api.response.profile.ProfileResponse
import com.puxxbu.PatuliApp.data.model.UserDataModel
import com.puxxbu.PatuliApp.data.repository.DataRepository
import com.puxxbu.PatuliApp.utils.Event
import kotlinx.coroutines.launch

class MainViewModel(private val dataRepository: DataRepository) : ViewModel() {
    val profileData : LiveData<ProfileResponse> = dataRepository.profileResponse
    val profileErrorResponse: LiveData<Event<String>> = dataRepository.profileErrorResponse
    private val _isloading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isloading

    private val _permissionResponse = MutableLiveData<Boolean>()
    val permissionResponse: MutableLiveData<Boolean> = _permissionResponse

    private val _isProceed = MutableLiveData<Event<Boolean>>()
    val isProceed: MutableLiveData<Event<Boolean>> = _isProceed

    private val _isDownloaded = MutableLiveData<Event<Boolean>>()
    val isDownloaded: MutableLiveData<Event<Boolean>> = _isDownloaded

    private val _downloadCount = MutableLiveData<Int>()
    val downloadCount: LiveData<Int> = _downloadCount

    fun getSessionData(): LiveData<UserDataModel> = dataRepository.getSessionData()

    fun logout() {
        viewModelScope.launch {
            dataRepository.logout()
        }
    }

    fun setDownloadCount(count: Int) {
        _downloadCount.value = count
    }

    fun saveSession(session: UserDataModel) {
        viewModelScope.launch {
            dataRepository.setSessionData(session)
        }
    }

    fun incrementDownloadCount() {
        _downloadCount.value = _downloadCount.value?.plus(1)
    }

    fun setPermissionResponse(response: Boolean) {
        _permissionResponse.value = response
    }

    fun setProceed(response: Event<Boolean>) {
        _isProceed.value = response
    }

    fun getProfile(token : String) = dataRepository.getProfile(token)


}