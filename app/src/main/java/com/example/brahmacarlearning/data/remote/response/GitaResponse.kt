package com.example.brahmacarlearning.data.remote.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class GitaResponse(

	@field:SerializedName("babs")
	val babs: List<BabsItem>
)
@Entity(tableName = "bab")
data class BabsItem(

	@field:SerializedName("summary")
	val summary: String,

	@PrimaryKey
	@field:SerializedName("number")
	val number: Int,

	@field:SerializedName("title_hindi")
	val titleHindi: String,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("translation_indo")
	val translationIndo: String
)
