package ru.dl.checklist.data.model.remote


import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("group")
    val group: String?,
    @SerializedName("user")
    val user: String?
)