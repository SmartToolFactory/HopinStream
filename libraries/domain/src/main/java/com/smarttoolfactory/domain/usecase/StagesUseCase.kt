package com.smarttoolfactory.domain.usecase

import com.smarttoolfactory.data.model.remote.broadcast.Stages
import com.smarttoolfactory.data.repository.StageRepository
import com.smarttoolfactory.domain.error.StageNotAvailableException
import com.smarttoolfactory.myapplication.model.broadcast.StageWithStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StagesUseCase @Inject constructor(private val repository: StageRepository) {

    private fun getStages(
        token: String,
        eventId: Long
    ): Flow<Stages> {
        return flow { emit(repository.getStages(token, eventId)) }
            .catch { cause: Throwable ->
                emitAll(flow { throw StageNotAvailableException(cause.message) })
            }
    }

    private fun getStageWithStatus(
        token: String,
        eventId: Long,
        uuid: String
    ): Flow<StageWithStatus> {
        return flow { emit(repository.getStageWithStatus(token, eventId, uuid)) }
            .catch { cause: Throwable ->
                emitAll(flow { throw StageNotAvailableException(cause.message) })
            }
    }

    fun getVideoLinks(token: String, eventId: Long): Flow<List<String>> {
        return getStages(token, eventId)
            .map {
                listOf<String>()
            }
    }
}
