package com.fakelocation.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fakelocation.app.R
import com.fakelocation.app.domain.model.SpoofingState

/**
 * Status indicator showing current spoofing state
 */
@Composable
fun StatusIndicator(
    state: SpoofingState,
    modifier: Modifier = Modifier
) {
    val (icon, color, text) = when (state) {
        is SpoofingState.Inactive -> Triple(
            Icons.Default.Close,
            Color.Gray,
            stringResource(R.string.inactive)
        )
        is SpoofingState.Active -> Triple(
            Icons.Default.Check,
            Color.Green,
            "${stringResource(R.string.active)} - ${state.location}"
        )
        is SpoofingState.Error -> Triple(
            Icons.Default.Warning,
            MaterialTheme.colorScheme.error,
            "${stringResource(R.string.error)}: ${state.message}"
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = if (state is SpoofingState.Error) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}
