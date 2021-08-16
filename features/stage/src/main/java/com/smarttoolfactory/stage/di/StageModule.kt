package com.smarttoolfactory.stage.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.smarttoolfactory.stage.viewmodel.StageViewModel
import com.smarttoolfactory.stage.viewmodel.StageViewModelFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@InstallIn(FragmentComponent::class)
@Module
class StageModule {

    @Provides
    fun provideStageViewModel(fragment: Fragment, factory: StageViewModelFactory) =
        ViewModelProvider(fragment, factory).get(StageViewModel::class.java)

    @Provides
    fun provideCoroutineScope() =
        CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
}
