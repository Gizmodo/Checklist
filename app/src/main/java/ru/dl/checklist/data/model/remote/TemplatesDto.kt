package ru.dl.checklist.data.model.remote

import com.google.gson.annotations.SerializedName

data class TemplatesDto(
    @SerializedName("templates")
    val templates: List<TemplateDto>?
)