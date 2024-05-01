package nav_com.ru.sevstoryaudio.models

data class ResponseMasterSightsModel(
    val responseBody: List<MasterSightsModel>,
    val code: Int,
    val message: String
)
