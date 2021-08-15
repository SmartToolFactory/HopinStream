package com.smarttoolfactory.login.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.smarttoolfactory.login.viewmodel.LoginViewModel
import com.smarttoolfactory.login.viewmodel.LoginViewModelFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@InstallIn(FragmentComponent::class)
@Module
class LoginModule {

    @Provides
    fun provideLoginViewModel(fragment: Fragment, factory: LoginViewModelFactory) =
        ViewModelProvider(fragment, factory).get(LoginViewModel::class.java)

    @Provides
    fun provideCoroutineScope() =
        CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
}
