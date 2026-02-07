package com.fakelocation.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fakelocation.app.R

/**
 * Quick location selection with preset locations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickLocationSelector(
    onLocationSelected: (Double, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val presetLocations = listOf(
        "New York" to Pair(40.7128, -74.0060),
        "Los Angeles" to Pair(34.0522, -118.2437),
        "London" to Pair(51.5074, -0.1278),
        "Paris" to Pair(48.8566, 2.3522),
        "Tokyo" to Pair(35.6762, 139.6503),
        "Sydney" to Pair(-33.8688, 151.2093),
        "Beijing" to Pair(39.9042, 116.4074),
        "Shanghai" to Pair(31.2304, 121.4737),
        "Hong Kong" to Pair(22.3193, 114.1694),
        "Singapore" to Pair(1.3521, 103.8198)
    )

    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.quick_select),
                style = MaterialTheme.typography.titleMedium
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = stringResource(R.string.choose_city),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    presetLocations.forEach { (city, coordinates) ->
                        DropdownMenuItem(
                            text = { Text(city) },
                            onClick = {
                                onLocationSelected(coordinates.first, coordinates.second)
                                expanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Map,
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}
