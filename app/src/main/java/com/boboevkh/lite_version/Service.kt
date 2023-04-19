package com.boboevkh.lite_version

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface Service {
    @POST("/")
    fun sendData(
        @Body data: Message
    ): Call<Message>
}