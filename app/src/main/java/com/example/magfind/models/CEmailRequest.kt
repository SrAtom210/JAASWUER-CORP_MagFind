package com.example.magfind.models

data class EmailRequest(
    val email: String
)

data class VerifyCodeRequest(
    val email: String,
    val code: String
)

data class PasswordResetRequest(
    val email: String,
    val code: String,
    val new_password: String
)

data class GenericResponse(
    val status: String, // "ok" o "error"
    val message: String
)