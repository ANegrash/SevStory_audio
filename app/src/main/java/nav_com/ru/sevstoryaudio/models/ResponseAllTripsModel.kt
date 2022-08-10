package nav_com.ru.sevstoryaudio.models

data class ResponseAllTripsModel(
    val responseBody: List<AllTripsModel>,
    val code: Int,
    val message: String
)
