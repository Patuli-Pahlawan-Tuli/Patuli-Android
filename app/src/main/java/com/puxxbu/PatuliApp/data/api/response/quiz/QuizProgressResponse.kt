package com.puxxbu.PatuliApp.data.api.response.quiz

import com.google.gson.annotations.SerializedName

data class QuizProgressResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)