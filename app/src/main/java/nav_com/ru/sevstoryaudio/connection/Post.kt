package nav_com.ru.sevstoryaudio.connection

import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

val JSON: MediaType = "application/json".toMediaType()

class Post {
    fun run(
        url: String,
        json: String,
        callback: Callback
    ) {

        val body = json.toRequestBody(JSON)
        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        okHttpClient.newCall(request).enqueue(callback)
    }
}