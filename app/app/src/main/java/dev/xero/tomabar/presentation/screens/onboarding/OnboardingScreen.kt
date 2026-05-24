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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.xero.tomabar.R
@Composable
fun OnboardingScreen(
    onConnected: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = viewModel(factory = OnboardingVMFactory.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var ip by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state) {
        when (val s = state) {
            is OnboardingUiState.Connected -> onConnected()
            is OnboardingUiState.Error -> {
                snackbarHostState.showSnackbar(
                    message = s.message,
                    withDismissAction = true
                )
            }
            else -> Unit
        }
    }

    val isConnecting = state is OnboardingUiState.Connecting

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
            Spacer(Modifier.height(48.dp))
            Text(stringResource(R.string.intro))
            Spacer(Modifier.height(24.dp))
            Text(stringResource(R.string.intro_contd))
            Spacer(Modifier.height(24.dp))

            OnboardingRemoteInput(
                value = ip,
                onValueChange = {
                    ip = it
                    viewModel.resetError()
                },
                isError = state is OnboardingUiState.Error,
                enabled = !isConnecting,
            )

            Spacer(Modifier.height(24.dp))
            OnboardingButton(
                onClick = { viewModel.connect(ip) },
                enabled = ip.isNotBlank() && !isConnecting,
                isLoading = isConnecting,
            )
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
    onValueChange: (String) -> Unit,
    isError: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.server_ip_address)) },
        singleLine = true,
        isError = isError,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
        shape = RoundedCornerShape(24.dp),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun OnboardingButton(
    onClick: () -> Unit,
    enabled: Boolean,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth()
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Text(stringResource(R.string.connect), modifier = Modifier.padding(8.dp))
        }
    }
}