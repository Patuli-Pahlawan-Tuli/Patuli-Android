package com.puxxbu.PatuliApp.data.api.response.file

import com.google.gson.annotations.SerializedName

data class Data(

	@field:SerializedName("abjad_lite")
	val abjadLite: String,

	@field:SerializedName("kata")
	val kata: String,

	@field:SerializedName("kata_lite")
	val kataLite: String,

	@field:SerializedName("abjad")
	val abjad: String,

	@field:SerializedName("angka")
	val angka: String,

	@field:SerializedName("angka_lite")
	val angkaLite: String
)