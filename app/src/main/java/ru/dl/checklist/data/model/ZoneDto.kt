package ru.dl.checklist.data.model


import com.google.gson.annotations.SerializedName

data class ZoneDto(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("marks")
    val marks: List<MarkDto?>?,
    @SerializedName("zone")
    val zone: String?
)