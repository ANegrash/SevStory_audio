package nav_com.ru.sevstoryaudio

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import nav_com.ru.sevstoryaudio.connection.Get
import nav_com.ru.sevstoryaudio.models.ResponseSightInfo
import nav_com.ru.sevstoryaudio.models.RouteModel
import nav_com.ru.sevstoryaudio.models.SavedRoutesModel
import nav_com.ru.sevstoryaudio.models.SightInfo
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class SightActivity : AppCompatActivity() {

    private val sharedPrefs by lazy {  getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sight)

        val toNextMap = findViewById<Button>(R.id.toNextMap)
        val toNextSight = findViewById<Button>(R.id.toNextSight)
        val sightName = findViewById<TextView>(R.id.sightName)
        val sightDescription = findViewById<TextView>(R.id.descriptionText)
        val sightImage = findViewById<ImageView>(R.id.sightImage)

        sightDescription.text = "Получение данных"

        val jsonString = getSavedRouts()
        if (!jsonString.isNullOrEmpty()) {
            val oldSavedRoute = Gson().fromJson(jsonString, SavedRoutesModel::class.java)
            if (oldSavedRoute.current > 0) {
                val route : List<RouteModel> = oldSavedRoute.route
                val currentIndex : Int = oldSavedRoute.current - 1
                sightName.text = route[currentIndex].sightName

                if (route.lastIndex == currentIndex) {
                    toNextMap.text = "Завершить экскурсию"
                    toNextSight.visibility = View.GONE
                    saveRouts("")

                    toNextMap.setOnClickListener {
                        val end_trip = Intent(this@SightActivity, MainActivity::class.java)
                        startActivity(end_trip)
                        finish()
                    }
                } else {
                    oldSavedRoute.current = oldSavedRoute.current + 1

                    toNextMap.setOnClickListener {
                        val map_intent = Intent(Intent.ACTION_VIEW)
                        map_intent.data =
                            Uri.parse(getGeoString(route[currentIndex + 1].longitude, route[currentIndex + 1].latitude, route[currentIndex + 1].sightName))
                        startActivity(map_intent)
                    }

                    toNextSight.setOnClickListener {
                        saveRouts(Gson().toJson(oldSavedRoute))
                        val next_intent = Intent(this@SightActivity, SightActivity::class.java)
                        startActivity(next_intent)
                        finish()
                    }
                }

                val url = "https://sevstory.nav-com.ru/app/api?q=getSightInfo&sightId=" + route[currentIndex].sightId

                val getResponse = Get()

                getResponse.run(
                    url,
                    object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            runOnUiThread {

                            }
                        }

                        @Throws(IOException::class)
                        override fun onResponse(call: Call, response: Response) {
                            val stringResponse = response.body.string()
                            val gson = Gson()

                            val responseSightInfo: ResponseSightInfo =
                                gson.fromJson(stringResponse, ResponseSightInfo::class.java)

                            if (responseSightInfo.code == 200) {
                                val sightInfo : SightInfo = responseSightInfo.responseBody
                                runOnUiThread {
                                    val imgUrl = "https://sevstory.nav-com.ru/app/img/sight_image/" + sightInfo.image
                                    Picasso.get()
                                        .load(imgUrl)
                                        .placeholder(R.drawable.ic_launcher_background)
                                        .error(R.drawable.ic_launcher_background)
                                        .into(sightImage)

                                    sightDescription.text = sightInfo.description
                                }
                            }
                        }
                    })

            } else {
                val first_intent = Intent(this@SightActivity, PreviewTripActivity::class.java)
                startActivity(first_intent)
            }
        }

    }

    private fun getGeoString(lon: Double, lat: Double, sight: String) = "geo:$lon,$lat?q=$lon,$lat($sight)"

    private fun getSavedRouts() = sharedPrefs.getString(KEY_ROUTS, "")

    private fun saveRouts (routes: String) = sharedPrefs.edit().putString(KEY_ROUTS, routes).apply()
}