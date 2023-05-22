package com.puxxbu.PatuliApp.data.api.config

import com.puxxbu.PatuliApp.data.api.response.login.LoginResponse
import com.puxxbu.PatuliApp.data.api.response.register.RegisterResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("auth/register")
    fun postRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("passwordConfirmation") passwordConfirmation: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("auth/login")
    fun postLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>



}