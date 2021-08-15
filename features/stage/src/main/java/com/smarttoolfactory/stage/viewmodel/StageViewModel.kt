package com.smarttoolfactory.stage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smarttoolfactory.core.connectivity.ConnectionManager
import com.smarttoolfactory.domain.usecase.StagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope

@HiltViewModel
class StageViewModel @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val stageUseCase: StagesUseCase,
    private val connectionManager: ConnectionManager
) : ViewModel()

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
