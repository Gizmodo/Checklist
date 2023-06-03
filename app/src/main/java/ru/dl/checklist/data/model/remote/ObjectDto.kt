package ru.dl.checklist.data.model.remote


import com.google.gson.annotations.SerializedName

data class ObjectDto(
    @SerializedName("name")
    val name: String?,
    @SerializedName("uuid")
    val uuid: String?
)