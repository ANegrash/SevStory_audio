package nav_com.ru.sevstoryaudio

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.gson.Gson
import nav_com.ru.sevstoryaudio.connection.Get
import nav_com.ru.sevstoryaudio.models.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

const val PREFS_NAME = "nav-com.sevstory"
const val KEY_ROUTS = "prefs.routs"

class StartTripActivity : AppCompatActivity() {

    private val sharedPrefs by lazy {  getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_trip)

        val loading = findViewById<ConstraintLayout>(R.id.loadingStarting)
        val done = findViewById<ConstraintLayout>(R.id.doneStarting)
        val infoText = findViewById<TextView>(R.id.infoTextStart)
        val monumentText = findViewById<TextView>(R.id.monumentInfoText)
        val toSight = findViewById<Button>(R.id.toFirstSight)
        val toMap = findViewById<Button>(R.id.toFirstMap)
        val resetRoute = findViewById<Button>(R.id.resetRoute)

        toMap.isEnabled = false
        toSight.isEnabled = false

        val jsonString = getSavedRouts()
        if (!jsonString.isNullOrEmpty()) {
            val oldSavedRoute = Gson().fromJson(jsonString, SavedRoutesModel::class.java)
            done.visibility = View.VISIBLE
            loading.visibility = View.GONE
            infoText.text = resources.getString(R.string.start_trip_first_point_heading)
            monumentText.text = oldSavedRoute.route[0].sightName

            toMap.isEnabled = true
            toSight.isEnabled = true

            toMap.setOnClickListener {
                val mapIntent = Intent(Intent.ACTION_VIEW)
                mapIntent.data =
                    Uri.parse(getGeoString(oldSavedRoute.route[0].longitude, oldSavedRoute.route[0].latitude, oldSavedRoute.route[0].sightName))
                startActivity(mapIntent)
            }

            toSight.setOnClickListener {
                val savedRouteNew = SavedRoutesModel(1, oldSavedRoute.tripId, oldSavedRoute.route)
                saveRouts(Gson().toJson(savedRouteNew))
                val sightIntent = Intent(this@StartTripActivity, SightActivity::class.java)
                startActivity(sightIntent)
                finish()
            }

            resetRoute.setOnClickListener {
                saveRouts("")
                val back = Intent(this@StartTripActivity, PreviewTripActivity::class.java)
                back.putExtra("tripId", oldSavedRoute.tripId)
                startActivity(back)
                finish()
            }
        } else {

            val tripId = intent.getStringExtra("tripId")

            if (tripId.isNullOrEmpty()) {
                //error
                resetRoute.setOnClickListener {
                    saveRouts("")
                    val back = Intent(this@StartTripActivity, MainActivity::class.java)
                    startActivity(back)
                    finish()
                }
            } else {
                done.visibility = View.GONE
                loading.visibility = View.VISIBLE
                infoText.text = resources.getString(R.string.start_trip_waiting_heading)

                val userToken = getToken()

                val url = "trips/$tripId/route?token=$userToken"

                val getResponse = Get()

                resetRoute.setOnClickListener {
                    saveRouts("")
                    val back = Intent(this@StartTripActivity, PreviewTripActivity::class.java)
                    back.putExtra("tripId", tripId)
                    startActivity(back)
                    finish()
                }

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

                            val responseRoute: ResponseRouteModel =
                                gson.fromJson(stringResponse, ResponseRouteModel::class.java)

                            if (responseRoute.code == 200) {
                                val route: List<RouteModel> = responseRoute.responseBody

                                val savedRoute = SavedRoutesModel(0, tripId, route)

                                saveRouts(gson.toJson(savedRoute))

                                runOnUiThread {
                                    done.visibility = View.VISIBLE
                                    loading.visibility = View.GONE
                                    infoText.text = resources.getString(R.string.start_trip_first_point_heading)

                                    monumentText.text = route[0].sightName

                                    toMap.isEnabled = true
                                    toSight.isEnabled = true

                                    toMap.setOnClickListener {
                                        val mapIntent = Intent(Intent.ACTION_VIEW)
                                        mapIntent.data =
                                            Uri.parse(
                                                getGeoString(
                                                    route[0].longitude,
                                                    route[0].latitude,
                                                    route[0].sightName
                                                )
                                            )
                                        startActivity(mapIntent)
                                    }

                                    toSight.setOnClickListener {
                                        val savedRouteNew = SavedRoutesModel(1, tripId,  route)
                                        saveRouts(gson.toJson(savedRouteNew))
                                        val sightIntent = Intent(
                                            this@StartTripActivity,
                                            SightActivity::class.java
                                        )
                                        startActivity(sightIntent)
                                        finish()
                                    }
                                }
                            }
                        }
                    })
            }
        }
    }

    private fun getGeoString(lon: Double, lat: Double, sight: String) = "geo:$lon,$lat?q=$lon,$lat($sight)"

    private fun getSavedRouts() = sharedPrefs.getString(KEY_ROUTS, "")

    private fun saveRouts (routes: String) = sharedPrefs.edit().putString(KEY_ROUTS, routes).apply()

    private fun getToken() = sharedPrefs.getString(TOKEN_KEY, "")
}