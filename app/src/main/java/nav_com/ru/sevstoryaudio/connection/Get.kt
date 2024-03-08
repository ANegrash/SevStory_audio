package nav_com.ru.sevstoryaudio.connection

import android.util.Log
import nav_com.ru.sevstoryaudio.BASE_URL
import okhttp3.*

class Get {

    fun run(
        url: String,
        callback: Callback
    ) {
        val finalURL = "${BASE_URL}api/$url"
        Log.e("FATALITY", finalURL)

        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .url(finalURL)
            .build()
        okHttpClient.newCall(request).enqueue(callback)
    }
}