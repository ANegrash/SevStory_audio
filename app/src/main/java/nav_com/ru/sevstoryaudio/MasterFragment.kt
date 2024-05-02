package nav_com.ru.sevstoryaudio

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.Gson
import nav_com.ru.sevstoryaudio.connection.Get
import nav_com.ru.sevstoryaudio.models.ResponseRouteModel
import nav_com.ru.sevstoryaudio.models.RouteModel
import nav_com.ru.sevstoryaudio.models.SavedRoutesModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class MasterFragment : Fragment() {

    private val sharedPrefs by lazy { activity?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view1: View = inflater.inflate(R.layout.fragment_master, container, false)

        val selectedCity = view1.findViewById<Button>(R.id.button_select_city_master)
        val addStartBtn = view1.findViewById<Button>(R.id.add_start_point_btn)
        val startPointChip = view1.findViewById<Chip>(R.id.chip_start_sight)
        val addMiddleBtn = view1.findViewById<Button>(R.id.add_middle_points_btn)
        val chipGroup = view1.findViewById<ChipGroup>(R.id.middle_points_chip_group)
        val createTripBtn = view1.findViewById<Button>(R.id.create_master)

        startPointChip.visibility = View.GONE
        addStartBtn.visibility = View.VISIBLE
        createTripBtn.visibility = View.GONE

        if (!getCityName().isNullOrEmpty()){
            selectedCity.text = getCityName()
        }

        Log.e("FATALISM", ""+getSavedIdList())

        selectedCity.setOnClickListener {
            val intent = Intent(context, SelectCity::class.java)
            intent.putExtra("return", "master")
            startActivity(intent)
            activity?.finish()
        }

        val namesListString = getSavedNamesList()

        var isFirstForCreate = false
        var isMiddleForCreate = false

        if (namesListString?.length != 0) {
            val namesList = namesListString?.split(",")?.toTypedArray()

            var skipFirstInMiddle = false

            if (isStartPointSelected() == true) {
                startPointChip.text = namesList?.get(0)
                startPointChip.visibility = View.VISIBLE
                addStartBtn.visibility = View.GONE
                skipFirstInMiddle = true
                isFirstForCreate = true
            }

            if (namesList != null) {
                for (item in namesList) {
                    if (skipFirstInMiddle) {
                        skipFirstInMiddle = false
                        continue
                    }
                    val chip = Chip(context)
                    chip.text = item
                    chip.isClickable = false
                    chip.isCheckable = false
                    chip.setChipBackgroundColorResource(R.color.background_color)
                    chip.setChipStrokeColorResource(R.color.gray_light)
                    chip.chipStrokeWidth = 2.5F
                    chip.setTextColor(resources.getColor(R.color.gray))
                    chip.typeface = resources.getFont(R.font.roboto)
                    chipGroup.addView(chip as View)
                    isMiddleForCreate = true
                }
            }
        }

        if (isFirstForCreate && isMiddleForCreate)
            createTripBtn.visibility = View.VISIBLE

        addStartBtn.setOnClickListener {
            val intent = Intent(context, MasterSelectSightActivity::class.java)
            intent.putExtra("return", "master")
            intent.putExtra("selection", "start")
            startActivity(intent)
            activity?.finish()
        }

        addMiddleBtn.setOnClickListener {
            val intent = Intent(context, MasterSelectSightActivity::class.java)
            intent.putExtra("return", "master")
            intent.putExtra("selection", "middle")
            startActivity(intent)
            activity?.finish()
        }

        createTripBtn.setOnClickListener {
            val userToken = getToken()
            val cityId = getCityId()
            val savedIdList = getSavedIdList()

            val url = "master/$cityId/add?token=$userToken&sightsId=$savedIdList"

            val getResponse = Get()

            getResponse.run(
                url,
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        activity?.runOnUiThread {

                        }
                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        val stringResponse = response.body.string()
                        val gson = Gson()

                        val responseRoute: ResponseRouteModel =
                            gson.fromJson(stringResponse, ResponseRouteModel::class.java)

                        if (responseRoute.code == 200) {
                            val route: List<RouteModel> = responseRoute.responseBody

                            val savedRoute = SavedRoutesModel(0, "master", route)

                            saveRouts(gson.toJson(savedRoute))

                            activity?.runOnUiThread {
                                setSavedIdList("")
                                setSavedNamesList("")
                                setStartPointSelected(false)

                                val intent = Intent(context, StartTripActivity::class.java)
                                intent.putExtra("tripId", "master")
                                startActivity(intent)
                                activity!!.finish()
                            }

                        }
                    }
                }
            )
        }

        return view1
    }

    private fun getCityId() = sharedPrefs?.getInt(CITY_ID_KEY, 0)

    private fun getCityName() = sharedPrefs?.getString(CITY_NAME_KEY, "")

    private fun getSavedIdList() = sharedPrefs?.getString(MASTER_SAVED_ID_LIST, "")

    private fun setSavedIdList(list: String) = sharedPrefs?.edit()?.putString(MASTER_SAVED_ID_LIST, list)?.apply()

    private fun getSavedNamesList() = sharedPrefs?.getString(MASTER_SAVED_NAME_LIST, "")

    private fun setSavedNamesList(list: String) = sharedPrefs?.edit()?.putString(MASTER_SAVED_NAME_LIST, list)?.apply()

    private fun isStartPointSelected() = sharedPrefs?.getBoolean(MASTER_SAVED_IS_FIRST, false)

    private fun setStartPointSelected(isSelected: Boolean) = sharedPrefs?.edit()?.putBoolean(
        MASTER_SAVED_IS_FIRST, isSelected)?.apply()

    private fun getToken() = sharedPrefs?.getString(TOKEN_KEY, "")

    private fun saveRouts (routes: String) = sharedPrefs?.edit()?.putString(KEY_ROUTS, routes)?.apply()
}