package com.smarttoolfactory.hopinstream

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.smarttoolfactory.data.model.remote.request.SessionTokenRequest
import com.smarttoolfactory.domain.usecase.LoginUseCase
import com.smarttoolfactory.domain.usecase.StagesUseCase
import com.smarttoolfactory.hopinstream.ui.theme.HopinStreamTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val cookie =
        "user.token=QDla%2Fin5Ryv071eziBpHb56KNwQQQdROaealpQHGZHvBxRKe%2FwZwgUFbGz" +
                "ks3OaJRs%2BWWNSZybMwgDKNuJeX5rnwr7OggNXPX5w%3D--XxJELxpUNISUuZl6--" +
                "Rts4nWVmI4uJCKgVDnyT%2Bw%3D%3D"

    private val sessionToken =
        "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI3MmVhYjBlNy05ZGIyLTRmNmEtOTQyMC1hNDY4YjQzYzc" +
                "zZDgiLCJzdWIiOjIxOTY5OTAsInBlcnNvbmFfaWQiOjMyNDUxMSwicmVnaXN0cmF0aW9u" +
                "X2lkIjo1Mzc0NzA5LCJldmVudF9pZCI6MTA4NTY0LCJyb2xlIjoib3JnYW5pc2VyIiwibXV" +
                "sdGlwbGVfY29ubiI6dHJ1ZSwiZGF0YV9zZWdyZWdhdGVkIjpmYWxzZX0.AAhrVXd5LYYy6YR" +
                "eFCN3hAc7e9d4z0FltcmPt_YdesY"

    private val request = SessionTokenRequest("hopincon2022")

    @Inject
    lateinit var loginUseCase: LoginUseCase

    @Inject
    lateinit var stagesUseCase: StagesUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

        loginUseCase.createUserSession(cookie, request)
//            .onEach {
//                println("🎃 User session: in thread: " +
//                        "${Thread.currentThread().name}," +
//                        " ${it.sessionToken}, " +
//                        "eventId: ${it.evenId}")
//            }
            .flatMapConcat {
                stagesUseCase.getVideoLinks(it.sessionToken, it.evenId)
            }
            .onEach {
                it.forEach {
                    println("🍏 Links: $it")
                }
            }
            .launchIn(coroutineScope)

        setContent {
            HopinStreamTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HopinStreamTheme {
        Greeting("Android")
    }
}
