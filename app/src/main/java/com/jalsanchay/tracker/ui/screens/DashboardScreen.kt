package com.jalsanchay.tracker.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.jalsanchay.tracker.navigation.Screen
import com.jalsanchay.tracker.ui.components.StatsCard
import com.jalsanchay.tracker.ui.components.WaterHeroCard
import com.jalsanchay.tracker.ui.components.WaterScreenBackground
import com.jalsanchay.tracker.ui.components.WaterTankCanvas
import com.jalsanchay.tracker.ui.theme.AccentCyan
import com.jalsanchay.tracker.ui.viewmodel.RainfallViewModel
import com.jalsanchay.tracker.utils.DateUtils
import com.jalsanchay.tracker.utils.WaterCalculator

@Composable
fun DashboardScreen(navController: NavController, viewModel: RainfallViewModel = viewModel()) {
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val totalLiters by viewModel.totalLiters.collectAsStateWithLifecycle()
    val todayLiters by viewModel.todayLiters.collectAsStateWithLifecycle()

    WaterScreenBackground {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                WaterHeroCard(
                    title = "Hello, ${profile?.name ?: "User"}",
                    subtitle = "Track rooftop capture and storage health for ${DateUtils.formatDisplayDate(DateUtils.getTodayString())}."
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(290.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFF1E3A5F))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        WaterTankCanvas(
                            totalLiters = totalLiters,
                            tankCapacity = profile?.tankCapacityLiters ?: 1000.0,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Stored ${WaterCalculator.formatLiters(totalLiters)}",
                                color = AccentCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Tank ${profile?.tankCapacityLiters?.toInt() ?: 1000}L",
                                color = MaterialTheme.colorScheme.tertiary,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatsCard(
                        icon = "TODAY",
                        label = "Harvested",
                        value = WaterCalculator.formatLiters(todayLiters),
                        accentColor = AccentCyan,
                        modifier = Modifier.weight(1f)
                    )
                    StatsCard(
                        icon = "TOTAL",
                        label = "Stored",
                        value = WaterCalculator.formatLiters(totalLiters),
                        accentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                val usage = profile?.householdDailyUsageLiters ?: 540.0
                val days = WaterCalculator.calculateHouseholdDays(totalLiters, usage)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2E4A)),
                    shape = MaterialTheme.shapes.large
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Coverage",
                            color = Color(0xFF00BCD4),
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Impact score",
                                color = MaterialTheme.colorScheme.tertiary,
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                text = "$days days",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp
                            )
                            Text(
                                text = "Estimated household water days covered",
                                color = MaterialTheme.colorScheme.tertiary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = { navController.navigate(Screen.LogRainfall.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = "LOG TODAY'S RAINFALL", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
