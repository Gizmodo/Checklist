package ru.dl.checklist.data.model.remote


import com.google.gson.annotations.SerializedName

data class HouseCheckDto(
    @SerializedName("end")
    val end: Boolean?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("next")
    val next: String?,
    @SerializedName("photo_required")
    val photoRequired: Boolean?,
    @SerializedName("start")
    val start: Boolean?,
    @SerializedName("uuid")
    val uuid: String?
)