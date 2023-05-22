package com.puxxbu.PatuliApp.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puxxbu.PatuliApp.data.repository.DataRepository
import com.puxxbu.PatuliApp.utils.Event
import kotlinx.coroutines.launch

class RegisterViewModel (private val dataRepository: DataRepository) : ViewModel() {
    val responseMessage : LiveData<Event<String>> = dataRepository.responseMessage
    val isLoading : LiveData<Boolean> = dataRepository.isLoading

    fun register (name: String, email: String, password: String, passwordConfirmation: String) {
        viewModelScope.launch {
            dataRepository.postRegister(name, email, password, passwordConfirmation)
        }
    }
}