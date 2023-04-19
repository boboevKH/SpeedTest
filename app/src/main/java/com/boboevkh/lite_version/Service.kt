package com.boboevkh.lite_version

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface Service {
    @FormUrlEncoded
    @POST("/")
    fun sendData(
        @Field("error_location") userIp: String,
        @Field("message") message: String,
        @Field("type") type: String

    ): Call<Message>
}