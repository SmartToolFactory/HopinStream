package com.smarttoolfactory.stage.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smarttoolfactory.core.connectivity.ConnectionManager
import com.smarttoolfactory.core.util.checkInternetConnectionFlow
import com.smarttoolfactory.core.util.convertToFlowViewState
import com.smarttoolfactory.core.viewstate.Status
import com.smarttoolfactory.core.viewstate.ViewState
import com.smarttoolfactory.domain.usecase.StagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

@HiltViewModel
class StageViewModel @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val stageUseCase: StagesUseCase,
    private val connectionManager: ConnectionManager
) : ViewModel() {

    private val _streamState = MutableLiveData<ViewState<List<String>>>()
    val streamState: LiveData<ViewState<List<String>>>
        get() = _streamState

    fun getStreamLinks(token: String, eventId: Long) {
        stageUseCase.getVideoLinks(token, eventId)
            .checkInternetConnectionFlow(connectionManager)
            .convertToFlowViewState()
            .onStart {
                _streamState.postValue(ViewState(status = Status.LOADING))
            }
            .onEach {
                _streamState.postValue(it)
            }
            .launchIn(coroutineScope)
    }
}

class StageViewModelFactory @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val stageUseCase: StagesUseCase,
    private val connectionManager: ConnectionManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass != StageViewModel::class.java) {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
        return StageViewModel(
            coroutineScope,
            stageUseCase,
            connectionManager
        ) as T
    }
}
