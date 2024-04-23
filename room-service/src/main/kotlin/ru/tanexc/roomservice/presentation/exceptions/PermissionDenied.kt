package ru.tanexc.userservice.presentation.exceptions

class PermissionDenied(
    description: String? = null,
) : BaseException(
    message = "permission denied",
    statusCode = 403,
    description = description,
)
