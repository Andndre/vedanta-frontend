package com.example.brahmacarlearning.data.remote.response

import com.google.gson.annotations.SerializedName

data class SessionChatResponse(

	@field:SerializedName("sessionId")
	val sessionId: String
)
