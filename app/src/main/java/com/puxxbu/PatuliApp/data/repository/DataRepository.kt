package com.puxxbu.PatuliApp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.google.gson.Gson
import com.puxxbu.PatuliApp.data.api.config.ApiService
import com.puxxbu.PatuliApp.data.api.response.login.LoginResponse
import com.puxxbu.PatuliApp.data.api.response.profile.ProfileResponse
import com.puxxbu.PatuliApp.data.api.response.register.RegisterResponse
import com.puxxbu.PatuliApp.data.database.SessionDataPreferences
import com.puxxbu.PatuliApp.data.model.UserDataModel
import com.puxxbu.PatuliApp.utils.Event

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

    private val _profileResponse = MutableLiveData<ProfileResponse>()
    val profileResponse: LiveData<ProfileResponse> = _profileResponse


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
                _isLoading.value = false
                _responseMessage.value = Event(t.message.toString())
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
                    _responseMessage.value = Event(response.body()?.message.toString())
                } else {
                    val errorResponse =
                        Gson().fromJson(response.errorBody()?.string(), ProfileResponse::class.java)
                    _responseMessage.value = Event(errorResponse.message)
                    Log.d("TAG", "onResponse: ${errorResponse.message}")
                }
            }

            override fun onFailure(call: retrofit2.Call<ProfileResponse>, t: Throwable) {
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