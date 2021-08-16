package com.smarttoolfactory.stage

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.material.snackbar.Snackbar
import com.smarttoolfactory.core.di.CoreModuleDependencies
import com.smarttoolfactory.core.ui.fragment.DynamicNavigationFragment
import com.smarttoolfactory.core.viewstate.Status
import com.smarttoolfactory.domain.error.InActiveBroadcastException
import com.smarttoolfactory.domain.error.NoConnectivityException
import com.smarttoolfactory.domain.error.StageNotAvailableException
import com.smarttoolfactory.domain.model.UserSession
import com.smarttoolfactory.stage.databinding.FragmentStageBinding
import com.smarttoolfactory.stage.di.DaggerStageComponent
import com.smarttoolfactory.stage.viewmodel.StageViewModel
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject

class StageFragment : DynamicNavigationFragment<FragmentStageBinding>() {

    private val args: StageFragmentArgs by navArgs()

    @Inject
    lateinit var stageViewModel: StageViewModel

    private var exoPlayer: SimpleExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        initCoreDependentInjection()
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userSession: UserSession = args.sessionArgs
        stageViewModel.getStreamLinks(userSession.sessionToken, userSession.evenId)

        stageViewModel.streamState.observe(
            viewLifecycleOwner,
            { viewState ->

                when (viewState.status) {
                    Status.LOADING -> {
                        dataBinding.progressBar.visibility = View.VISIBLE
                    }

                    Status.ERROR -> {
                        dataBinding.progressBar.visibility = View.GONE
                        processError(viewState.error)
                    }
                    Status.SUCCESS -> {
                        dataBinding.progressBar.visibility = View.GONE
                        startVideoStreamFromUrl(viewState.data!!)
                    }
                }
            }
        )
    }

    private fun startVideoStreamFromUrl(url: List<String>) {
        url.firstOrNull()?.let { streamUrl ->
            val playerView = dataBinding.playerView
            playerView.visibility = View.VISIBLE

            // Global settings.
            exoPlayer = SimpleExoPlayer.Builder(requireContext())
                .setMediaSourceFactory(
                    DefaultMediaSourceFactory(requireContext()).setLiveTargetOffsetMs(5000)
                )
                .build()

            // Per MediaItem settings.
            val mediaItem: MediaItem = MediaItem.Builder()
                .setUri(streamUrl)
                .setLiveMaxPlaybackSpeed(1.02f)
                .build()
            exoPlayer?.apply {
                setMediaItem(mediaItem)
                playerView.player = exoPlayer
                prepare()
                playWhenReady = true
            }
        }
    }

    override fun onPause() {
        super.onPause()
        exoPlayer?.run {
            stop()
            release()
        }
    }

    private fun processError(error: Throwable?) {

        val errorMessage: String = when (error) {
            is NoConnectivityException -> {
                "Make sure that you are connected to internet"
            }

            is StageNotAvailableException -> {
                "Stage is not available due to ${error.message}"
            }
            is InActiveBroadcastException -> {
                "There is no active broadcast link of hopin or ivs"
            }

            else -> {
                error?.message?.apply {
                    if (contains("401")) {
                        "Http 401 error, Requires authentication"
                    }
                } ?: run {
                    "Error occurred: ${error?.message}"
                }
            }
        }

        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            errorMessage, Snackbar.LENGTH_LONG
        ).show()
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
