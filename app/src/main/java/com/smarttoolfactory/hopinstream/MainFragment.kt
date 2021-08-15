package com.smarttoolfactory.hopinstream

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.smarttoolfactory.core.ui.fragment.DynamicNavigationFragment
import com.smarttoolfactory.hopinstream.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : DynamicNavigationFragment<FragmentMainBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonLogin = view.findViewById<Button>(R.id.btnLogin)
        val buttonStage = view.findViewById<Button>(R.id.btnStage)

        buttonLogin.setOnClickListener {
            navigateWithInstallMonitor(findNavController(), R.id.nav_graph_login)
        }

        buttonStage.setOnClickListener {
            navigateWithInstallMonitor(findNavController(), R.id.nav_graph_stage)
        }
    }

    override fun getLayoutRes(): Int = R.layout.fragment_main
}
