package nav_com.ru.sevstoryaudio.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import nav_com.ru.sevstoryaudio.BASE_URL
import nav_com.ru.sevstoryaudio.R
import nav_com.ru.sevstoryaudio.models.FavModel

class FavTripsAdapter (
    context: Context?,
    resource: Int,
    jsonObjects: List<FavModel>
) : ArrayAdapter<FavModel?>(context!!, resource, jsonObjects) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val layout: Int = resource
    private val jsonObject: List<FavModel> = jsonObjects

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val view = inflater.inflate(layout, parent, false)
        val tripObj = jsonObject[position]

        val nameTrip = view.findViewById<TextView>(R.id.trip_name_fav)
        val image = view.findViewById<ImageView>(R.id.iconTrip_fav)

        image.clipToOutline = true
        val url = "${BASE_URL}img/tour_icons/" + tripObj.tripIcon

        Picasso.get()
            .load(url)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(image)

        nameTrip.text = tripObj.tripName

        return view
    }
}