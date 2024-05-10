package com.example.brahmacarlearning.data.remote.response

import com.google.gson.annotations.SerializedName

data class GetChatBySessionResponse(

	@field:SerializedName("session")
	val session: Session,

	@field:SerializedName("error")
	val error: Boolean
)

data class Session(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("history")
	val history: List<HistoryItem>,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("userId")
	val userId: String
)

data class HistoryItem(

	@field:SerializedName("role")
	val role: String,

	@field:SerializedName("parts")
	val parts: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("sessionId")
	val sessionId: String
)
