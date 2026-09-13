package com.project.binar.okariru.data.auth.repository

import com.project.binar.okariru.core.error.AppFailure
import com.project.binar.okariru.data.auth.dto.AuthUser

enum class AuthStatus {
    UNKNOWN,
    AUTHENTICATED,
    UNAUTHENTICATED,
}

data class AuthUiState(
    val status: AuthStatus = AuthStatus.UNKNOWN,
    val user: AuthUser? = null,
    val isSubmitting: Boolean = false,
    val failure: AppFailure? = null,
    val errorMessage: String? = null,
//    val fieldErrors: Map<AuthField, ValidationError> = emptyMap(),
) {
    val isLoggedIn: Boolean get() = status == AuthStatus.AUTHENTICATED

    val isRestoringSession: Boolean get() = status == AuthStatus.UNKNOWN

    val isLoggedOut: Boolean get() = status == AuthStatus.UNAUTHENTICATED

//    val generalFailure: AppFailure?
//        get() = failure.takeUnless { it is AuthFailure.Validation }
//
//    fun errorOf(field: AuthField): ValidationError? = fieldErrors[field]
}