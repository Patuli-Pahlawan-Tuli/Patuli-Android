package com.puxxbu.PatuliApp.data.api.config

import com.puxxbu.PatuliApp.data.api.response.login.LoginResponse
import com.puxxbu.PatuliApp.data.api.response.profile.EditPasswordResponse
import com.puxxbu.PatuliApp.data.api.response.profile.EditProfilePicResponse
import com.puxxbu.PatuliApp.data.api.response.profile.ProfileResponse
import com.puxxbu.PatuliApp.data.api.response.register.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

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


    @GET("account/profile")
    fun getProfile(
       @Header("Authorization") token: String
    ): Call<ProfileResponse>

    @FormUrlEncoded
    @PUT("account/edit-password")
    fun editPassword(
        @Header("Authorization") token: String,
        @Field("oldPassword") oldPassword: String,
        @Field("newPassword") newPassword: String,
        @Field("passwordConfirmation") newPasswordConfirmation: String
    ): Call<EditPasswordResponse>


    @Multipart
    @PUT("account/edit-image")
    fun editProfilePicture(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
    ): Call<EditProfilePicResponse>


}