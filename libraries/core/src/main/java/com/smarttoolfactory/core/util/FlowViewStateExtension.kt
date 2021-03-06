package com.smarttoolfactory.core.util

import com.smarttoolfactory.core.connectivity.ConnectionManager
import com.smarttoolfactory.core.viewstate.Status
import com.smarttoolfactory.core.viewstate.ViewState
import com.smarttoolfactory.domain.error.NoConnectivityException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

fun <T> Flow<T>.convertToFlowViewState(
    dispatcher: CoroutineDispatcher = Dispatchers.Default
): Flow<ViewState<T>> {
    return this
        .map { list -> ViewState(status = Status.SUCCESS, data = list) }
        .catch { cause: Throwable -> emitAll(flowOf(ViewState(Status.ERROR, error = cause))) }
        .flowOn(dispatcher)
}

fun <T> Flow<T>.checkInternetConnectionFlow(
    connectionManager: ConnectionManager,
    dispatcher: CoroutineDispatcher = Dispatchers.Default
): Flow<T> {
    return this
        .onStart {
            if (!connectionManager.isNetworkAvailable()) {
                throw NoConnectivityException("Make sure that you are connected a network")
            }
        }
        .flowOn(dispatcher)
}
