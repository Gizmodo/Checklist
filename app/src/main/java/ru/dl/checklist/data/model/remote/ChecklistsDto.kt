package ru.dl.checklist.data.model.remote

import com.google.gson.annotations.SerializedName

data class ChecklistsDto(
    @SerializedName("checklists")
    val checklists: List<ChecklistDto>?
)
