package ru.dl.checklist.data.model.remote


import com.google.gson.annotations.SerializedName
import ru.dl.checklist.data.model.remote.MarkDto

data class ZoneDto(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("marks")
    val marks: List<MarkDto?>?,
    @SerializedName("zone")
    val zone: String?
)