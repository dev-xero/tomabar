package dev.xero.tomabar.presentation.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.xero.tomabar.R

@Composable
fun OnboardingScreen(modifier: Modifier) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.padding(12.dp)
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(innerPadding)
                .padding(innerPadding),
        ) {
            OnboardingHeader()
            Spacer(modifier = Modifier.height(48.dp))

            Text(stringResource(R.string.intro))
            Spacer(modifier = Modifier.height(24.dp))

            Text(stringResource(R.string.intro_contd))
            Spacer(modifier = Modifier.height(24.dp))

            OnboardingRemoteInput("", { })
            Spacer(modifier = Modifier.height(24.dp))

            OnboardingButton({ })
        }
    }
}

@Composable
private fun OnboardingHeader(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_app),
            contentDescription = "tomabar",
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = "TomaBar",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}

@Composable
private fun OnboardingRemoteInput(
    value: String,
    onValueChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange() },
        label = { Text(stringResource(R.string.server_ip_address)) },
        singleLine = true,
        shape = RoundedCornerShape(24.dp),
        modifier = modifier
            .fillMaxWidth()
    )
}

@Composable
private fun OnboardingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { onClick() },
        modifier = modifier.fillMaxWidth()
    ) {
        Text(stringResource(R.string.connect), modifier = Modifier.padding(8.dp))
    }
}