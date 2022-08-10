package nav_com.ru.sevstoryaudio.models

data class SavedRoutesModel(
    var current: Int,
    val tripId: String,
    val route: List<RouteModel>
)
