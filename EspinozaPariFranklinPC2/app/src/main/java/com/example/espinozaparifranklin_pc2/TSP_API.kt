package com.example.espinozaparifranklin_pc2

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface TSP_API {
    @POST("/predict/")
    fun predict(@Body request:RequestData): Call<ResponseData>
}