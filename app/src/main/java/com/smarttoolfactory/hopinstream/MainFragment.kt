package com.smarttoolfactory.hopinstream

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.smarttoolfactory.core.ui.fragment.DynamicNavigationFragment
import com.smarttoolfactory.core.viewstate.Status
import com.smarttoolfactory.domain.error.NoConnectivityException
import com.smarttoolfactory.domain.error.TokenNotAvailableException
import com.smarttoolfactory.hopinstream.databinding.FragmentMainBinding
import com.smarttoolfactory.hopinstream.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : DynamicNavigationFragment<FragmentMainBinding>() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.loginState.observe(
            viewLifecycleOwner,
            { viewState ->

                when (viewState.status) {

                    Status.LOADING -> {
                        dataBinding.progressBar.visibility = View.VISIBLE
                        dataBinding.tvError.visibility = View.GONE
                    }

                    Status.SUCCESS -> {
                        dataBinding.progressBar.visibility = View.GONE

//                    val direction =
//                        MainFragmentDirections.actionMainFragmentToStageFragment(viewState.data!!)

                        val bundle = bundleOf("sessionArgs" to viewState.data!!)

                        navigateWithInstallMonitor(
                            findNavController(),
                            R.id.nav_graph_stage,
                            bundle
                        )
                    }

                    Status.ERROR -> {
                        dataBinding.progressBar.visibility = View.GONE
                        processError(viewState.error)
                    }
                }
            }
        )

//        navigateWithInstallMonitor( v, R.id.nav_graph_login)
//        navigateWithInstallMonitor(findNavController(), R.id.nav_graph_stage)
    }

    private fun processError(error: Throwable?) {
        when (error) {
            is NoConnectivityException -> {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    "Make sure that you are connected to internet", Snackbar.LENGTH_LONG
                ).show()
            }
            is TokenNotAvailableException -> {
                navigateWithInstallMonitor(findNavController(), R.id.nav_graph_login)
            }
            else -> {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    "Unknown error has occurred. Please restart the app or contact admin",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun getLayoutRes(): Int = R.layout.fragment_main
}
