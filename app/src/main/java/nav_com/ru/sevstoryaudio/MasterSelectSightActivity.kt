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
import nav_com.ru.sevstoryaudio.adapters.SightsAdapter
import nav_com.ru.sevstoryaudio.connection.Get
import nav_com.ru.sevstoryaudio.models.AllCitiesModel
import nav_com.ru.sevstoryaudio.models.MasterSightsModel
import nav_com.ru.sevstoryaudio.models.ResponseAllCitiesModel
import nav_com.ru.sevstoryaudio.models.ResponseMasterSightsModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class MasterSelectSightActivity : AppCompatActivity() {

    private val sharedPrefs by lazy { getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_master_select_sight)
        setScreen(0, 1, 0)

        val back = findViewById<Button>(R.id.backToMaster_selectSights)
        val list = findViewById<ListView>(R.id.list_select_sights)

        val intent = intent
        val backPage = intent.getStringExtra("return")
        val selection = intent.getStringExtra("selection")

        back.setOnClickListener {
            val intentOpen = Intent(this@MasterSelectSightActivity, MainActivity::class.java)
            intentOpen.putExtra("return", backPage)
            startActivity(intentOpen)
            finish()
        }

        val cityId = getCityId()
        val except = getSavedIdList()
        val url = "master/$cityId/all?except=$except"
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

                    val responseAllSights: ResponseMasterSightsModel =
                        gson.fromJson(stringResponse, ResponseMasterSightsModel::class.java)

                    if (responseAllSights.code == 200) {
                        val sightsList: List<MasterSightsModel> = responseAllSights.responseBody

                        val sightsAdapter = SightsAdapter(
                            this@MasterSelectSightActivity,
                            R.layout.sight_item,
                            sightsList
                        )

                        runOnUiThread {
                            list.adapter = sightsAdapter

                            list.onItemClickListener =
                                AdapterView.OnItemClickListener { _, _, position, _ ->
                                    val idList = getSavedIdList()
                                    val nameList = getSavedNamesList()

                                    if (selection == "start") {
                                        if (idList != null) {
                                            if (idList.isNotEmpty())
                                                setSavedIdList(""+sightsList[position].sightId+","+idList)
                                            else
                                                setSavedIdList(""+sightsList[position].sightId)
                                        }

                                        if (nameList != null) {
                                            if (nameList.isNotEmpty())
                                                setSavedNamesList(""+sightsList[position].nameSight+","+nameList)
                                            else
                                                setSavedNamesList(""+sightsList[position].nameSight)
                                        }

                                        setStartPointSelected(true)
                                    } else {
                                        if (idList != null) {
                                            if (idList.isNotEmpty())
                                                setSavedIdList(idList+","+sightsList[position].sightId)
                                            else
                                                setSavedIdList(""+sightsList[position].sightId)
                                        }

                                        if (nameList != null) {
                                            if (nameList.isNotEmpty())
                                                setSavedNamesList(nameList+","+sightsList[position].nameSight)
                                            else
                                                setSavedNamesList(""+sightsList[position].nameSight)
                                        }
                                    }

                                    val intentOpen =
                                        Intent(this@MasterSelectSightActivity, MainActivity::class.java)
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
        super.onBackPressed()
        val intentOpen = Intent(this, MainActivity::class.java)
        intentOpen.putExtra("return", intent.getStringExtra("return"))
        startActivity(intentOpen)
        finish()
    }

    private fun setScreen(
        mainScreen: Int = 1,
        loadingScreen: Int = 0,
        errorScreen: Int = 0
    ) {
        val mainScreenActivity = findViewById<ConstraintLayout>(R.id.main_select_sights)
        val loadingScreenActivity = findViewById<ConstraintLayout>(R.id.loading_select_sights)
        val errorScreenActivity = findViewById<ConstraintLayout>(R.id.err_select_sights)

        when (mainScreen) {
            0 -> mainScreenActivity.visibility = View.GONE
            1 -> mainScreenActivity.visibility = View.VISIBLE
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

    private fun getSavedIdList() = sharedPrefs?.getString(MASTER_SAVED_ID_LIST, "")

    private fun setSavedIdList(list: String) = sharedPrefs?.edit()?.putString(MASTER_SAVED_ID_LIST, list)?.apply()

    private fun getSavedNamesList() = sharedPrefs?.getString(MASTER_SAVED_NAME_LIST, "")

    private fun setSavedNamesList(list: String) = sharedPrefs?.edit()?.putString(MASTER_SAVED_NAME_LIST, list)?.apply()

    private fun setStartPointSelected(isSelected: Boolean) = sharedPrefs?.edit()?.putBoolean(
        MASTER_SAVED_IS_FIRST, isSelected)?.apply()
}