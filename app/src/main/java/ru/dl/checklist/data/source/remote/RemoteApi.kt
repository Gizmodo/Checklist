package ru.dl.checklist.data.source.remote

import com.skydoves.sandwich.ApiResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import ru.dl.checklist.data.model.remote.BackendResponseDto
import ru.dl.checklist.data.model.remote.ChecklistsDto
import ru.dl.checklist.data.model.remote.ReadyChecklist


interface RemoteApi {
    @GET("/checklists")
    suspend fun getChecklist(): ApiResponse<ChecklistsDto>

    @Multipart
    @POST("/uploadImages")
    suspend fun uploadImages(@Part images: List<MultipartBody.Part>): ApiResponse<BackendResponseDto>

    @POST("/uploadMarks")
    suspend fun uploadMarks(@Body marks: ReadyChecklist): ApiResponse<BackendResponseDto>
}