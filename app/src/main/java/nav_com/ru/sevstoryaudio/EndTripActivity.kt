package nav_com.ru.sevstoryaudio

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar
import com.google.gson.Gson
import nav_com.ru.sevstoryaudio.connection.Get
import nav_com.ru.sevstoryaudio.models.SavedRoutesModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class EndTripActivity : AppCompatActivity() {
    private val sharedPrefs by lazy {  getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end_trip)

        val rating = findViewById<RatingBar>(R.id.ratingBar_endTrip)
        val buttonOk = findViewById<Button>(R.id.missOrScore)

        rating.setOnRatingBarChangeListener { _, _, _ ->
            buttonOk.text = resources.getString(R.string.btn_score)
        }

        buttonOk.setOnClickListener {
            val endRating = rating.rating.toInt()

            if (endRating > 0) {
                val token = getToken()
                val tripId = Gson().fromJson(getSavedRouts(), SavedRoutesModel::class.java).tripId
                val url = "https://sevstory.nav-com.ru/app/api?q=setScore&tripId=$tripId&token=$token&score=$endRating"

                val getResponse = Get()

                getResponse.run(
                    url,
                    object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            runOnUiThread {
                                saveRouts()
                                val mainActivity = Intent(this@EndTripActivity, MainActivity::class.java)
                                startActivity(mainActivity)
                                finish()
                            }
                        }

                        @Throws(IOException::class)
                        override fun onResponse(call: Call, response: Response) {
                            runOnUiThread {
                                saveRouts()
                                val mainActivity = Intent(this@EndTripActivity, MainActivity::class.java)
                                startActivity(mainActivity)
                                finish()
                            }
                        }
                    })
            } else {
                saveRouts()
                val mainActivity = Intent(this, MainActivity::class.java)
                startActivity(mainActivity)
                finish()
            }
        }
    }

    private fun getSavedRouts() = sharedPrefs.getString(KEY_ROUTS, "")

    private fun saveRouts() = sharedPrefs.edit().putString(KEY_ROUTS, "").apply()

    private fun getToken() = sharedPrefs.getString(TOKEN_KEY, "")
}