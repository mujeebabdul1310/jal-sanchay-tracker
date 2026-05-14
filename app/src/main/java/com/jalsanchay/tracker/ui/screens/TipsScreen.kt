package com.jalsanchay.tracker.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jalsanchay.tracker.ui.components.WaterHeroCard
import com.jalsanchay.tracker.ui.components.WaterScreenBackground
import com.jalsanchay.tracker.ui.components.waterFieldColors

data class Tip(val title: String, val description: String)

@Composable
fun TipsScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }

    val allTips = listOf(
        Tip("Maximize Catchment Area", "Use the full roof surface where possible. Even modest rooftops can store meaningful seasonal water."),
        Tip("First Flush Diverter", "Discard the first dirty rainwater flow so the tank stays cleaner and easier to maintain."),
        Tip("Regular Maintenance", "Clean gutters, screens, and filters every few months to reduce clogging and contamination."),
        Tip("Runoff Coefficient", "Metal and tile roofs capture more efficiently than rough roofs. Tune the coefficient realistically."),
        Tip("Garden Priority", "Use harvested rainwater first for irrigation and toilet flushing where treatment is not required."),
        Tip("Monsoon Preparation", "Before heavy rains begin, inspect the roof path, tank lid, and overflow route."),
        Tip("Overflow Management", "Route overflow to another tank, soak pit, or garden zone so captured water is never wasted."),
        Tip("Household Target", "Estimate daily demand and decide what percentage you want the harvesting system to offset."),
        Tip("Groundwater Recharge", "When storage is full, redirect to recharge pits to support groundwater levels."),
        Tip("Annual Potential", "Calculate yearly yield using roof area, rainfall depth, and runoff coefficient to plan storage size."),
        Tip("Mosquito Control", "Keep tanks sealed and cover inlets and outlets with mesh to prevent breeding."),
        Tip("Community Harvesting", "Shared harvesting across neighboring roofs can reduce cost and increase overall resilience.")
    )

    val filteredTips = allTips.filter {
        it.title.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(containerColor = Color.Transparent) { padding ->
        WaterScreenBackground(modifier = Modifier.padding(padding)) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                WaterHeroCard(
                    title = "Harvesting tips",
                    subtitle = "Field-tested guidance for better collection, cleaner storage, and smarter reuse."
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search tips...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = waterFieldColors()
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(filteredTips) { tip ->
                        ExpandableTipCard(tip)
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandableTipCard(tip: Tip) {
    var expanded by remember { mutableStateOf(false) }
    val rotateAnimation by animateFloatAsState(targetValue = if (expanded) 180f else 0f, label = "tip_rotate")

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, Color(0xFF1E3A5F))
    ) {
        Column(modifier = Modifier.clickable { expanded = !expanded }.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = tip.title,
                    color = Color(0xFF00BCD4),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Icon(
                    Icons.Default.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier.rotate(rotateAnimation)
                )
            }
            AnimatedVisibility(visible = expanded) {
                Text(
                    text = tip.description,
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}
