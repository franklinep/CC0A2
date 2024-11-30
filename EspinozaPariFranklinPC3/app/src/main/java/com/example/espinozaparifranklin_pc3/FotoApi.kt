package com.example.espinozaparifranklin_pc3

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface FotoApi {
    @Multipart
    @POST("/predict/")
    fun predict(@Part file: MultipartBody.Part): Call<ResponseData>
    //fun predict(@Body request: RequestData): Call<ResponseData>
}