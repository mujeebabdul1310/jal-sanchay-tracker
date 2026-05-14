package com.jalsanchay.tracker.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jalsanchay.tracker.ui.components.WaterHeroCard
import com.jalsanchay.tracker.ui.components.WaterInfoRow
import com.jalsanchay.tracker.ui.components.WaterScreenBackground
import com.jalsanchay.tracker.ui.components.WaterSectionCard
import com.jalsanchay.tracker.ui.viewmodel.RainfallViewModel
import com.jalsanchay.tracker.utils.DateUtils
import com.jalsanchay.tracker.utils.WaterCalculator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    rainfallViewModel: RainfallViewModel = viewModel()
) {
    val profile by rainfallViewModel.userProfile.collectAsStateWithLifecycle()
    val entries by rainfallViewModel.entries.collectAsStateWithLifecycle()
    val initials = profile?.name
        ?.split(" ")
        ?.mapNotNull { it.firstOrNull()?.uppercaseChar() }
        ?.take(2)
        ?.joinToString("")
        ?.ifBlank { null }
        ?: "US"

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
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
                    title = profile?.name ?: "User profile",
                    subtitle = "Your identity and water setup summary in one place."
                )

                WaterSectionCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = profile?.name ?: "User",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                            Text(
                                text = profile?.email ?: "email@example.com",
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                    WaterInfoRow(
                        icon = Icons.Default.Person,
                        label = "Full Name",
                        value = profile?.name ?: "Not available"
                    )
                    WaterInfoRow(
                        icon = Icons.Default.Email,
                        label = "Email",
                        value = profile?.email ?: "Not available"
                    )
                }

                WaterSectionCard {
                    Text(
                        text = "Water setup",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    WaterInfoRow(
                        icon = Icons.Default.WaterDrop,
                        label = "Roof Area",
                        value = "${profile?.roofAreaSqFt ?: 0.0} sq ft"
                    )
                    WaterInfoRow(
                        icon = Icons.Default.WaterDrop,
                        label = "Tank Capacity",
                        value = "${profile?.tankCapacityLiters ?: 1000.0} L"
                    )
                    WaterInfoRow(
                        icon = Icons.Default.WaterDrop,
                        label = "Daily Usage",
                        value = "${profile?.householdDailyUsageLiters ?: 540.0} L"
                    )
                }

                WaterSectionCard {
                    Text(
                        text = "Recent rainfall entries",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    if (entries.isEmpty()) {
                        Text(
                            text = "No rainfall entries saved yet.",
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    } else {
                        entries.take(3).forEach { entry ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = DateUtils.formatDisplayDate(entry.date),
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${entry.rainfallMm} mm rainfall",
                                        color = MaterialTheme.colorScheme.tertiary,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                                Text(
                                    text = WaterCalculator.formatLiters(entry.litersHarvested),
                                    color = Color(0xFF00BCD4),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
