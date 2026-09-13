package com.project.binar.okariru.data.auth.dto

data class AuthUser(
    val name: String,
    val roles: List<String> = emptyList(),
    val id: Int? = null,
)