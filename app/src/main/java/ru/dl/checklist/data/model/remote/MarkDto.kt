package ru.dl.checklist.data.model.remote


import com.google.gson.annotations.SerializedName

data class MarkDto(
    @SerializedName("points")
    val points: Int?,
    @SerializedName("title")
    val title: String?
)