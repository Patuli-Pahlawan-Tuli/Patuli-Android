package com.puxxbu.PatuliApp.data.api.response.lesson

import com.google.gson.annotations.SerializedName

data class DataItem(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("lessonNumber")
	val lessonNumber: Int,

	@field:SerializedName("imageUrl")
	val imageUrl: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("_id")
	val id: String,

	@field:SerializedName("lessonType")
	val lessonType: String,

	@field:SerializedName("lessonName")
	val lessonName: String
)