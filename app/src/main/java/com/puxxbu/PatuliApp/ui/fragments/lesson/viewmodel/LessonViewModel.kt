package com.puxxbu.PatuliApp.ui.fragments.lesson.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.puxxbu.PatuliApp.data.api.response.lesson.DetailLessonResponse
import com.puxxbu.PatuliApp.data.api.response.lesson.LessonDataResponse
import com.puxxbu.PatuliApp.data.model.UserDataModel
import com.puxxbu.PatuliApp.data.repository.DataRepository

class LessonViewModel(private val dataRepository: DataRepository) : ViewModel() {
    val isLoading : LiveData<Boolean> = dataRepository.isLoading

    private val _pageNumber = MutableLiveData<Int>()
    val pageNumber: MutableLiveData<Int> = _pageNumber
    val lessonData : LiveData<LessonDataResponse> = dataRepository.lessonListResponse
    val detailLessonData : LiveData<DetailLessonResponse> = dataRepository.lessonDetailResponse

    fun getSessionData(): LiveData<UserDataModel> = dataRepository.getSessionData()

    fun setNumber(number : Int){
        _pageNumber.value = number
    }


    fun getLessonListData(token : String, type :String){
        dataRepository.getLessonList(token, type)
    }

    fun getDetailLessonData(token: String, type: String, number: Int){
        dataRepository.getLessonbyNumber(token, type, number)
    }

}