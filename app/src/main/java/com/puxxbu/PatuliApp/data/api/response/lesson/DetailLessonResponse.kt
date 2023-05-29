package com.puxxbu.PatuliApp.data.api.response.lesson

import com.google.gson.annotations.SerializedName

data class DetailLessonResponse(

	@field:SerializedName("data")
	val data: DataItem,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)