package com.smarttoolfactory.login.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smarttoolfactory.core.connectivity.ConnectionManager
import com.smarttoolfactory.core.util.Event
import com.smarttoolfactory.core.util.checkInternetConnectionFlow
import com.smarttoolfactory.core.util.convertToFlowViewState
import com.smarttoolfactory.core.viewstate.Status
import com.smarttoolfactory.core.viewstate.ViewState
import com.smarttoolfactory.domain.model.UserSession
import com.smarttoolfactory.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val loginUseCase: LoginUseCase,
    private val connectionManager: ConnectionManager
) : ViewModel() {

    private val _loginState = MutableLiveData<Event<ViewState<UserSession>>>()
    val loginState
        get() = _loginState

    fun getTokenFromCookies(cookies: String): String? {
        return loginUseCase.getTokenFromCookies(cookies)
    }

    fun createUserSession(token: String, eventSlug: String) {
        loginUseCase.createUserSession(token, eventSlug)
            .checkInternetConnectionFlow(connectionManager)
            .convertToFlowViewState()
            .onStart {
                _loginState.postValue(Event(ViewState(status = Status.LOADING)))
            }
            .onEach {
                _loginState.postValue(Event(it))
            }
            .launchIn(coroutineScope)
    }
}

class LoginViewModelFactory @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val loginUseCase: LoginUseCase,
    private val connectionManager: ConnectionManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass != LoginViewModel::class.java) {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
        return LoginViewModel(
            coroutineScope,
            loginUseCase,
            connectionManager
        ) as T
    }
}
