package com.puxxbu.PatuliApp.data.api.response.quiz

import com.google.gson.annotations.SerializedName

data class QuizResponse(

	@field:SerializedName("data")
	val data: List<DataItem>,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)