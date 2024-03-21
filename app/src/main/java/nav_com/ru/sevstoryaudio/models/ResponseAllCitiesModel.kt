package nav_com.ru.sevstoryaudio.models

data class ResponseAllCitiesModel(
    val responseBody: List<AllCitiesModel>,
    val code: Int,
    val message: String
)
