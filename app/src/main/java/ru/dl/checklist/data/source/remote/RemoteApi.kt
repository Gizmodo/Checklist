package ru.dl.checklist.data.source.remote

import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET
import ru.dl.checklist.data.model.ChecklistsDto


interface RemoteApi {
    @GET("/checklists")
    suspend fun getChecklist(): ApiResponse<ChecklistsDto>
}