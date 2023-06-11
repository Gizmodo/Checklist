package ru.dl.checklist.data.model.remote


import com.google.gson.annotations.SerializedName

data class HouseChecklistsDto(
    @SerializedName("checklists_house")
    val checklistsHouse: List<HouseChecklistDto>?
)