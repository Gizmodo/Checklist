package ru.dl.checklist.data.model.remote


import com.google.gson.annotations.SerializedName

data class TemplateDto(
    @SerializedName("uuid")
    val uuid: String?,
    @SerializedName("name")
    val name: String?
)