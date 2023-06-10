package com.puxxbu.PatuliApp.data.api.response.profile

import com.google.gson.annotations.SerializedName

data class Data(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("password")
	val password: String,

	@field:SerializedName("completedSubQuiz")
	val completedSubQuiz: Int,

	@field:SerializedName("imageUrl")
	val imageUrl: String,

	@field:SerializedName("__v")
	val v: Int,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("_id")
	val id: String,

	@field:SerializedName("accountLevel")
	val accountLevel: Int,

	@field:SerializedName("accountExp")
	val accountExp: Int,

	@field:SerializedName("completedQuiz")
	val completedQuiz: Int,

	@field:SerializedName("email")
	val email: String
)