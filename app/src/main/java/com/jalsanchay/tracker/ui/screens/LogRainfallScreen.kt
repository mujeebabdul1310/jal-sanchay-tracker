package com.jalsanchay.tracker.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jalsanchay.tracker.data.Result
import com.jalsanchay.tracker.data.model.RainfallEntry
import com.jalsanchay.tracker.navigation.Screen
import com.jalsanchay.tracker.ui.components.LoadingButton
import com.jalsanchay.tracker.ui.components.WaterHeroCard
import com.jalsanchay.tracker.ui.components.WaterScreenBackground
import com.jalsanchay.tracker.ui.components.WaterSectionCard
import com.jalsanchay.tracker.ui.components.waterFieldColors
import com.jalsanchay.tracker.ui.viewmodel.RainfallViewModel
import com.jalsanchay.tracker.utils.DateUtils
import com.jalsanchay.tracker.utils.WaterCalculator
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogRainfallScreen(navController: NavController, viewModel: RainfallViewModel = viewModel()) {
    val context = LocalContext.current
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val addEntryState by viewModel.addEntryState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var roofArea by remember { mutableStateOf("") }
    var tankCapacity by remember { mutableStateOf("") }
    var dailyUsage by remember { mutableStateOf("") }
    var rainfallMm by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(DateUtils.getTodayString()) }
    var runoffCoefficient by remember { mutableStateOf(0.8f) }
    var expandedSetup by remember { mutableStateOf(false) }

    LaunchedEffect(profile) {
        profile?.let {
            roofArea = it.roofAreaSqFt.toString()
            tankCapacity = it.tankCapacityLiters.toString()
            dailyUsage = it.householdDailyUsageLiters.toString()
            runoffCoefficient = it.runoffCoefficient.toFloat()
        }
    }

    LaunchedEffect(addEntryState) {
        when (val result = addEntryState) {
            is Result.Success -> {
                Toast.makeText(context, "Rainfall entry saved", Toast.LENGTH_SHORT).show()
                viewModel.clearAddEntryState()
                navController.navigate(Screen.History.route) {
                    popUpTo(Screen.LogRainfall.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
            is Result.Error -> snackbarHostState.showSnackbar(result.message.ifBlank { "Unable to save rainfall entry" })
            else -> {}
        }
    }

    val estimated = WaterCalculator.calculateHarvestedLiters(
        roofArea.toDoubleOrNull() ?: 0.0,
        rainfallMm.toDoubleOrNull() ?: 0.0,
        runoffCoefficient.toDouble()
    )

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        WaterScreenBackground(modifier = Modifier.padding(padding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    WaterHeroCard(
                        title = "Log rainfall",
                        subtitle = "Capture today's rain event and instantly estimate how much water your roof could harvest."
                    )
                }

                item {
                    WaterSectionCard(modifier = Modifier.animateContentSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedSetup = !expandedSetup },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Harvest setup", color = Color.White, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.weight(1f))
                            Icon(
                                if (expandedSetup) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }

                        AnimatedVisibility(expandedSetup) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Spacer(Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = roofArea,
                                    onValueChange = { roofArea = it },
                                    label = { Text("Roof Area (sq ft)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    colors = waterFieldColors()
                                )
                                OutlinedTextField(
                                    value = tankCapacity,
                                    onValueChange = { tankCapacity = it },
                                    label = { Text("Tank Capacity (L)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    colors = waterFieldColors()
                                )
                                OutlinedTextField(
                                    value = dailyUsage,
                                    onValueChange = { dailyUsage = it },
                                    label = { Text("Daily Usage (L)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    colors = waterFieldColors()
                                )
                                Text(
                                    text = "These values sync with your profile settings.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    }
                }

                item {
                    WaterSectionCard {
                        Text(text = "Rain event", color = Color.White, fontWeight = FontWeight.Bold)

                        OutlinedButton(
                            onClick = {
                                val calendar = Calendar.getInstance()
                                DatePickerDialog(context, { _, y, m, d ->
                                    selectedDate = String.format("%04d-%02d-%02d", y, m + 1, d)
                                }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(text = DateUtils.formatDisplayDate(selectedDate))
                        }

                        OutlinedTextField(
                            value = rainfallMm,
                            onValueChange = { rainfallMm = it },
                            placeholder = { Text("0.0") },
                            suffix = { Text("mm") },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = waterFieldColors()
                        )

                        Text(text = "Runoff Coefficient", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Slider(
                                value = runoffCoefficient,
                                onValueChange = { runoffCoefficient = it },
                                valueRange = 0.3f..1.0f,
                                modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF00BCD4),
                                    activeTrackColor = Color(0xFF00BCD4)
                                )
                            )
                            Spacer(Modifier.width(8.dp))
                            Surface(
                                color = Color(0xFF1E3A5F),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = String.format("%.1f", runoffCoefficient),
                                    color = Color(0xFF00BCD4),
                                    modifier = Modifier.padding(8.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Tile 0.9" to 0.9f, "Asphalt 0.8" to 0.8f, "Gravel 0.6" to 0.6f).forEach { (label, value) ->
                                OutlinedButton(
                                    onClick = { runoffCoefficient = value },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(0.dp),
                                    colors = if (runoffCoefficient == value) {
                                        ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    } else {
                                        ButtonDefaults.outlinedButtonColors()
                                    }
                                ) {
                                    Text(text = label, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }

                item {
                    AnimatedVisibility(
                        visible = (rainfallMm.toDoubleOrNull() ?: 0.0) > 0,
                        enter = slideInVertically() + fadeIn(),
                        exit = slideOutVertically() + fadeOut()
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2E4A)),
                            shape = MaterialTheme.shapes.large
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Live Preview", color = Color(0xFF00BCD4), style = MaterialTheme.typography.labelSmall)
                                Text(text = "Estimated Harvest", color = MaterialTheme.colorScheme.tertiary)
                                Text(
                                    text = WaterCalculator.formatLiters(estimated),
                                    style = MaterialTheme.typography.displayLarge,
                                    color = Color.White
                                )
                                Text(
                                    text = "$roofArea x ${rainfallMm}mm x 0.0929 x ${String.format("%.1f", runoffCoefficient)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    }
                }

                item {
                    LoadingButton(
                        text = "SAVE ENTRY",
                        isLoading = addEntryState is Result.Loading,
                        onClick = {
                            val rain = rainfallMm.toDoubleOrNull() ?: 0.0
                            val area = roofArea.toDoubleOrNull() ?: 0.0

                            if (rain <= 0) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Enter rainfall greater than 0 mm")
                                }
                                return@LoadingButton
                            }
                            if (area <= 0) {
                                expandedSetup = true
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Enter roof area before saving")
                                }
                                return@LoadingButton
                            }

                            val harvested = WaterCalculator.calculateHarvestedLiters(area, rain, runoffCoefficient.toDouble())
                            val entry = RainfallEntry(
                                date = selectedDate,
                                rainfallMm = rain,
                                litersHarvested = harvested,
                                roofAreaSqFt = area,
                                runoffCoefficient = runoffCoefficient.toDouble(),
                                timestamp = System.currentTimeMillis()
                            )

                            viewModel.addEntry(entry)

                            val profileUpdates = mutableMapOf<String, Any>()
                            if (area != profile?.roofAreaSqFt) profileUpdates["roofAreaSqFt"] = area
                            if ((tankCapacity.toDoubleOrNull() ?: 0.0) != profile?.tankCapacityLiters) {
                                profileUpdates["tankCapacityLiters"] = tankCapacity.toDoubleOrNull() ?: 1000.0
                            }
                            if ((dailyUsage.toDoubleOrNull() ?: 0.0) != profile?.householdDailyUsageLiters) {
                                profileUpdates["householdDailyUsageLiters"] = dailyUsage.toDoubleOrNull() ?: 540.0
                            }
                            if (profileUpdates.isNotEmpty()) viewModel.updateProfile(profileUpdates)

                            rainfallMm = ""
                            selectedDate = DateUtils.getTodayString()
                        }
                    )
                }
            }
        }
    }
}
