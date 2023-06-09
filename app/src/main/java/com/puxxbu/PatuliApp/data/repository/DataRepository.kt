package com.puxxbu.PatuliApp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.google.gson.Gson
import com.puxxbu.PatuliApp.data.api.config.ApiService
import com.puxxbu.PatuliApp.data.api.response.file.FileHashResponse
import com.puxxbu.PatuliApp.data.api.response.lesson.DetailLessonResponse
import com.puxxbu.PatuliApp.data.api.response.lesson.LessonDataResponse
import com.puxxbu.PatuliApp.data.api.response.login.LoginResponse
import com.puxxbu.PatuliApp.data.api.response.profile.EditPasswordResponse
import com.puxxbu.PatuliApp.data.api.response.profile.EditProfilePicResponse
import com.puxxbu.PatuliApp.data.api.response.profile.ProfileResponse
import com.puxxbu.PatuliApp.data.api.response.quiz.QuizProgressResponse
import com.puxxbu.PatuliApp.data.api.response.quiz.QuizResponse
import com.puxxbu.PatuliApp.data.api.response.register.RegisterResponse
import com.puxxbu.PatuliApp.data.database.SessionDataPreferences
import com.puxxbu.PatuliApp.data.model.UserDataModel
import com.puxxbu.PatuliApp.utils.Event
import okhttp3.MultipartBody

