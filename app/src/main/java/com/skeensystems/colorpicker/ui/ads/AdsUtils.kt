package com.skeensystems.colorpicker.ui.ads

import com.skeensystems.colorpicker.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.URL

suspend fun collectAdsData(outcome: String) {
    withContext(Dispatchers.IO) {
        val data = "entry.1054911767=${BuildConfig.VERSION_CODE}&entry.1945605800=$outcome&"

        val url = URL(BuildConfig.adsDataURL)
        val connection =
            url.openConnection().apply {
                connectTimeout = 5000
                readTimeout = 5000
                doOutput = true
            }

        OutputStreamWriter(connection.getOutputStream()).use { writer ->
            writer.write(data)
            writer.flush()
        }

        BufferedReader(InputStreamReader(connection.getInputStream())).use { reader ->
            reader.readLine()
        }
    }
}
