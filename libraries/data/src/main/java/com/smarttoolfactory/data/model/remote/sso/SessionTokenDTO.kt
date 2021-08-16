package com.smarttoolfactory.data.model.remote.sso

import com.smarttoolfactory.data.model.DataTransferObject

/**
 * Session token of the user fetched via REST api
 */
data class SessionTokenDTO(val token: String) : DataTransferObject
