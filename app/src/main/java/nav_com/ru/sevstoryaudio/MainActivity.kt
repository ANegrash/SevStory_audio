package nav_com.ru.sevstoryaudio

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.navigation.NavigationBarView
import com.google.gson.Gson
import nav_com.ru.sevstoryaudio.connection.Get
import nav_com.ru.sevstoryaudio.models.ResponseGetTokenModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

const val TOKEN_KEY = "prefs.token"
const val IS_TOKEN_KEY = "prefs.is_token"
const val USER_PICTURE_KEY = "prefs.user_picture"
const val EMAIL_KEY = "prefs.email"
const val PASS_KEY = "prefs.pass"
const val CITY_ID_KEY = "prefs.city_id"
const val CITY_NAME_KEY = "prefs.city_name"
const val FIRSTRUN_KEY = "prefs.firstrun"

const val BASE_URL = "https://nav-com.ru/myguide/app/"

class MainActivity : AppCompatActivity() {

    private val sharedPrefs by lazy {  getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val intent = intent
        val backPage = intent.getStringExtra("return")

        if (isFirstrun()) {
            //TODO: first introducing activity
            //setFirstrun()
        }

        if (getCityId() == 0) {
            //TODO: change to city selection
            setCityId(1)
            setCityName("Севастополь")
        }

        if (!isTokenExist()) {
            val url = "user/create"

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
                                setPicture("default")
                            }
                        }
                    }
                })
        }

        val bnView = findViewById<NavigationBarView>(R.id.bottom_navigation)
        val tourListFragment: Fragment = TourListFragment()
        val profileFragment: Fragment = ProfileFragment()
        val masterFragment: Fragment = MasterFragment()
        val fragmentManager: FragmentManager = supportFragmentManager
        var active: Fragment = tourListFragment

        if (backPage == "profile") {
            bnView.selectedItemId = R.id.page_2
            fragmentManager.beginTransaction().add(R.id.main_container, masterFragment, "4")
                .hide(masterFragment).commit()
            fragmentManager.beginTransaction().add(R.id.main_container, tourListFragment, "2")
                .hide(tourListFragment).commit()
            fragmentManager.beginTransaction().add(R.id.main_container, profileFragment, "1")
                .commit()
            active = profileFragment
        } else if (backPage == "master"){
            bnView.selectedItemId = R.id.page_4
            fragmentManager.beginTransaction().add(R.id.main_container, masterFragment, "4")
                .commit()
            fragmentManager.beginTransaction().add(R.id.main_container, profileFragment, "2")
                .hide(profileFragment).commit()
            fragmentManager.beginTransaction().add(R.id.main_container, tourListFragment, "1")
                .hide(tourListFragment).commit()
        } else {
            bnView.selectedItemId = R.id.page_1
            fragmentManager.beginTransaction().add(R.id.main_container, masterFragment, "4")
                .hide(masterFragment).commit()
            fragmentManager.beginTransaction().add(R.id.main_container, profileFragment, "2")
                .hide(profileFragment).commit()
            fragmentManager.beginTransaction().add(R.id.main_container, tourListFragment, "1")
                .commit()
        }

        bnView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    fragmentManager.beginTransaction().hide(active).show(tourListFragment).commit()
                    active = tourListFragment
                    true
                }
                R.id.page_2 -> {
                    fragmentManager.beginTransaction().hide(active).show(profileFragment).commit()
                    active = profileFragment
                    true
                }
                R.id.page_4 -> {
                    fragmentManager.beginTransaction().hide(active).show(masterFragment).commit()
                    active = masterFragment
                    true
                }
                else -> false
            }
        }
    }

    private fun isFirstrun() = sharedPrefs.getBoolean(FIRSTRUN_KEY, false)

    private fun setFirstrun() = sharedPrefs.edit().putBoolean(FIRSTRUN_KEY, true).apply()

    private fun isTokenExist() = sharedPrefs.getBoolean(IS_TOKEN_KEY, false)

    private fun setTokenExist() = sharedPrefs.edit().putBoolean(IS_TOKEN_KEY, true).apply()

    private fun getToken() = sharedPrefs.getString(TOKEN_KEY, "")

    private fun setToken(token: String) = sharedPrefs.edit().putString(TOKEN_KEY, token).apply()

    private fun setPicture(picture: String) = sharedPrefs.edit().putString(USER_PICTURE_KEY, picture).apply()

    private fun getCityId() = sharedPrefs.getInt(CITY_ID_KEY, 0)

    private fun setCityId(id: Int) = sharedPrefs.edit().putInt(CITY_ID_KEY, id).apply()

    private fun getCityName() = sharedPrefs.getString(CITY_NAME_KEY, "")

    private fun setCityName(name: String) = sharedPrefs.edit().putString(CITY_NAME_KEY, name).apply()
}