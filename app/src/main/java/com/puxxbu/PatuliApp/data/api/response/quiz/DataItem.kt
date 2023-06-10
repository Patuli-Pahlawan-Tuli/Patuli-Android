package com.puxxbu.PatuliApp.data.api.response.quiz

import com.google.gson.annotations.SerializedName

data class DataItem(

	@field:SerializedName("quiz")
	val quiz: String,

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("languageType")
	val languageType: String,

	@field:SerializedName("answer")
	val answer: String,

	@field:SerializedName("subQuiz")
	val subQuiz: Int,

	@field:SerializedName("_id")
	val id: String,

	@field:SerializedName("quizNumber")
	val quizNumber: Int,

	@field:SerializedName("quizDifficulty")
	val quizDifficulty: String
)