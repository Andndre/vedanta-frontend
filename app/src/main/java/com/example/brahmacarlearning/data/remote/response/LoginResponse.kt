package com.example.brahmacarlearning.data.remote.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("token")
	val token: String
)
