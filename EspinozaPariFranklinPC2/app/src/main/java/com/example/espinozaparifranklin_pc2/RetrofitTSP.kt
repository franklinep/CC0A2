package com.example.espinozaparifranklin_pc2

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitTSP {
    private const val BASE_URL = "https://franklinep-hg-finaltsp.hf.space"
    val tsp_api:TSP_API by lazy {

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(TSP_API::class.java)
    }
}