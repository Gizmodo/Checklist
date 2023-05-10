package ru.dl.checklist.data.model.remote

import com.google.gson.annotations.SerializedName
import ru.dl.checklist.data.model.remote.ChecklistDto

data class ChecklistsDto(
    @SerializedName("checklists")
    val checklists: List<ChecklistDto?>?
)
