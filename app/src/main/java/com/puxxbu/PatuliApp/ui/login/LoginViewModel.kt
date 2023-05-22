package com.puxxbu.PatuliApp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puxxbu.PatuliApp.data.api.response.login.LoginResponse
import com.puxxbu.PatuliApp.data.model.UserDataModel
import com.puxxbu.PatuliApp.data.repository.DataRepository
import com.puxxbu.PatuliApp.utils.Event
import kotlinx.coroutines.launch

class LoginViewModel(private val dataRepository: DataRepository) : ViewModel() {
    val responseMessage: LiveData<Event<String>> = dataRepository.responseMessage
    val loginResponse: LiveData<LoginResponse> = dataRepository.loginResponse
    val isLoading: LiveData<Boolean> = dataRepository.isLoading

    fun postLogin(email: String, password: String) {
        viewModelScope.launch {
            dataRepository.postLogin(email, password)
        }
    }

    fun saveSession(session: UserDataModel) {
        viewModelScope.launch {
            dataRepository.setSessionData(session)
        }
    }

    fun login() {
        viewModelScope.launch {
            dataRepository.login( )
        }
    }
}