class DataRepository constructor(
    private val pref: SessionDataPreferences,
    private val apiService: ApiService
) {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> = _loginResponse

    private val _responseMessage = MutableLiveData<Event<String>>()
    val responseMessage: LiveData<Event<String>> = _responseMessage

    private val _profileErrorResponse = MutableLiveData<Event<String>>()
    val profileErrorResponse: LiveData<Event<String>> = _profileErrorResponse

    private val _profilePicErrorResponse = MutableLiveData<Event<String>>()
    val profilePicErrorResponse: LiveData<Event<String>> = _profilePicErrorResponse

    private val _profileResponse = MutableLiveData<ProfileResponse>()
    val profileResponse: LiveData<ProfileResponse> = _profileResponse

    private val _editPasswordResponse = MutableLiveData<Event<EditPasswordResponse>>()
    val editPasswordResponse: LiveData<Event<EditPasswordResponse>> = _editPasswordResponse

    private val _editProfilePicResponse = MutableLiveData<EditProfilePicResponse>()
    val editProfilePicResponse: LiveData<EditProfilePicResponse> = _editProfilePicResponse

    private val _lessonListResponse = MutableLiveData<LessonDataResponse>()
    val lessonListResponse: LiveData<LessonDataResponse> = _lessonListResponse

    private val _lessonDetailResponse = MutableLiveData<DetailLessonResponse>()
    val lessonDetailResponse: LiveData<DetailLessonResponse> = _lessonDetailResponse

    private val _quizResponse = MutableLiveData<QuizResponse>()
    val quizResponse: LiveData<QuizResponse> = _quizResponse

    private val _fileHashResponse = MutableLiveData<FileHashResponse>()
    val fileHashResponse: LiveData<FileHashResponse> = _fileHashResponse

    private val _updateQuizProgressResponse = MutableLiveData<QuizProgressResponse>()
    val updateQuizProgressResponse: LiveData<QuizProgressResponse> = _updateQuizProgressResponse

    private val _updateExpResponse = MutableLiveData<QuizProgressResponse>()
    val updateExpResponse: LiveData<QuizProgressResponse> = _updateExpResponse


    fun postRegister(name: String, email: String, password: String, passwordConfirmation: String) {
        _isLoading.value = true
        val client = apiService.postRegister(name, email, password, passwordConfirmation)
        client.enqueue(object : retrofit2.Callback<RegisterResponse> {
            override fun onResponse(
                call: retrofit2.Call<RegisterResponse>,
                response: retrofit2.Response<RegisterResponse>
            ) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    _responseMessage.value = Event(response.body()?.message.toString())
                    Log.d(
                        "TAG",
                        "Success: ${response.body()} ${response.body()?.message.toString()}"
                    )
                } else {

                    val errorResponse = Gson().fromJson(
                        response.errorBody()?.string(),
                        RegisterResponse::class.java
                    )
                    _responseMessage.value = Event(errorResponse.message)
                    Log.d(
                        "TAG",
                        "Failed: ${errorResponse.message}"
                    )
                }
            }

            override fun onFailure(call: retrofit2.Call<RegisterResponse>, t: Throwable) {
                Log.d("TAG", "Failed: ${t.message}")
            }

        })

    }

    fun editPassword(token: String, oldPassword: String, newPassword: String, newPasswordConfirmation: String){
        _isLoading.value = true
        val client = apiService.editPassword(token , oldPassword , newPassword , newPasswordConfirmation )
        client.enqueue(object : retrofit2.Callback<EditPasswordResponse> {
            override fun onResponse(
                call: retrofit2.Call<EditPasswordResponse>,
                response: retrofit2.Response<EditPasswordResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _profileErrorResponse.value = Event(response.body()?.message.toString())
                    _editPasswordResponse.value = Event(response.body()!!)
                } else {
                    val errorResponse =
                        Gson().fromJson(response.errorBody()?.string(), EditPasswordResponse::class.java)
                    _profileErrorResponse.value = Event(errorResponse.message)
                    _editPasswordResponse.value = Event(errorResponse)
                    Log.d("TAG", "onResponse: ${errorResponse.message}")
                }
            }

            override fun onFailure(call: retrofit2.Call<EditPasswordResponse>, t: Throwable) {
                Log.d("TAG", "Failed: ${t.message}")
            }
        })
    }

    fun editProfilePicture(token : String, image : MultipartBody.Part){
        _isLoading.value = true
        Log.d("TAG", "onResponse: CALL editpic")
        val client = apiService.editProfilePicture(token , image)
        client.enqueue(object : retrofit2.Callback<EditProfilePicResponse> {
            override fun onResponse(
                call: retrofit2.Call<EditProfilePicResponse>,
                response: retrofit2.Response<EditProfilePicResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    Log.d("TAG", "onResponse: response editpic")
                    _profilePicErrorResponse.value = Event(response.body()?.message.toString())
                    _editProfilePicResponse.value = response.body()

                } else {
                    val errorResponse =
                        Gson().fromJson(response.errorBody()?.string(), EditProfilePicResponse::class.java)
                    _profilePicErrorResponse.value = Event(errorResponse.message)
                    Log.d("TAG", "onResponse: ${errorResponse.message}")
                }
            }

            override fun onFailure(call: retrofit2.Call<EditProfilePicResponse>, t: Throwable) {
                _isLoading.value = false
                Log.d("TAG", "Failed: ${t.message}")
            }
        })
    }


    fun postLogin(email: String, password: String) {
        _isLoading.value = true
        val client = apiService.postLogin(email, password)
        client.enqueue(object : retrofit2.Callback<LoginResponse> {
            override fun onResponse(
                call: retrofit2.Call<LoginResponse>,
                response: retrofit2.Response<LoginResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _loginResponse.value = response.body()
                    _responseMessage.value = Event(response.body()?.message.toString())
                } else {
                    val errorResponse =
                        Gson().fromJson(response.errorBody()?.string(), LoginResponse::class.java)
                    _responseMessage.value = Event(errorResponse.message)
                    Log.d("TAG", "onResponse: ${errorResponse.message}")
                }
            }

            override fun onFailure(call: retrofit2.Call<LoginResponse>, t: Throwable) {
                Log.d("TAG", "Failed: ${t.message}")
            }
        })
    }

    fun getProfile(token: String) {
        _isLoading.value = true
        val client = apiService.getProfile(token)
        client.enqueue(object : retrofit2.Callback<ProfileResponse> {
            override fun onResponse(
                call: retrofit2.Call<ProfileResponse>,
                response: retrofit2.Response<ProfileResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _profileResponse.value = response.body()
                    _profileErrorResponse.value = Event(response.body()?.message.toString())
                } else {
                    val errorResponse =
                        Gson().fromJson(response.errorBody()?.string(), ProfileResponse::class.java)
                    _profileErrorResponse.value = Event(errorResponse.message)
                    Log.d("TAG", "onResponse: ${errorResponse.message}")
                }
            }

            override fun onFailure(call: retrofit2.Call<ProfileResponse>, t: Throwable) {
                Log.d("TAG", "Failed: ${t.message}")
            }
        })
    }

    fun getLessonList(token:String, type: String){
        _isLoading.value = true
        val client = apiService.getLessonData(token , type)
        client.enqueue(object : retrofit2.Callback<LessonDataResponse> {
            override fun onResponse(
                call: retrofit2.Call<LessonDataResponse>,
                response: retrofit2.Response<LessonDataResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _lessonListResponse.value = response.body()
                    _responseMessage.value = Event(response.body()?.message.toString())
                } else {
                    val errorResponse =
                        Gson().fromJson(response.errorBody()?.string(), LessonDataResponse::class.java)
                    _responseMessage.value = Event(errorResponse.message)
                    Log.d("TAG", "onResponse: ${errorResponse.message}")
                }
            }

            override fun onFailure(call: retrofit2.Call<LessonDataResponse>, t: Throwable) {
                Log.d("TAG", "Failed: ${t.message}")
            }
        })
    }

    fun getLessonbyNumber(token:String, type: String, number : Int){
        Log.d("TAGlesson", "onResponse: getLessonbyNumber")
        _isLoading.value = true
        val client = apiService.getDetailLesson(token , type, number)
        client.enqueue(object : retrofit2.Callback<DetailLessonResponse> {
            override fun onResponse(
                call: retrofit2.Call<DetailLessonResponse>,
                response: retrofit2.Response<DetailLessonResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _lessonDetailResponse.value = response.body()
                    _responseMessage.value = Event(response.body()?.message.toString())
                } else {
                    Log.d("TAGlesson", "onResponse: GAGAL")
                    val errorResponse =
                        Gson().fromJson(response.errorBody()?.string(), DetailLessonResponse::class.java)
                    _responseMessage.value = Event(errorResponse.message)
                    Log.d("TAG", "onResponse: ${errorResponse.message}")
                }
            }

            override fun onFailure(call: retrofit2.Call<DetailLessonResponse>, t: Throwable) {
                Log.d("TAG", "Failed: ${t.message}")
            }
        })
    }

    fun getQuizbyNumber(token : String, type : String,subQuiz : Int, number : Int){
        _isLoading.value = true
        val client = apiService.getQuizbyNumber(token , type, subQuiz, number)
        client.enqueue(object : retrofit2.Callback<QuizResponse> {
            override fun onResponse(
                call: retrofit2.Call<QuizResponse>,
                response: retrofit2.Response<QuizResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _quizResponse.value = response.body()
                    _responseMessage.value = Event(response.body()?.message.toString())
                } else {
                    val errorResponse =
                        Gson().fromJson(response.errorBody()?.string(), QuizResponse::class.java)
                    _responseMessage.value = Event(errorResponse.message)
                    Log.d("TAG", "onResponse: ${errorResponse.message}")
                }
            }

            override fun onFailure(call: retrofit2.Call<QuizResponse>, t: Throwable) {
                Log.d("TAG", "Failed: ${t.message}")
            }
        })

    }


    fun getFileHashData(token :String){
        _isLoading.value = true
        val client = apiService.getFileHash(token)
        client.enqueue(object : retrofit2.Callback<FileHashResponse> {
            override fun onResponse(
                call: retrofit2.Call<FileHashResponse>,
                response: retrofit2.Response<FileHashResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _fileHashResponse.value = response.body()
                } else {
                    val errorResponse =
                        Gson().fromJson(response.errorBody()?.string(), FileHashResponse::class.java)

                }
            }

            override fun onFailure(call: retrofit2.Call<FileHashResponse>, t: Throwable) {
                Log.d("TAG", "Failed: ${t.message}")
            }
        })
    }

    fun updateQuizProgress(token: String, type: String){
        _isLoading.value = true
        val client = apiService.updateQuizProgress(token, type,-1)
        client.enqueue(object : retrofit2.Callback<QuizProgressResponse> {
            override fun onResponse(
                call: retrofit2.Call<QuizProgressResponse>,
                response: retrofit2.Response<QuizProgressResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _updateQuizProgressResponse.value = response.body()
                    _responseMessage.value = Event(response.body()?.message.toString())
                } else {
                    val errorResponse =
                        Gson().fromJson(response.errorBody()?.string(), QuizProgressResponse::class.java)
                    _responseMessage.value = Event(errorResponse.message)
                    Log.d("TAG", "onResponse: ${errorResponse.message}")
                }
            }

            override fun onFailure(call: retrofit2.Call<QuizProgressResponse>, t: Throwable) {
                Log.d("TAG", "Failed: ${t.message}")
            }
        })
    }

    fun updateSubQuizProgress(token: String, subQuiz: Int){
        _isLoading.value = true
        val client = apiService.updateSubQuizProgress(token,1,subQuiz)
        client.enqueue(object : retrofit2.Callback<QuizProgressResponse> {
            override fun onResponse(
                call: retrofit2.Call<QuizProgressResponse>,
                response: retrofit2.Response<QuizProgressResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _updateQuizProgressResponse.value = response.body()
                    _responseMessage.value = Event(response.body()?.message.toString())
                } else {
                    val errorResponse =
                        Gson().fromJson(response.errorBody()?.string(), QuizProgressResponse::class.java)
                    _responseMessage.value = Event(errorResponse.message)
                    Log.d("TAG", "onResponse: ${errorResponse.message}")
                }
            }

            override fun onFailure(call: retrofit2.Call<QuizProgressResponse>, t: Throwable) {
                Log.d("TAG", "Failed: ${t.message}")
            }
        })
    }

    fun updateUserExperience(token: String, newExp : Int){
        _isLoading.value = true
        val client = apiService.updateUserExperience(token, newExp)
        client.enqueue(object : retrofit2.Callback<QuizProgressResponse> {
            override fun onResponse(
                call: retrofit2.Call<QuizProgressResponse>,
                response: retrofit2.Response<QuizProgressResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _updateExpResponse.value = response.body()
                    _responseMessage.value = Event(response.body()?.message.toString())
                } else {
                    val errorResponse =
                        Gson().fromJson(response.errorBody()?.string(), QuizProgressResponse::class.java)
                    _responseMessage.value = Event(errorResponse.message)
                    Log.d("TAG", "onResponse: ${errorResponse.message}")
                }
            }

            override fun onFailure(call: retrofit2.Call<QuizProgressResponse>, t: Throwable) {
                Log.d("TAG", "Failed: ${t.message}")
            }
        })
    }


    fun getSessionData(): LiveData<UserDataModel> {
        return pref.getSession().asLiveData()
    }

    suspend fun setSessionData(userDataModel: UserDataModel) {
        pref.saveSession(userDataModel)
    }


    suspend fun login() {
        pref.login()
    }

    suspend fun logout() {
        pref.logout()
    }


}