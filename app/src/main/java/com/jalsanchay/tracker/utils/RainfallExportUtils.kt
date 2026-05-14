package com.jalsanchay.tracker.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.jalsanchay.tracker.data.model.RainfallEntry
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object RainfallExportUtils {
    enum class ExportFormat(
        val extension: String,
        val mimeType: String,
        val label: String
    ) {
        CSV("csv", "text/csv", "CSV"),
        PDF("pdf", "application/pdf", "PDF")
    }

    fun exportRecentRainfallData(
        context: Context,
        entries: List<RainfallEntry>,
        maxEntries: Int = 20,
        format: ExportFormat = ExportFormat.CSV
    ): String {
        val recentEntries = entries.take(maxEntries)
        require(recentEntries.isNotEmpty()) { "No rainfall entries available to export." }

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "jal_sanchay_recent_rainfall_$timestamp.${format.extension}"

        writeDownloadFile(context, fileName, format.mimeType) { outputStream ->
            when (format) {
                ExportFormat.CSV -> outputStream.bufferedWriter().use { it.write(buildCsv(recentEntries)) }
                ExportFormat.PDF -> writePdf(outputStream, recentEntries)
            }
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            "Download/$fileName"
        } else {
            File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                fileName
            ).absolutePath
        }
    }

    private fun writeDownloadFile(
        context: Context,
        fileName: String,
        mimeType: String,
        write: (OutputStream) -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, mimeType)
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                ?: error("Unable to create download file.")
            try {
                resolver.openOutputStream(uri)?.use(write)
                    ?: error("Unable to write download file.")
                values.clear()
                values.put(MediaStore.Downloads.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
            } catch (e: Exception) {
                resolver.delete(uri, null, null)
                throw e
            }
        } else {
            val rootDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                ?: error("Unable to access downloads directory.")
            val file = File(rootDir, fileName)
            FileOutputStream(file).use(write)
        }
    }

    private fun buildCsv(entries: List<RainfallEntry>): String = buildString {
        appendLine("Date,Rainfall (mm),Harvested (L),Roof Area (sq ft),Runoff Coefficient,Timestamp")
        entries.forEach { entry ->
            appendLine(
                listOf(
                    entry.date,
                    entry.rainfallMm.toString(),
                    entry.litersHarvested.toString(),
                    entry.roofAreaSqFt.toString(),
                    entry.runoffCoefficient.toString(),
                    entry.timestamp.toString()
                ).joinToString(",") { escapeCsv(it) }
            )
        }
    }

    private fun escapeCsv(value: String): String {
        return if (value.any { it == ',' || it == '"' || it == '\n' }) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }

    private fun writePdf(outputStream: OutputStream, entries: List<RainfallEntry>) {
        val document = PdfDocument()
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(13, 27, 42)
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(0, 121, 145)
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(20, 20, 20)
            textSize = 11f
        }

        var pageNumber = 1
        var page = document.startPage(PdfDocument.PageInfo.Builder(595, 842, pageNumber).create())
        var canvas = page.canvas
        var y = 48f

        fun drawHeader() {
            canvas.drawText("Jal-Sanchay Recent Rainfall Data", 40f, y, titlePaint)
            y += 34f
            canvas.drawText("Date", 40f, y, headerPaint)
            canvas.drawText("Rainfall", 175f, y, headerPaint)
            canvas.drawText("Harvested", 270f, y, headerPaint)
            canvas.drawText("Roof Area", 385f, y, headerPaint)
            canvas.drawText("Runoff", 495f, y, headerPaint)
            y += 18f
        }

        fun newPage() {
            document.finishPage(page)
            pageNumber += 1
            page = document.startPage(PdfDocument.PageInfo.Builder(595, 842, pageNumber).create())
            canvas = page.canvas
            y = 48f
            drawHeader()
        }

        drawHeader()
        entries.forEach { entry ->
            if (y > 800f) newPage()
            canvas.drawText(entry.date, 40f, y, bodyPaint)
            canvas.drawText("${entry.rainfallMm} mm", 175f, y, bodyPaint)
            canvas.drawText("${String.format(Locale.getDefault(), "%.1f", entry.litersHarvested)} L", 270f, y, bodyPaint)
            canvas.drawText("${entry.roofAreaSqFt} sq ft", 385f, y, bodyPaint)
            canvas.drawText(String.format(Locale.getDefault(), "%.2f", entry.runoffCoefficient), 495f, y, bodyPaint)
            y += 20f
        }
        document.finishPage(page)
        document.writeTo(outputStream)
        document.close()
    }
}
