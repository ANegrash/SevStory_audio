package nav_com.ru.sevstoryaudio

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view1: View = inflater.inflate(R.layout.fragment_tour_list, container, false)

        val url = "https://sevstory.nav-com.ru/app/api?q=getAllTrips"

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
                            val listView = view1.findViewById<ListView>(R.id.list_view)
                            if (listView != null) {
                                listView.adapter = allTripsAdapter

                                listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                                    val intent = Intent(context, PreviewTripActivity::class.java)
                                    intent.putExtra("tripId", tripsList[position].tripId.toString())
                                    startActivity(intent)
                                    activity!!.finish()
                                }
                            }
                        }
                    }
                }
            })

        return view1
    }
}