package com.puxxbu.PatuliApp.data.api.response.file

import com.google.gson.annotations.SerializedName

data class FileHashResponse(

	@field:SerializedName("data")
	val data: Data
)