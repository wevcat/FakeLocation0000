package com.fakelocation.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fakelocation.app.R
import com.fakelocation.app.domain.model.LocationPoint
import java.text.DecimalFormat

/**
 * Input component for manual location entry
 */
@Composable
fun LocationInputForm(
    onLocationSelected: (LocationPoint) -> Unit,
    modifier: Modifier = Modifier
) {
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var accuracy by remember { mutableStateOf("10") }
    var latitudeError by remember { mutableStateOf(false) }
    var longitudeError by remember { mutableStateOf(false) }

    val decimalFormat = remember { DecimalFormat("#.######") }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = latitude,
            onValueChange = {
                latitude = it
                latitudeError = false
            },
            label = { Text(stringResource(R.string.latitude)) },
            placeholder = { Text("e.g., 37.7749") },
            isError = latitudeError,
            supportingText = if (latitudeError) {
                { Text("Invalid latitude (-90 to 90)") }
            } else null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = longitude,
            onValueChange = {
                longitude = it
                longitudeError = false
            },
            label = { Text(stringResource(R.string.longitude)) },
            placeholder = { Text("e.g., -122.4194") },
            isError = longitudeError,
            supportingText = if (longitudeError) {
                { Text("Invalid longitude (-180 to 180)") }
            } else null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = accuracy,
            onValueChange = { accuracy = it.filter { char -> char.isDigit() } },
            label = { Text(stringResource(R.string.accuracy)) },
            placeholder = { Text("e.g., 10") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val lat = latitude.toDoubleOrNull()
                val lon = longitude.toDoubleOrNull()
                val acc = accuracy.toFloatOrNull() ?: 10f

                latitudeError = lat == null || lat < -90 || lat > 90
                longitudeError = lon == null || lon < -180 || lon > 180

                if (!latitudeError && !longitudeError && lat != null && lon != null) {
                    onLocationSelected(
                        LocationPoint(
                            latitude = lat,
                            longitude = lon,
                            accuracy = acc
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = latitude.isNotEmpty() && longitude.isNotEmpty()
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.set_location))
        }
    }
}
