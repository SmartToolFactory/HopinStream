package com.smarttoolfactory.data.api

import com.smarttoolfactory.data.constant.BASE_URL
import com.smarttoolfactory.data.model.remote.broadcast.Stages
import com.smarttoolfactory.data.model.remote.request.SessionTokenRequest
import com.smarttoolfactory.data.model.remote.sso.SessionTokenDTO
import com.smarttoolfactory.myapplication.model.broadcast.StageWithStatus
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface HopinApi {

    @POST("users/sso")
    @Headers(
        "Content-type: application/json"
    )
    suspend fun getEvent(
        @Header("Cookie") cookie: String,
        @Body eventSlug: SessionTokenRequest
    ): SessionTokenDTO

    @GET("api/v2/events/{event_id}/stages")
    suspend fun getStages(
        @Header("Authorization") token: String,
        @Path(value = "event_id") eventId: Long
    ): Stages

    @GET("api/v2/events/{event_id}/studio/{uuid}/status")
    suspend fun getStagesWithStatus(
        @Header("Authorization") token: String,
        @Path(value = "event_id") eventId: Long,
        @Path(value = "uuid") uuid: String
    ): StageWithStatus
}

fun getHopinApi(): HopinApi {
    return Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(HopinApi::class.java)
}
