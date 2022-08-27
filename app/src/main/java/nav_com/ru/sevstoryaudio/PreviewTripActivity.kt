package nav_com.ru.sevstoryaudio

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import nav_com.ru.sevstoryaudio.connection.Get
import nav_com.ru.sevstoryaudio.models.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class PreviewTripActivity : AppCompatActivity() {

    private val sharedPrefs by lazy {  getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_trip)

        setScreen(0, 1, 0)
        val intent = intent
        val tripId = intent.getStringExtra("tripId")
        val backPage = intent.getStringExtra("return")

        val back = findViewById<Button>(R.id.backButton)
        val favorite = findViewById<ImageButton>(R.id.favoriteBtn)
        back.setOnClickListener {
            val intentOpen : Intent = if (backPage == "favorite")
                Intent(this@PreviewTripActivity, FavoriteList::class.java)
            else
                Intent(this@PreviewTripActivity, MainActivity::class.java)
            startActivity(intentOpen)
            finish()
        }

        favorite.setOnClickListener {
            if (!isFavorite) {
                val token = getToken()
                val url = "https://sevstory.nav-com.ru/app/api?q=setFavorite&tripId=$tripId&token=$token"
                val getResponse = Get()

                getResponse.run(
                    url,
                    object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            runOnUiThread {
                                Toast.makeText(
                                    this@PreviewTripActivity,
                                    "Ошибка добавления в избранное",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        @Throws(IOException::class)
                        override fun onResponse(call: Call, response: Response) {
                            val stringResponse = response.body.string()
                            val gson = Gson()

                            val answer: AnswerModel = gson.fromJson(stringResponse, AnswerModel::class.java)
                            if (answer.responseBody) {
                                runOnUiThread {
                                    favorite.setImageResource(R.drawable.ic_favorite_fill)
                                }
                                isFavorite = true
                            } else {
                                runOnUiThread {
                                    Toast.makeText(
                                        this@PreviewTripActivity,
                                        "Ошибка добавления в избранное",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    })
            } else {
                val token = getToken()
                val url = "https://sevstory.nav-com.ru/app/api?q=setNotFavorite&tripId=$tripId&token=$token"
                val getResponse = Get()

                getResponse.run(
                    url,
                    object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            runOnUiThread {
                                Toast.makeText(this@PreviewTripActivity, "Ошибка удаления из избранного", Toast.LENGTH_SHORT).show()
                            }
                        }

                        @Throws(IOException::class)
                        override fun onResponse(call: Call, response: Response) {
                            val stringResponse = response.body.string()
                            val gson = Gson()

                            val answer: AnswerModel = gson.fromJson(stringResponse, AnswerModel::class.java)
                            if (answer.responseBody) {
                                runOnUiThread {
                                    favorite.setImageResource(R.drawable.ic_favorite_border)
                                }
                                isFavorite = false
                            } else {
                                runOnUiThread {
                                    Toast.makeText(
                                        this@PreviewTripActivity,
                                        "Ошибка удаления из избранного",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    })
            }
        }

        val url = "https://sevstory.nav-com.ru/app/api?q=getTripPreview&tripId=$tripId&token=" + getToken()
        val getResponse = Get()

        getResponse.run(
            url,
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        setError()
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

                            if (tripPreview.isFavorite) {
                                favorite.setImageResource(R.drawable.ic_favorite_fill)
                                isFavorite = true
                            }

                            val tripLength = findViewById<TextView>(R.id.time_preview)
                            val tripScore = findViewById<TextView>(R.id.rating_preview)
                            val tripViewed = findViewById<TextView>(R.id.countViewed)

                            val urlImage = "https://sevstory.nav-com.ru/app/img/tour_preview/" + tripPreview.image

                            Picasso.get()
                                .load(urlImage)
                                .into(tripImage)

                            for (item in tripPreview.sightsArray) {
                                val chip = Chip(this@PreviewTripActivity)
                                chip.text = item
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
                            tripViewed.text = tripPreview.viewed.toString()
                            tripDescription.text = tripPreview.description
                            tripStart.setOnClickListener {
                                val intentOpen = Intent(this@PreviewTripActivity, StartTripActivity::class.java)
                                intentOpen.putExtra("tripId", tripId)
                                startActivity(intentOpen)
                            }

                            setScreen()
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

    private fun getToken() = sharedPrefs.getString(TOKEN_KEY, "")

    private fun setScreen(
        mainScreen: Int = 1,
        loadingScreen: Int = 0,
        errorScreen: Int = 0
    ) {
        val mainScreenActivity = findViewById<ConstraintLayout>(R.id.mainScreen_preview_trip)
        val loadingScreenActivity = findViewById<ConstraintLayout>(R.id.loadingScreen_preview_trip)
        val errorScreenActivity = findViewById<ConstraintLayout>(R.id.errorScreen_preview_trip)
        val favBtn = findViewById<ImageButton>(R.id.favoriteBtn)

        when (mainScreen) {
            0 -> {
                favBtn.visibility = View.GONE
                mainScreenActivity.visibility = View.GONE
            }
            1 -> {
                favBtn.visibility = View.VISIBLE
                mainScreenActivity.visibility = View.VISIBLE
            }
        }

        when (loadingScreen) {
            0 -> loadingScreenActivity.visibility = View.GONE
            1 -> loadingScreenActivity.visibility = View.VISIBLE
        }

        when (errorScreen) {
            0 -> errorScreenActivity.visibility = View.GONE
            1 -> errorScreenActivity.visibility = View.VISIBLE
        }
    }

    private fun setError(
        //code: Int = 0,
        //message: String = "Произошла неизвестная ошибка, попробуйте позже"
    ) {
        setScreen(0, 0, 1)
    }
}