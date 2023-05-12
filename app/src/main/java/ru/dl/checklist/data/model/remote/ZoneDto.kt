package ru.dl.checklist.data.model.remote


import com.google.gson.annotations.SerializedName

data class ZoneDto(
    @SerializedName("marks")
    val marks: List<MarkDto>?,
    @SerializedName("zone")
    val zone: String?
)