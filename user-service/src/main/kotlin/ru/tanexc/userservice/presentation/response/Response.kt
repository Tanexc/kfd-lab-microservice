package ru.tanexc.userservice.presentation.response

sealed class Response<T>(
    val data: T?,
    val message: String = "response"
): Any() {
    class DataResponse<T>(
        data: T?,
        message: String = "data response"
    ) : Response<T>(data, message)
}