package nav_com.ru.sevstoryaudio

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.navigation.NavigationBarView
import com.google.gson.Gson
import nav_com.ru.sevstoryaudio.models.SavedRoutesModel

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
}