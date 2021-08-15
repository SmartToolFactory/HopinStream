package com.smarttoolfactory.stage

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.smarttoolfactory.core.di.CoreModuleDependencies
import com.smarttoolfactory.core.ui.fragment.DynamicNavigationFragment
import com.smarttoolfactory.stage.databinding.FragmentStageBinding
import com.smarttoolfactory.stage.di.DaggerStageComponent
import com.smarttoolfactory.stage.viewmodel.StageViewModel
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject

class StageFragment : DynamicNavigationFragment<FragmentStageBinding>() {

    private val args: StageFragmentArgs by navArgs()

    @Inject
    lateinit var stageViewModel: StageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        initCoreDependentInjection()
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initCoreDependentInjection() {

        val coreModuleDependencies = EntryPointAccessors.fromApplication(
            requireActivity().applicationContext,
            CoreModuleDependencies::class.java
        )

        DaggerStageComponent.factory().create(
            dependentModule = coreModuleDependencies,
            fragment = this
        )
            .inject(this)
    }

    override fun getLayoutRes(): Int = R.layout.fragment_stage
}
