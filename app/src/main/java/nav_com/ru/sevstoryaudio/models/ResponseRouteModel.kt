package nav_com.ru.sevstoryaudio.models

data class ResponseRouteModel(
    val responseBody: List<RouteModel>,
    val code: Int,
    val message: String
)
