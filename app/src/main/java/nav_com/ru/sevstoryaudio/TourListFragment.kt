package nav_com.ru.sevstoryaudio

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import nav_com.ru.sevstoryaudio.adapters.AllTripsAdapter
import nav_com.ru.sevstoryaudio.connection.Get
import nav_com.ru.sevstoryaudio.models.AllTripsModel
import nav_com.ru.sevstoryaudio.models.ResponseAllTripsModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException


class TourListFragment : Fragment() {

    private val sharedPrefs by lazy { activity?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view1: View = inflater.inflate(R.layout.fragment_tour_list, container, false)

        val listScreen = view1.findViewById<ConstraintLayout>(R.id.mainScreen_tour_list)
        val loadingScreen = view1.findViewById<ConstraintLayout>(R.id.loadingScreen_tour_list)
        val errorScreen = view1.findViewById<ConstraintLayout>(R.id.errorScreen_tour_list)
        val errorImage = view1.findViewById<ImageView>(R.id.errorImage_tour_list)
        val errorText = view1.findViewById<TextView>(R.id.errorText_tour_list)
        val listView = view1.findViewById<ListView>(R.id.list_view)

        listScreen.visibility = View.GONE
        loadingScreen.visibility = View.VISIBLE
        errorScreen.visibility = View.GONE

        val url = "https://sevstory.nav-com.ru/app/api?q=getAllTrips&token=" + getToken()

        val getResponse = Get()

        getResponse.run(
            url,
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    if (context?.let { isOnline(it) } == false) {
                        errorImage.setImageResource(R.drawable.err_check_internet)
                        errorText.text = "Проверьте интернет-соединение"
                    }
                    listScreen.visibility = View.GONE
                    loadingScreen.visibility = View.GONE
                    errorScreen.visibility = View.VISIBLE
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
                            context,
                            R.layout.trip_item,
                            tripsList
                        )

                        activity?.runOnUiThread {
                            listView.adapter = allTripsAdapter

                            listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                                val intent = Intent(context, PreviewTripActivity::class.java)
                                intent.putExtra("tripId", tripsList[position].tripId.toString())
                                startActivity(intent)
                                activity!!.finish()
                            }

                            listScreen.visibility = View.VISIBLE
                            loadingScreen.visibility = View.GONE
                            errorScreen.visibility = View.GONE
                        }
                    } else {
                        listScreen.visibility = View.GONE
                        loadingScreen.visibility = View.GONE
                        errorScreen.visibility = View.VISIBLE
                    }
                }
            })

        return view1
    }

    private fun getToken() = sharedPrefs?.getString(TOKEN_KEY, "")

    fun isOnline(context1: Context): Boolean {
        val connectivityManager = context1.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

}