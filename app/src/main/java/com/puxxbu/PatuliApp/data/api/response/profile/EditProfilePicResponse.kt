package com.puxxbu.PatuliApp.data.api.response.profile

import com.google.gson.annotations.SerializedName

data class EditProfilePicResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)