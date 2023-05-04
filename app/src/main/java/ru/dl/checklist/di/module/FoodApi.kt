package ru.dl.checklist.di.module

import com.skydoves.sandwich.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import ru.dl.checklist.data.remote.ChecklistResponse


interface FoodApi {
    @GET("RSNataliyaD/hs/TSD/senduser")
    suspend fun getUsersNew(): ApiResponse<ChecklistResponse>

    @GET("RSNataliyaD/hs/mobAuthentication/{login}/{password}")
    suspend fun getAuthCheck(
        @Path("password") password: String,
        @Path("login") login: String
    ): ApiResponse<AuthResponseDto>

    @GET("KOMBPIT_NEW1/hs/TSD/getitemlist")
    suspend fun getFoodList(): ApiResponse<FoodResponseDto>

    @POST("KOMBPIT_NEW1/hs/TSD/sendorder")
    @Headers("Content-Type:application/json")
    suspend fun sendOrders(@Body body: OrderRequestDto): ApiResponse<OrderResponseDto>

    @POST("KOMBPIT_NEW1/hs/TSD/sendapproveorder")
    @Headers("Content-Type:application/json")
    suspend fun sendApproveOrders(@Body body: ApproveOrdersListDto): ApiResponse<OrderResponseDto>
}