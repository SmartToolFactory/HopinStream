package com.smarttoolfactory.hopinstream.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.smarttoolfactory.core.connectivity.ConnectionManager
import com.smarttoolfactory.core.util.checkInternetConnectionFlow
import com.smarttoolfactory.core.util.convertToFlowViewState
import com.smarttoolfactory.core.viewstate.Status
import com.smarttoolfactory.core.viewstate.ViewState
import com.smarttoolfactory.domain.model.UserSession
import com.smarttoolfactory.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

@HiltViewModel
class MainViewModel @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val loginUseCase: LoginUseCase,
    private val connectionManager: ConnectionManager
) : ViewModel() {

    private val _loginState = MutableLiveData<ViewState<UserSession>>()

    val loginState
        get() = _loginState

    init {
        loginUseCase.getUserSession()
            .checkInternetConnectionFlow(connectionManager)
            .convertToFlowViewState()
            .onStart {
                _loginState.postValue(ViewState(status = Status.LOADING))
            }
            .onEach {

                // This is fake delay to simulate loading, since it takes ms to load from db
                if (it.status != Status.LOADING) {
                    delay(300)
                }
                _loginState.postValue(it)
            }
            .launchIn(coroutineScope)
    }
}
