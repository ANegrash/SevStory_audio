package nav_com.ru.sevstoryaudio.models

data class TripPreviewModel(
    val tripName: String,
    val length: Int,
    val description: String,
    val image: String,
    val sightsArray: Array<String>,
    val score: Float,
    val viewed: Int,
    val isFavorite: Boolean,
    val isAudio: Boolean
)