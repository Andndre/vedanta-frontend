package com.example.brahmacarlearning.data.local.pref

data class UserModel(
    val email: String,
    val token: String,
    val isLogin: Boolean = false,
//    val role: UserRole
)

//enum class UserRole {
//    TEACHER, STUDENT
//}