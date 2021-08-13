package com.smarttoolfactory.data.api

import com.smarttoolfactory.data.model.remote.broadcast.Stages
import com.smarttoolfactory.data.model.remote.request.SessionTokenRequest
import com.smarttoolfactory.data.model.remote.sso.SessionTokenDTO
import com.smarttoolfactory.myapplication.model.broadcast.StageWithStatus
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface HopinApi {

    /**
     * Fetch session token in JWT format
     * @param cookie contains token in format of ***user.token=...***
     * @param eventSlug is [SessionTokenRequest] that is event id
     *
     * @throws [retrofit2.HttpException] **HTTP 401** when cookie or eventSlug is not valid
     *
     */
    @POST("users/sso")
    @Headers("Content-type: application/json")
    suspend fun getSessionToken(
        @Header("Cookie") cookie: String,
        @Body eventSlug: SessionTokenRequest
    ): SessionTokenDTO

    /**
     * Get [Stages] that contains list of[Stage] from Rest API
     * @param token Bearer token for authorization
     * @param eventId event id that is included inside JWT
     *
     * @throws [retrofit2.HttpException] with **403** when eventId is wrong,
     * **401** when token is not valid
     */
    @GET("api/v2/events/{event_id}/stages")
    suspend fun getStages(
        @Header("Authorization") token: String,
        @Path(value = "event_id") eventId: Long
    ): Stages

    /**
     * Get a stage and details of it for a specific [uuid] for a event specified with [eventId]
     *
     * @throws [retrofit2.HttpException] with **403** when eventId is wrong,
     * **401** when token is not valid
     * **404** when uuid is not valid
     */
    @GET("api/v2/events/{event_id}/studio/{uuid}/status")
    suspend fun getStageWithStatus(
        @Header("Authorization") token: String,
        @Path(value = "event_id") eventId: Long,
        @Path(value = "uuid") uuid: String
    ): StageWithStatus
}
