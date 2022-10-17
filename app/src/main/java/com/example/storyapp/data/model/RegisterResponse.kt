package com.example.storyapp.data.model

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class RegisterRequest(
	@SerializedName("name")
	val name: String,

	@SerializedName("email")
	val email: String,

	@SerializedName("password")
	val password: String,
)