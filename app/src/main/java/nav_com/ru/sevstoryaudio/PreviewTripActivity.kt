package nav_com.ru.sevstoryaudio

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import nav_com.ru.sevstoryaudio.adapters.AllTripsAdapter
import nav_com.ru.sevstoryaudio.connection.Get
import nav_com.ru.sevstoryaudio.models.AllTripsModel
import nav_com.ru.sevstoryaudio.models.ResponseAllTripsModel
import nav_com.ru.sevstoryaudio.models.ResponseTripPreviewModel
import nav_com.ru.sevstoryaudio.models.TripPreviewModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.lang.Integer.parseInt

class PreviewTripActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_trip)

        val intent = intent
        val tripId = intent.getStringExtra("tripId")

        val back = findViewById<ImageButton>(R.id.backButton)
        back.setOnClickListener {
            val intentOpen = Intent(this@PreviewTripActivity, MainActivity::class.java)
            startActivity(intentOpen)
            finish()
        }

        val url = "https://sevstory.nav-com.ru/app/api?q=getTripPreview&tripId=$tripId"

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

                    val responseTripPreview: ResponseTripPreviewModel =
                        gson.fromJson(stringResponse, ResponseTripPreviewModel::class.java)

                    if (responseTripPreview.code == 200) {
                        val tripPreview: TripPreviewModel = responseTripPreview.responseBody

                        runOnUiThread {
                            val tripImage = findViewById<ImageView>(R.id.trip_preview_image)
                            val tripName = findViewById<TextView>(R.id.name_trip_preview)
                            val chipGroup = findViewById<ChipGroup>(R.id.chip_group)
                            val tripDescription = findViewById<TextView>(R.id.description_trip_preview)
                            val tripStart = findViewById<Button>(R.id.startButton)
                            val back = findViewById<ImageButton>(R.id.backButton)

                            val tripLength = findViewById<TextView>(R.id.time_preview)
                            val tripScore = findViewById<TextView>(R.id.rating_preview)
                            val tripViewed = findViewById<TextView>(R.id.countViewed)

                            val url = "https://sevstory.nav-com.ru/app/img/tour_preview/" + tripPreview.image

                            Picasso.get()
                                .load(url)
                                .placeholder(R.drawable.ic_launcher_background)
                                .error(R.drawable.ic_launcher_background)
                                .into(tripImage)

                            for (item in tripPreview.sightsArray) {
                                val chip = Chip(this@PreviewTripActivity)
                                chip.text = item
                                chip.chipIcon = getDrawable(R.drawable.ic_location)
                                chip.isChipIconVisible = false
                                chip.isClickable = false
                                chip.isCheckable = false
                                chip.setChipBackgroundColorResource(R.color.white)
                                chip.setChipStrokeColorResource(R.color.gray)
                                chip.chipStrokeWidth = 2.5F
                                chip.setTextColor(resources.getColor(R.color.gray))
                                chipGroup.addView(chip as View)
                            }

                            tripName.text = tripPreview.tripName
                            tripLength.text = getTrueMinutes(tripPreview.length)
                            tripScore.text = getTrueScore(tripPreview.score)
                            tripViewed.text = "" + tripPreview.viewed
                            tripDescription.text = tripPreview.description
                            tripStart.setOnClickListener {
                                val intentOpen = Intent(this@PreviewTripActivity, StartTripActivity::class.java)
                                intentOpen.putExtra("tripId", tripId)
                                startActivity(intentOpen)
                            }

                        }
                    }
                }
            })
    }

    override fun onBackPressed() {
        val intentOpen = Intent(this@PreviewTripActivity, MainActivity::class.java)
        startActivity(intentOpen)
        finish()
    }

    private fun getTrueMinutes (minutes: Int) : String {
        var result = ""
        val hours = minutes / 60
        if (hours != 0)
            result += "$hours ч "

        val leftMinutes = minutes % 60
        if (leftMinutes != 0)
            result += "$leftMinutes мин"

        return result
    }

    private fun getTrueScore (score: Float) : String {
        return score.toString()
    }
}