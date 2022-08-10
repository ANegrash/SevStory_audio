package nav_com.ru.sevstoryaudio

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import com.google.gson.Gson
import nav_com.ru.sevstoryaudio.adapters.AllTripsAdapter
import nav_com.ru.sevstoryaudio.connection.Get
import nav_com.ru.sevstoryaudio.models.AllTripsModel
import nav_com.ru.sevstoryaudio.models.ResponseAllTripsModel
import nav_com.ru.sevstoryaudio.models.SavedRoutesModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val sharedPrefs by lazy {  getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#ffffff")

        val jsonString = getSavedRouts()
        if (!jsonString.isNullOrEmpty()) {
            val oldSavedRoute = Gson().fromJson(jsonString, SavedRoutesModel::class.java)
            if (oldSavedRoute.current == 0) {
                val intent = Intent(this@MainActivity, StartTripActivity::class.java)
                startActivity(intent)
                finish()
            } else if (oldSavedRoute.current > 0) {
                val intent = Intent(this@MainActivity, SightActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        loadList()
    }

    private fun loadList() {
        val listView = findViewById<ListView>(R.id.list_view)

        val url = "https://sevstory.nav-com.ru/app/api?q=getAllTrips"

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

                    val responseAllTripsModel: ResponseAllTripsModel =
                        gson.fromJson(stringResponse, ResponseAllTripsModel::class.java)

                    if (responseAllTripsModel.code == 200) {
                        val tripsList: List<AllTripsModel> =
                            responseAllTripsModel.responseBody

                        val allTripsAdapter = AllTripsAdapter(
                            this@MainActivity,
                            R.layout.trip_item,
                            tripsList
                        )

                        runOnUiThread {
                            listView.adapter = allTripsAdapter
                            listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                                val intent = Intent(this@MainActivity, PreviewTripActivity::class.java)
                                intent.putExtra("tripId", tripsList[position].tripId.toString())
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                }
            })

    }

    private fun getSavedRouts() = sharedPrefs.getString(KEY_ROUTS, "")
}