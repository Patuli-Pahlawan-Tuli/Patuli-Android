package com.puxxbu.PatuliApp.data.api.config

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
import okhttp3.MultipartBody
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

    @GET("lessons/{type}")
    fun getLessonData(
        @Header("Authorization") token: String,
        @Path("type") type: String
    ): Call<LessonDataResponse>

    @GET("lessons/{type}/{number}")
    fun getDetailLesson(
        @Header("Authorization") token: String,
        @Path("type") type: String,
        @Path("number") number: Int
    ): Call<DetailLessonResponse>

    @GET("quizzes/{type}/{subQuiz}/{quizNumber}")
    fun getQuizbyNumber(
        @Header("Authorization") token: String,
        @Path("type") type: String,
        @Path("subQuiz") subQuiz: Int,
        @Path("quizNumber") quizNumber: Int
    ): Call<QuizResponse>

    @GET("hash")
    fun getFileHash(
        @Header("Authorization") token: String
    ): Call<FileHashResponse>


    @FormUrlEncoded
    @PUT("level/edit-completed-quiz-{type}")
    fun updateQuizProgress(
        @Header("Authorization") token: String,
        @Path("type") type: String,
        @Field("newQuestComplete") newQuestComplete: Int,
    ): Call<QuizProgressResponse>

    @FormUrlEncoded
    @PUT("level/edit-completed-sub-quiz")
    fun updateSubQuizProgress(
        @Header("Authorization") token: String,
        @Field("newQuestComplete") newQuestComplete: Int,
        @Field("subQuiz") subQuiz: Int,
    ): Call<QuizProgressResponse>


    @FormUrlEncoded
    @PUT("level/edit-exp")
    fun updateUserExperience(
        @Header("Authorization") token: String,
        @Field("newExp") newExp: Int,
    ): Call<QuizProgressResponse>

}