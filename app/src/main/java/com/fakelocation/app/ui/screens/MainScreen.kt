package com.fakelocation.app.ui.screens

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fakelocation.app.R
import com.fakelocation.app.domain.model.LocationPoint
import com.fakelocation.app.domain.usecase.SpoofingUiState
import com.fakelocation.app.ui.components.LocationInputForm
import com.fakelocation.app.ui.components.QuickLocationSelector
import com.fakelocation.app.ui.components.StatusIndicator

/**
 * Main screen for location spoofing functionality
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val spoofingState by viewModel.spoofingState.collectAsState()
    val hasPermissions by viewModel.hasPermissions
    val isMockEnabled by viewModel.isMockLocationEnabled

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        viewModel.updatePermissionState(permissions.values.all { it })
    }

    LaunchedEffect(Unit) {
        viewModel.checkPermissions()
        viewModel.checkMockLocationEnabled()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { openDeveloperOptions(context) }) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Permission status card
            if (!hasPermissions) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.permissions_required),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = stringResource(R.string.permissions_required_desc),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(
                            onClick = { permissionLauncher.launch(getRequiredPermissions()) }
                        ) {
                            Text(stringResource(R.string.grant_permissions))
                        }
                    }
                }
            }

            // Mock location status
            if (!isMockEnabled && hasPermissions) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.enable_mock_locations),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = stringResource(R.string.enable_mock_locations_desc),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        OutlinedButton(
                            onClick = { openDeveloperOptions(context) }
                        ) {
                            Text(stringResource(R.string.open_developer_options))
                        }
                    }
                }
            }

            // Status indicator
            StatusIndicator(
                state = when (val state = spoofingState) {
                    is SpoofingUiState.Inactive -> com.fakelocation.app.domain.model.SpoofingState.Inactive
                    is SpoofingUiState.Active -> com.fakelocation.app.domain.model.SpoofingState.Active(
                        state.location
                    )
                    is SpoofingUiState.Error -> com.fakelocation.app.domain.model.SpoofingState.Error(
                        state.message)
                }
            )

            // Location input form
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.enter_location),
                        style = MaterialTheme.typography.titleMedium
                    )

                    LocationInputForm(
                        onLocationSelected = { location ->
                            if (isMockEnabled) {
                                viewModel.startSpoofing(location)
                            }
                        }
                    )
                }
            }

            // Quick location selector
            QuickLocationSelector(
                onLocationSelected = { latitude, longitude ->
                    if (isMockEnabled) {
                        viewModel.startSpoofing(
                            LocationPoint(latitude, longitude)
                        )
                    }
                }
            )

            // Quick action buttons
            if (spoofingState is SpoofingUiState.Active) {
                Button(
                    onClick = { viewModel.stopSpoofing() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.stop_spoofing_btn))
                }
            }
        }
    }
}

private fun openDeveloperOptions(context: android.content.Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
        context.startActivity(intent)
    } else {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        context.startActivity(intent)
    }
}

private fun getRequiredPermissions(): Array<String> {
    return arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
}
