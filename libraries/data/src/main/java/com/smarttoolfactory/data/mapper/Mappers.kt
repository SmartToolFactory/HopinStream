package com.smarttoolfactory.data.mapper

import com.smarttoolfactory.data.model.IEntity
import com.smarttoolfactory.data.model.local.SessionTokenEntity
import com.smarttoolfactory.data.model.remote.sso.SessionTokenDTO
import javax.inject.Inject

/**
 * Mapper for transforming objects between REST and database or REST/db and domain
 * as [IEntity]  which are Non-nullable to Non-nullable
 */
interface Mapper<I, O> {
    fun map(input: I): O
}

/**
 * Mapper for transforming objects between REST and database or REST/db and domain
 * as [List] of [IEntity] which are Non-nullable to Non-nullable
 */
interface ListMapper<I, O> : Mapper<List<I>, List<O>>

class SessionTokenMapper @Inject constructor() : Mapper<SessionTokenDTO, SessionTokenEntity> {
    override fun map(input: SessionTokenDTO): SessionTokenEntity {
        return SessionTokenEntity(input.token, System.currentTimeMillis())
    }
}
