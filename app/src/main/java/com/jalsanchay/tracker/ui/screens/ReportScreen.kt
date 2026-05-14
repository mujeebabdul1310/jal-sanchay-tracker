package com.jalsanchay.tracker.ui.screens

import android.content.Context
import android.widget.Toast
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.jalsanchay.tracker.ui.components.WaterHeroCard
import com.jalsanchay.tracker.ui.components.WaterScreenBackground
import com.jalsanchay.tracker.ui.viewmodel.RainfallViewModel
import com.jalsanchay.tracker.utils.DateUtils
import com.jalsanchay.tracker.utils.RainfallExportUtils.ExportFormat
import com.jalsanchay.tracker.utils.RainfallExportUtils
import com.jalsanchay.tracker.utils.WaterCalculator
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext

@Composable
fun ReportScreen(navController: NavController, viewModel: RainfallViewModel = viewModel()) {
    val entries by viewModel.entries.collectAsStateWithLifecycle()
    val totalLiters by viewModel.totalLiters.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val monthlyTotals = remember(entries) {
        entries.groupBy { DateUtils.getMonthKey(it.date) }
            .mapValues { group -> group.value.sumOf { it.litersHarvested } }
            .toList()
            .take(6)
            .reversed()
    }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        WaterScreenBackground(modifier = Modifier.padding(padding)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    WaterHeroCard(
                        title = "Monthly report",
                        subtitle = "Visualize how much rooftop water you are harvesting month after month."
                    )
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Download recent rainfall data",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            FilledTonalButton(
                                onClick = {
                                    scope.launch {
                                        exportRecentRainfallData(
                                            context = context,
                                            entries = entries,
                                            format = ExportFormat.CSV,
                                            snackbarHostState = snackbarHostState
                                        )
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("DOWNLOAD CSV")
                            }
                            FilledTonalButton(
                                onClick = {
                                    scope.launch {
                                        exportRecentRainfallData(
                                            context = context,
                                            entries = entries,
                                            format = ExportFormat.PDF,
                                            snackbarHostState = snackbarHostState
                                        )
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("DOWNLOAD PDF")
                            }
                        }
                    }
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Total Water", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
                                Text(text = WaterCalculator.formatWholeLiters(totalLiters), style = MaterialTheme.typography.titleLarge, color = Color(0xFF42A5F5))
                                Text(text = "liters harvested", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
                            }
                        }
                        val thisMonth = DateUtils.getMonthKey(DateUtils.getTodayString())
                        val monthTotal = monthlyTotals.find { it.first == thisMonth }?.second ?: 0.0
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "This Month Water", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
                                Text(text = WaterCalculator.formatWholeLiters(monthTotal), style = MaterialTheme.typography.titleLarge, color = Color(0xFF00BCD4))
                                Text(text = "liters harvested", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
                            }
                        }
                    }
                }

                item {
                    if (monthlyTotals.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth().height(350.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Water Harvested (L)", style = MaterialTheme.typography.labelSmall)
                                Spacer(Modifier.height(8.dp))
                                AndroidView(
                                    factory = { context ->
                                        BarChart(context).apply {
                                            layoutParams = ViewGroup.LayoutParams(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.MATCH_PARENT
                                            )
                                            description.isEnabled = false
                                            legend.isEnabled = false
                                            setDrawGridBackground(false)
                                            setTouchEnabled(true)
                                            setScaleEnabled(false)
                                            setPinchZoom(false)
                                            setNoDataText("No rainfall data yet")
                                            setExtraOffsets(8f, 12f, 8f, 14f)
                                            xAxis.apply {
                                                position = XAxis.XAxisPosition.BOTTOM
                                                setDrawGridLines(false)
                                                textColor = android.graphics.Color.WHITE
                                                textSize = 10f
                                                granularity = 1f
                                                labelRotationAngle = -20f
                                            }
                                            axisLeft.apply {
                                                textColor = android.graphics.Color.WHITE
                                                textSize = 10f
                                                axisMinimum = 0f
                                                setDrawGridLines(true)
                                                gridColor = android.graphics.Color.argb(45, 255, 255, 255)
                                                valueFormatter = object : ValueFormatter() {
                                                    override fun getFormattedValue(value: Float): String {
                                                        return "${value.toInt()} L"
                                                    }
                                                }
                                            }
                                            axisRight.isEnabled = false
                                            animateY(1000)
                                        }
                                    },
                                    update = { chart ->
                                        val barEntries = monthlyTotals.mapIndexed { index, pair ->
                                            BarEntry(index.toFloat(), pair.second.toFloat())
                                        }
                                        val dataSet = BarDataSet(barEntries, "Monthly Harvest").apply {
                                            colors = listOf(
                                                android.graphics.Color.parseColor("#00BCD4"),
                                                android.graphics.Color.parseColor("#42A5F5"),
                                                android.graphics.Color.parseColor("#1565C0")
                                            )
                                            valueTextColor = android.graphics.Color.WHITE
                                            valueTextSize = 11f
                                            valueFormatter = object : ValueFormatter() {
                                                override fun getFormattedValue(value: Float): String {
                                                    return "${value.toInt()} L"
                                                }
                                            }
                                        }
                                        chart.xAxis.valueFormatter = IndexAxisValueFormatter(monthlyTotals.map { it.first })
                                        chart.data = BarData(dataSet).apply {
                                            barWidth = 0.55f
                                        }
                                        chart.setFitBars(true)
                                        chart.invalidate()
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            Text(text = "No data yet. Start logging!", color = MaterialTheme.colorScheme.tertiary)
                        }
                    }
                }
            }
        }
    }
}

private suspend fun exportRecentRainfallData(
    context: Context,
    entries: List<com.jalsanchay.tracker.data.model.RainfallEntry>,
    format: ExportFormat,
    snackbarHostState: SnackbarHostState
) {
    if (entries.isEmpty()) {
        Toast.makeText(context, "No rainfall data available to download", Toast.LENGTH_SHORT).show()
        snackbarHostState.showSnackbar("No rainfall data available to download.")
        return
    }

    runCatching {
        RainfallExportUtils.exportRecentRainfallData(context, entries, format = format)
    }.onSuccess { path ->
        Toast.makeText(context, "${format.label} downloaded", Toast.LENGTH_SHORT).show()
        snackbarHostState.showSnackbar("Downloaded ${format.label} to $path")
    }.onFailure { error ->
        val message = error.message ?: "Could not download rainfall data."
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        snackbarHostState.showSnackbar(message)
    }
}
