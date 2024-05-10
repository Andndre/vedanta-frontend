package com.example.brahmacarlearning.data.remote.response

import com.google.gson.annotations.SerializedName

data class DetailGitaResponse(

	@field:SerializedName("summary")
	val summary: String,

	@field:SerializedName("number")
	val number: Int,

	@field:SerializedName("title_hindi")
	val titleHindi: String,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("translation_indo")
	val translationIndo: String
)
