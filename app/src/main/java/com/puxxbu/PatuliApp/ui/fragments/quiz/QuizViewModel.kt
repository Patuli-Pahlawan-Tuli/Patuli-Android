package com.puxxbu.PatuliApp.ui.fragments.quiz

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puxxbu.PatuliApp.data.api.response.profile.ProfileResponse
import com.puxxbu.PatuliApp.data.api.response.quiz.QuizProgressResponse
import com.puxxbu.PatuliApp.data.api.response.quiz.QuizResponse
import com.puxxbu.PatuliApp.data.model.UserDataModel
import com.puxxbu.PatuliApp.data.repository.DataRepository
import com.puxxbu.PatuliApp.utils.Event
import kotlinx.coroutines.launch

class QuizViewModel (private val dataRepository: DataRepository) : ViewModel() {
    val profileData : LiveData<ProfileResponse> = dataRepository.profileResponse
    val profileErrorResponse: LiveData<Event<String>> = dataRepository.profileErrorResponse
    val isLoading: LiveData<Boolean> = dataRepository.isLoading
    val quizProgressResponse: LiveData<QuizProgressResponse> = dataRepository.updateQuizProgressResponse
    private val _quizNumber = MutableLiveData<Int>()
    val quizNumber: MutableLiveData<Int> = _quizNumber
    val quizData : LiveData<QuizResponse> = dataRepository.quizResponse
    private val _quizAnswer = MutableLiveData<String>()
    val quizAnswer: MutableLiveData<String> = _quizAnswer

    fun updateQuizProgress(token: String, type: String){
        viewModelScope.launch {
            dataRepository.updateQuizProgress(token, type)
        }
    }

    fun updateSubQuizProgress(token: String,  subQuiz: Int){
        viewModelScope.launch {
            dataRepository.updateSubQuizProgress(token, subQuiz)
        }
    }

    fun getSessionData(): LiveData<UserDataModel> = dataRepository.getSessionData()

    fun getQuizData(token : String, type : String, subQuiz : Int, number : Int){
        viewModelScope.launch {
            dataRepository.getQuizbyNumber(token, type, subQuiz, number)
        }
    }



    fun setNumber(number : Int){
        _quizNumber.value = number
    }
    fun logout(){
        viewModelScope.launch {
            dataRepository.logout()
        }
    }

    fun getProfile(token : String) = dataRepository.getProfile(token)
}