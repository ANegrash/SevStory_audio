package nav_com.ru.sevstoryaudio.models

data class ResponseTripPreviewModel(
    val responseBody: TripPreviewModel,
    val code: Int,
    val message: String
)
