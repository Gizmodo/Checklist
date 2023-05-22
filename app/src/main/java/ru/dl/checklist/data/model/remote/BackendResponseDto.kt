package ru.dl.checklist.data.model.remote

import com.google.gson.annotations.SerializedName

data class BackendResponseDto(
    @SerializedName("message")
    val message: String?,
    @SerializedName("result")
    val result: Boolean?
)