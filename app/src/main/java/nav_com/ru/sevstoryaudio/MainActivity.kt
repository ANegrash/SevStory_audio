package nav_com.ru.sevstoryaudio

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.navigation.NavigationBarView
import com.google.gson.Gson
import nav_com.ru.sevstoryaudio.connection.Get
import nav_com.ru.sevstoryaudio.models.ResponseAllTripsModel
import nav_com.ru.sevstoryaudio.models.ResponseGetTokenModel
import nav_com.ru.sevstoryaudio.models.SavedRoutesModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

const val TOKEN_KEY = "prefs.token"
const val IS_TOKEN_KEY = "prefs.is_token"
const val EMAIL_KEY = "prefs.email"
const val PASS_KEY = "prefs.pass"
const val FIRSTRUN_KEY = "prefs.firstrun"

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

        if (isFirstrun()) {
            //TODO: first introducing activity
            //setFirstrun()
        }

        if (!isTokenExist()) {
            val url = "https://sevstory.nav-com.ru/app/api?q=addNewSimpleUser"

            val getResponse = Get()

            getResponse.run(
                url,
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {

                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        val stringResponse = response.body.string()
                        val gson = Gson()

                        val responseGetToken: ResponseGetTokenModel =
                            gson.fromJson(stringResponse, ResponseGetTokenModel::class.java)

                        if (responseGetToken.code == 200) {
                            val token = responseGetToken.responseBody
                            if (token.isNotBlank()) {
                                setToken(token)
                                setTokenExist()
                            }
                        }
                    }
                })
        }

        val bnView = findViewById<NavigationBarView>(R.id.bottom_navigation)

        bnView.selectedItemId = R.id.page_1

        bnView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.page_1 -> {
                    tourSelected()
                    true
                }
                R.id.page_2 -> {
                    profileSelected()
                    true
                }
                else -> false
            }
        }
        Log.e("TAG", "" + getToken())
    }

    private fun tourSelected() {
        val tourContent = findViewById<FrameLayout>(R.id.tour_fragment)

        tourContent.visibility = View.VISIBLE
    }

    private fun profileSelected() {
        val tourContent = findViewById<FrameLayout>(R.id.tour_fragment)

        tourContent.visibility = View.GONE
    }

    private fun getSavedRouts() = sharedPrefs.getString(KEY_ROUTS, "")

    private fun isFirstrun() = sharedPrefs.getBoolean(FIRSTRUN_KEY, false)

    private fun setFirstrun() = sharedPrefs.edit().putBoolean(FIRSTRUN_KEY, true).apply()

    private fun isTokenExist() = sharedPrefs.getBoolean(IS_TOKEN_KEY, false)

    private fun setTokenExist() = sharedPrefs.edit().putBoolean(IS_TOKEN_KEY, true).apply()

    private fun getToken() = sharedPrefs.getString(TOKEN_KEY, "")

    private fun setToken(token: String) = sharedPrefs.edit().putString(TOKEN_KEY, token).apply()
}