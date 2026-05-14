package com.jalsanchay.tracker.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.jalsanchay.tracker.ui.components.WaterHeroCard
import com.jalsanchay.tracker.ui.components.WaterScreenBackground
import com.jalsanchay.tracker.ui.theme.ErrorRed
import com.jalsanchay.tracker.ui.viewmodel.RainfallViewModel
import com.jalsanchay.tracker.utils.DateUtils
import com.jalsanchay.tracker.utils.WaterCalculator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController, viewModel: RainfallViewModel = viewModel()) {
    val entries by viewModel.entries.collectAsStateWithLifecycle()
    var deleteEntryId by remember { mutableStateOf<String?>(null) }

    Scaffold(containerColor = Color.Transparent) { padding ->
        WaterScreenBackground(modifier = Modifier.padding(padding)) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                WaterHeroCard(
                    title = "Rainfall history",
                    subtitle = "Review every collection entry and keep your harvesting record tidy."
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (entries.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "No entries yet", style = MaterialTheme.typography.titleLarge, color = Color.White)
                            Text(text = "Start logging rainfall to build your history", color = MaterialTheme.colorScheme.tertiary)
                            Spacer(modifier = Modifier.height(24.dp))
                            FilledTonalButton(onClick = { navController.navigate(Screen.LogRainfall.route) }) {
                                Text(text = "Log First Entry")
                            }
                        }
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(entries, key = { it.id }) { entry ->
                            val dismissState = rememberSwipeToDismissBoxState()

                            if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                                deleteEntryId = entry.id
                            }

                            SwipeToDismissBox(
                                state = dismissState,
                                backgroundContent = {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(ErrorRed.copy(alpha = 0.2f), MaterialTheme.shapes.medium)
                                            .padding(horizontal = 20.dp),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = null, tint = ErrorRed)
                                    }
                                }
                            ) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    shape = MaterialTheme.shapes.medium,
                                    border = BorderStroke(1.dp, Color(0xFF1E3A5F))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = DateUtils.formatDisplayDate(entry.date),
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = "${entry.rainfallMm} mm rainfall",
                                                color = MaterialTheme.colorScheme.tertiary,
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = WaterCalculator.formatLiters(entry.litersHarvested),
                                                color = Color(0xFF00BCD4),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 17.sp
                                            )
                                            Text(text = "harvested", color = MaterialTheme.colorScheme.tertiary, fontSize = 11.sp)
                                        }
                                        IconButton(onClick = { deleteEntryId = entry.id }) {
                                            Icon(Icons.Default.Delete, contentDescription = null, tint = ErrorRed)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (deleteEntryId != null) {
        val entry = entries.find { it.id == deleteEntryId }
        AlertDialog(
            onDismissRequest = { deleteEntryId = null },
            title = { Text("Delete Entry?") },
            text = { Text("Entry from ${entry?.date ?: ""} will be permanently deleted.") },
            confirmButton = {
                TextButton(onClick = {
                    deleteEntryId?.let { viewModel.deleteEntry(it) }
                    deleteEntryId = null
                }) {
                    Text("DELETE", color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteEntryId = null }) {
                    Text("CANCEL")
                }
            }
        )
    }
}
