package ru.dl.checklist.data.model.remote


import com.google.gson.annotations.SerializedName

data class CheckedObjectsDto(
    @SerializedName("objects")
    val objects: List<ObjectDto>?
)