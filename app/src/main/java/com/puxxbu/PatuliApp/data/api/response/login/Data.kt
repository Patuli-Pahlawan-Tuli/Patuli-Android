package com.puxxbu.PatuliApp.data.api.response.login

import com.google.gson.annotations.SerializedName

data class Data(

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("token")
	val token: String
)