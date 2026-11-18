package com.grupo2.sportconnect.domain.repository

/** Expected repository/service failure with a safe message that the UI can display. */
class RepositoryException(
    val userMessage: String,
    cause: Throwable? = null
) : Exception(userMessage, cause)
