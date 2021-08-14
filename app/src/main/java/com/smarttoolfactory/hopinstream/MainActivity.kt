package com.smarttoolfactory.hopinstream

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.smarttoolfactory.domain.usecase.LoginUseCase
import com.smarttoolfactory.domain.usecase.StagesUseCase
import com.smarttoolfactory.hopinstream.ui.theme.HopinStreamTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var loginUseCase: LoginUseCase

    @Inject
    lateinit var stagesUseCase: StagesUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Toast.makeText(
            applicationContext,
            "LoginUseCase: $loginUseCase, stagesUseCase: $stagesUseCase",
            Toast.LENGTH_SHORT
        ).show()

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
