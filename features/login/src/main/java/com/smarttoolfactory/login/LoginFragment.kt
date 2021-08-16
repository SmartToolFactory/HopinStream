package com.smarttoolfactory.login

import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import com.smarttoolfactory.core.di.CoreModuleDependencies
import com.smarttoolfactory.core.ui.fragment.DynamicNavigationFragment
import com.smarttoolfactory.core.viewstate.Status
import com.smarttoolfactory.domain.error.NoConnectivityException
import com.smarttoolfactory.login.constant.EVENT_SLUG
import com.smarttoolfactory.login.constant.SIGN_IN_URL
import com.smarttoolfactory.login.databinding.FragmentLoginBinding
import com.smarttoolfactory.login.di.DaggerLoginComponent
import com.smarttoolfactory.login.viewmodel.LoginViewModel
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject

class LoginFragment : DynamicNavigationFragment<FragmentLoginBinding>() {

    @Inject
    lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        initCoreDependentInjection()
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initWebView()

        loginViewModel.loginState.observe(
            viewLifecycleOwner,
            { event ->
                event.getContentIfNotHandled()?.let { viewState ->

                    when (viewState.status) {
                        Status.LOADING -> {
                            dataBinding.webView.visibility = View.GONE
                            dataBinding.progressBar.visibility = View.VISIBLE
                            dataBinding.tvStatus.visibility = View.VISIBLE
                        }

                        Status.SUCCESS -> {
                            dataBinding.progressBar.visibility = View.GONE
                            val direction = LoginFragmentDirections
                                .actionLoginFragmentToStageFragment(viewState.data!!)
                            findNavController().navigate(direction)
                        }

                        Status.ERROR -> {
                            dataBinding.progressBar.visibility = View.GONE
                            processError(viewState.error)
                        }
                    }
                }
            }
        )
    }

    private fun initWebView() {
        val webView: WebView = dataBinding.webView
        webView.settings.javaScriptEnabled = true

        webView.webViewClient = (
            object : WebViewClient() {

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    dataBinding.tvStatus.visibility = View.VISIBLE
                    dataBinding.tvStatus.text = "Login error occurred: $error"
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    val cookies: String = CookieManager.getInstance().getCookie(url)

                    val token = loginViewModel.getTokenFromCookies(cookies)
                    token?.let {
                        dataBinding.tvStatus.visibility = View.VISIBLE
                        dataBinding.tvStatus.text = "Login Successful fetching session token"
                        loginViewModel.createUserSession(token, EVENT_SLUG)
                    }
                }
            }
            )
        webView.loadUrl(SIGN_IN_URL)
    }

    private fun processError(error: Throwable?) {
        dataBinding.tvStatus.text = when (error) {
            is NoConnectivityException -> {
                "Make sure that you are connected to internet"
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
    }

    private fun initCoreDependentInjection() {

        val coreModuleDependencies = EntryPointAccessors.fromApplication(
            requireActivity().applicationContext,
            CoreModuleDependencies::class.java
        )

        DaggerLoginComponent.factory().create(
            dependentModule = coreModuleDependencies,
            fragment = this
        )
            .inject(this)
    }

    override fun getLayoutRes(): Int = R.layout.fragment_login
}
