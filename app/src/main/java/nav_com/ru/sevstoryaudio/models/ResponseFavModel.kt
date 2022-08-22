package nav_com.ru.sevstoryaudio.models

data class ResponseFavModel(
    val responseBody: List<FavModel>,
    val code: Int,
    val message: String
)
