package com.smarttoolfactory.data.source

import com.smarttoolfactory.data.api.HopinApi
import com.smarttoolfactory.data.model.remote.broadcast.Stages
import com.smarttoolfactory.myapplication.model.broadcast.StageWithStatus
import javax.inject.Inject

interface StagesDataSource {

    suspend fun getStages(
        token: String,
        eventId: Long
    ): Stages

    suspend fun getStageWithStatus(
        token: String,
        eventId: Long,
        uuid: String
    ): StageWithStatus
}

class StagesDataSourceImpl @Inject constructor(private val hopinApi: HopinApi) : StagesDataSource {

    override suspend fun getStages(token: String, eventId: Long): Stages =
        hopinApi.getStages(token, eventId)

    override suspend fun getStageWithStatus(
        token: String,
        eventId: Long,
        uuid: String
    ): StageWithStatus = hopinApi.getStageWithStatus(token, eventId, uuid)
}
