package ru.tanexc.userservice.presentation.exceptions

class ServerError(
    description: String? = null
) : BaseException(
    message = "Some error",
    statusCode = 500,
    description = description
)