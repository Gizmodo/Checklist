package ru.dl.checklist.data.model.remote


import com.google.gson.annotations.SerializedName

data class HouseChecklistDto(
    @SerializedName("checks")
    val checks: List<HouseCheckDto>?,
    @SerializedName("time_end")
    val timeEnd: String?,
    @SerializedName("time_start")
    val timeStart: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("uuid")
    val uuid: String?
)