package com.jalsanchay.tracker.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.jalsanchay.tracker.navigation.Screen
import com.jalsanchay.tracker.ui.components.LoadingButton
import com.jalsanchay.tracker.ui.components.WaterHeroCard
import com.jalsanchay.tracker.ui.components.WaterMetricChip
import com.jalsanchay.tracker.ui.components.WaterScreenBackground
import com.jalsanchay.tracker.ui.components.WaterSectionCard
import com.jalsanchay.tracker.ui.components.waterFieldColors
import com.jalsanchay.tracker.ui.viewmodel.AuthViewModel
import com.jalsanchay.tracker.ui.viewmodel.RainfallViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    rainfallViewModel: RainfallViewModel = viewModel()
) {
    val profile by rainfallViewModel.userProfile.collectAsStateWithLifecycle()
    val profileUpdateState by rainfallViewModel.profileUpdateState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var roofArea by remember { mutableStateOf("") }
    var tankCapacity by remember { mutableStateOf("") }
    var dailyUsage by remember { mutableStateOf("") }

    LaunchedEffect(profile) {
        profile?.let {
            roofArea = it.roofAreaSqFt.toString()
            tankCapacity = it.tankCapacityLiters.toString()
            dailyUsage = it.householdDailyUsageLiters.toString()
        }
    }

    LaunchedEffect(profileUpdateState) {
        when (val result = profileUpdateState) {
            is Result.Success -> {
                Toast.makeText(context, "Water profile saved", Toast.LENGTH_SHORT).show()
                rainfallViewModel.clearProfileUpdateState()
                navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.Settings.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
            is Result.Error -> snackbarHostState.showSnackbar(result.message.ifBlank { "Unable to save water profile" })
            else -> {}
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0D1B2A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        WaterScreenBackground(modifier = Modifier.padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                WaterHeroCard(
                    title = "Rainwater setup",
                    subtitle = "Shape the numbers behind your rooftop collection and household storage."
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate(Screen.Profile.route) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = MaterialTheme.shapes.large
                ) {
                    Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                        val initials = profile?.name
                            ?.split(" ")
                            ?.mapNotNull { it.firstOrNull()?.uppercaseChar() }
                            ?.take(2)
                            ?.joinToString("")
                            ?: "US"
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = profile?.name ?: "User",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Text(
                                text = profile?.email ?: "email@example.com",
                                color = MaterialTheme.colorScheme.tertiary,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Open profile", color = Color(0xFF00BCD4), fontSize = 13.sp)
                        }
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = Color(0xFF00BCD4)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    WaterMetricChip("Roof", "${roofArea.ifBlank { "0" }} sq ft")
                    WaterMetricChip("Tank", "${tankCapacity.ifBlank { "0" }} L")
                }

                WaterSectionCard {
                    Text(
                        text = "Harvest setup",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Use realistic values so the app feels closer to an actual water harvesting control panel.",
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    SettingFieldLabel(
                        icon = Icons.Default.Home,
                        title = "Roof catchment",
                        subtitle = "Collection area available for rain capture"
                    )
                    OutlinedTextField(
                        value = roofArea,
                        onValueChange = { roofArea = it },
                        label = { Text("Roof Area (sq ft)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = waterFieldColors()
                    )

                    SettingFieldLabel(
                        icon = Icons.Default.WaterDrop,
                        title = "Tank storage",
                        subtitle = "Stored water and typical daily demand"
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
                        label = { Text("Daily Household Usage (L)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = waterFieldColors()
                    )

                    LoadingButton(
                        text = "SAVE WATER PROFILE",
                        isLoading = profileUpdateState is Result.Loading,
                        onClick = {
                            val updates = mapOf(
                                "roofAreaSqFt" to (roofArea.toDoubleOrNull() ?: 0.0),
                                "tankCapacityLiters" to (tankCapacity.toDoubleOrNull() ?: 1000.0),
                                "householdDailyUsageLiters" to (dailyUsage.toDoubleOrNull() ?: 540.0)
                            )
                            rainfallViewModel.updateProfile(updates)
                        }
                    )
                }

                Button(
                    onClick = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF5350)),
                    shape = MaterialTheme.shapes.large
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, tint = Color(0xFFEF5350))
                    Spacer(Modifier.width(8.dp))
                    Text(text = "LOGOUT", color = Color(0xFFEF5350), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun SettingFieldLabel(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0x1600BCD4), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF00BCD4))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = title, color = Color.White, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, color = MaterialTheme.colorScheme.tertiary, style = MaterialTheme.typography.labelSmall)
        }
    }
}
