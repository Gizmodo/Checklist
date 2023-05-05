package ru.dl.checklist.data.source.remote

import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET
import ru.dl.checklist.data.remote.ChecklistDto


interface RemoteApi {
    @GET("/checklist")
    suspend fun getChecklist(): ApiResponse<ChecklistDto>
}