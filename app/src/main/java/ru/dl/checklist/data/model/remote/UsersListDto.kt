package ru.dl.checklist.data.model.remote


import com.google.gson.annotations.SerializedName

data class UsersListDto(
    @SerializedName("users")
    val users: List<UserDto>?
)