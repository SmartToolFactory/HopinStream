package com.smarttoolfactory.data.repository

import com.smarttoolfactory.data.model.remote.broadcast.Stages
import com.smarttoolfactory.data.source.StagesDataSource
import com.smarttoolfactory.myapplication.model.broadcast.StageWithStatus
import javax.inject.Inject

interface StageRepository {

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


class StageRepositoryImpl @Inject constructor(private val stagesDataSource: StagesDataSource) :
    StageRepository {

    override suspend fun getStages(token: String, eventId: Long): Stages =
        stagesDataSource.getStages(token, eventId)

    override suspend fun getStageWithStatus(
        token: String,
        eventId: Long,
        uuid: String
    ): StageWithStatus = stagesDataSource.getStageWithStatus(token, eventId, uuid)

}