package com.example.brahmacarlearning.data.remote.response

import com.google.gson.annotations.SerializedName

data class ChatResponse(

	@field:SerializedName("response")
	val response: String,

	@field:SerializedName("error")
	val error: Boolean
)
