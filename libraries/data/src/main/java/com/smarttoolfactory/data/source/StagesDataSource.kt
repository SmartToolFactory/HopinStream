package com.smarttoolfactory.data.source

import com.smarttoolfactory.data.api.HopinApi
import com.smarttoolfactory.data.model.remote.broadcast.Stages
import com.smarttoolfactory.myapplication.model.broadcast.StageWithStatus
import retrofit2.http.GET
import javax.inject.Inject

interface StagesDataSource {
    suspend fun getStages(
        token: String,
        eventId: Long
    ): Stages

    @GET("api/v2/events/{event_id}/studio/{uuid}/status")
    suspend fun getStagesWithStatus(
        token: String,
        eventId: Long,
        uuid: String
    ): StageWithStatus
}

class StagesDataSourceImpl @Inject constructor(private val hopinApi: HopinApi) : StagesDataSource {
    override suspend fun getStages(token: String, eventId: Long): Stages =
        hopinApi.getStages(token, eventId)

    override suspend fun getStagesWithStatus(
        token: String,
        eventId: Long,
        uuid: String
    ): StageWithStatus = hopinApi.getStagesWithStatus(token, eventId, uuid)

}