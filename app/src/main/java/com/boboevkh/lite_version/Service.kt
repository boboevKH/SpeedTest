package com.boboevkh.lite_version

import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface Service {
    @POST("/")
    fun sendData(
        @Body data: JSONObject
    ): Call<Void>
}