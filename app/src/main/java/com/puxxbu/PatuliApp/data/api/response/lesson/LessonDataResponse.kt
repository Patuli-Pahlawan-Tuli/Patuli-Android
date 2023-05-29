package com.puxxbu.PatuliApp.data.api.response.lesson

import com.google.gson.annotations.SerializedName

data class LessonDataResponse(

	@field:SerializedName("data")
	val data: List<DataItem>,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)