package nav_com.ru.sevstoryaudio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.gson.Gson
import nav_com.ru.sevstoryaudio.adapters.AllCitiesAdapter
import nav_com.ru.sevstoryaudio.connection.Get
import nav_com.ru.sevstoryaudio.models.AllCitiesModel
import nav_com.ru.sevstoryaudio.models.ResponseAllCitiesModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class SelectCity : AppCompatActivity() {

    private val sharedPrefs by lazy {  getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_city)
        setScreen(0, 1, 0)

        val back = findViewById<Button>(R.id.backToAllTours_selectCity)
        val list = findViewById<ListView>(R.id.list_select_city)

        val intent = intent
        val backPage = intent.getStringExtra("return")

        back.setOnClickListener {
            val intentOpen = Intent(this@SelectCity, MainActivity::class.java)
            intentOpen.putExtra("return", backPage)
            startActivity(intentOpen)
            finish()
        }

        val cityId = getCityId()
        val url = "user/city/$cityId/all"
        val getResponse = Get()

        getResponse.run(
            url,
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        setError(1, resources.getString(R.string.err_no_internet))
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val stringResponse = response.body.string()
                    val gson = Gson()

                    val responseAllCities: ResponseAllCitiesModel =
                        gson.fromJson(stringResponse, ResponseAllCitiesModel::class.java)

                    if (responseAllCities.code == 200) {
                        val allCitiesList: List<AllCitiesModel> = responseAllCities.responseBody

                        val allCitiesAdapter = AllCitiesAdapter(
                            this@SelectCity,
                            R.layout.city_item,
                            allCitiesList
                        )

                        runOnUiThread {
                            list.adapter = allCitiesAdapter

                            list.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                                setCityId(allCitiesList[position].cityId)
                                setCityName(allCitiesList[position].cityName)

                                val intentOpen = Intent(this@SelectCity, MainActivity::class.java)
                                intentOpen.putExtra("return", backPage)
                                startActivity(intentOpen)
                                finish()
                            }

                            setScreen()
                        }
                    } else {
                        setError()
                    }
                }
            }
        )

    }

    override fun onBackPressed() {
        val intentOpen = Intent(this@SelectCity, MainActivity::class.java)
        intentOpen.putExtra("return", intent.getStringExtra("return"))
        startActivity(intentOpen)
        finish()
    }

    private fun setScreen(
        mainScreen: Int = 1,
        loadingScreen: Int = 0,
        errorScreen: Int = 0
    ) {
        val mainScreenActivity = findViewById<ConstraintLayout>(R.id.main_select_city)
        val loadingScreenActivity = findViewById<ConstraintLayout>(R.id.loading_select_city)
        val errorScreenActivity = findViewById<ConstraintLayout>(R.id.err_select_city)

        when (mainScreen) {
            0 -> mainScreenActivity.visibility = View.GONE
            1 ->mainScreenActivity.visibility = View.VISIBLE
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
        code: Int = 0,
        message: String = resources.getString(R.string.err_unknown)
    ) {
        val errorIcon = findViewById<ImageView>(R.id.errorImage_preview_trip)
        val errorText = findViewById<TextView>(R.id.errorText_preview_trip)

        errorText.text = message

        when (code) {
            0 -> errorIcon.setImageResource(R.drawable.err_unknown)
            1 -> errorIcon.setImageResource(R.drawable.err_check_internet)
        }

        setScreen(0, 0, 1)
    }

    private fun getCityId() = sharedPrefs.getInt(CITY_ID_KEY, 0)

    private fun setCityId(id: Int) = sharedPrefs.edit().putInt(CITY_ID_KEY, id).apply()

    private fun getCityName() = sharedPrefs.getString(CITY_NAME_KEY, "")

    private fun setCityName(name: String) = sharedPrefs.edit().putString(CITY_NAME_KEY, name).apply()
}