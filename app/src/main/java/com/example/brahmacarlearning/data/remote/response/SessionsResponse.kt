package com.example.brahmacarlearning.data.remote.response

import com.google.gson.annotations.SerializedName

data class SessionsResponse(


	@field:SerializedName("sessions")
	val sessions: List<SessionsItem>
)

data class SessionsItem(

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("title")
	val title: String
)
