package ru.dl.checklist.data.model.remote


import com.google.gson.annotations.SerializedName

data class ChecklistDto(
    @SerializedName("uuid")
    val uuid: String?,
    @SerializedName("address")
    val address: String?,
    @SerializedName("audit_date")
    val auditDate: String?,
    @SerializedName("checker")
    val checker: String?,
    @SerializedName("senior")
    val senior: String?,
    @SerializedName("short_name")
    val shortName: String?,
    @SerializedName("zones")
    val zones: List<ZoneDto>?
)