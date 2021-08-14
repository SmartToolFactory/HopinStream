package com.smarttoolfactory.domain.usecase

import com.smarttoolfactory.data.model.remote.broadcast.Stages
import com.smarttoolfactory.data.repository.StageRepository
import com.smarttoolfactory.domain.error.InActiveBroadcastException
import com.smarttoolfactory.domain.error.StageNotAvailableException
import com.smarttoolfactory.myapplication.model.broadcast.StageWithStatus
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

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
            .map { userStages: Stages ->
                val stage = userStages.stages.first()
                val uuid = stage.uuid
                uuid
            }
            .flatMapConcat { uuid ->
                getStageWithStatus(token, eventId, uuid)
            }
            .map {
                it.videoChannels
                    .filter { videoChannel ->
                        (
                            videoChannel.deliveryType == "hopin" ||
                                videoChannel.deliveryType == "ivs"
                            ) &&
                            videoChannel.status == "active"
                    }.map { channel ->
                        channel.streamUrl
                    }
            }
            .map {
                if (it.isEmpty()) {
                    throw InActiveBroadcastException()
                } else it
            }
    }
}
