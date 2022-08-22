package nav_com.ru.sevstoryaudio

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.gson.Gson
import nav_com.ru.sevstoryaudio.adapters.AllTripsAdapter
import nav_com.ru.sevstoryaudio.adapters.FavTripsAdapter
import nav_com.ru.sevstoryaudio.connection.Get
import nav_com.ru.sevstoryaudio.models.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import kotlin.math.log

class FavoriteList : AppCompatActivity() {
    private val sharedPrefs by lazy {  getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_list)
        val toProfile = findViewById<Button>(R.id.backToProfile)

        toProfile.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("return","profile")
            startActivity(intent)
            finish()
        }

        loadFavList()
    }

    fun loadFavList () {
        val url = "https://sevstory.nav-com.ru/app/api?q=getFavoriteList&token=" + getToken()
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

                    val favoriteListModel: ResponseFavModel =
                        gson.fromJson(stringResponse, ResponseFavModel::class.java)

                    val favoriteList: List<FavModel> =
                        favoriteListModel.responseBody

                    if (favoriteList.isEmpty()) {
                        runOnUiThread {
                            setError()
                        }
                    } else {
                        val favTripsAdapter = FavTripsAdapter(
                            this@FavoriteList,
                            R.layout.fav_item,
                            favoriteList
                        )

                        runOnUiThread {
                            val favListWidget = findViewById<ListView>(R.id.fav_list)
                            favListWidget.adapter = favTripsAdapter

                            favListWidget.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                                    val intent =
                                        Intent(this@FavoriteList, PreviewTripActivity::class.java)
                                    intent.putExtra(
                                        "tripId",
                                        favoriteList[position].tripId.toString()
                                    )
                                    intent.putExtra("return","favorite")
                                    startActivity(intent)
                                    finish()
                                }
                            setScreen()
                        }
                    }
                }
            })
    }

    private fun setScreen(
        main: Int = 1,
        loading: Int = 0,
        error: Int = 0
    ) {
        val mainScreen = findViewById<ConstraintLayout>(R.id.fav_list_container)
        //val loadingScreen = findViewById<ConstraintLayout>(R.id.fav_list_container)
        val errorScreen = findViewById<ConstraintLayout>(R.id.emptyError)

        when (main) {
            0 -> mainScreen.visibility = View.GONE
            1 -> mainScreen.visibility = View.VISIBLE
        }

//        when (loading) {
//            0 -> loadingScreen.visibility = View.GONE
//            1 -> loadingScreen.visibility = View.VISIBLE
//        }

        when (error) {
            0 -> errorScreen.visibility = View.GONE
            1 -> errorScreen.visibility = View.VISIBLE
        }
    }

    private fun setError (
        code: Int = 0,
        message: String = ""
    ) {
        val errorIcon = findViewById<ImageView>(R.id.errorImage_fav)
        val errorText = findViewById<TextView>(R.id.errorText_fav)

        setScreen(0, 0, 1)
    }

    private fun getToken() = sharedPrefs.getString(TOKEN_KEY, "")
}