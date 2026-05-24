package dev.xero.tomabar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dev.xero.tomabar.presentation.screens.home.HomeScreen
import dev.xero.tomabar.presentation.screens.onboarding.OnboardingScreen
import dev.xero.tomabar.presentation.theme.TomaBarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        installSplashScreen()
        enableEdgeToEdge()

        setContent {
            TomaBarTheme {
                Scaffold (containerColor = MaterialTheme.colorScheme.surface) { innerPadding ->
                      OnboardingScreen(modifier = Modifier.padding(innerPadding))
//                    HomeScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}