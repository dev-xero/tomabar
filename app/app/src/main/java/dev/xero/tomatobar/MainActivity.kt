package dev.xero.tomatobar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dev.xero.tomatobar.presentation.screens.home.HomeScreen
import dev.xero.tomatobar.presentation.theme.TomatoBarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        enableEdgeToEdge()

        setContent {
            TomatoBarTheme {
                HomeScreen()
            }
        }
    }
}
