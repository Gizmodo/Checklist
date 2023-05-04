package ru.dl.checklist.data.remote
import com.google.gson.annotations.SerializedName

data class Mark(
    @SerializedName("points")
    val points: Int?,
    @SerializedName("title")
    val title: String?
)