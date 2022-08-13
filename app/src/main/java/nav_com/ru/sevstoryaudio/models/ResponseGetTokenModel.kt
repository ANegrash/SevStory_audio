package nav_com.ru.sevstoryaudio.models

data class ResponseGetTokenModel(
    val responseBody: String,
    val code: Int,
    val message: String
)
