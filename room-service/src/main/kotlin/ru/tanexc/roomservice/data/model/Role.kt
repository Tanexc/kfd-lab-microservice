package ru.tanexc.roomservice.data.model

enum class Role {
    User, Admin, Org
}

fun String.buildRole(): Role = when (this) {
    "Admin" -> Role.Admin
    "User" -> Role.User
    "Org" -> Role.Org
    else -> Role.User
}