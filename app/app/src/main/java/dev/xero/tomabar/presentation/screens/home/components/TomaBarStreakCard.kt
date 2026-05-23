package dev.xero.tomabar.presentation.screens.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import dev.xero.tomabar.R

@Composable
fun TomaBarStreakCard(modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Row() {
            Icon(
                painter = painterResource(R.drawable.ic_flame),
                contentDescription = stringResource(R.string.streak_flame)
            )
            Column() {
                Text("Hi")
            }
        }
    }
}
