package ru.dl.checklist.data.remote
import com.google.gson.annotations.SerializedName

data class ChecklistResponseItem(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("marks")
    val marks: List<Mark?>?,
    @SerializedName("zone")
    val zone: String?
)