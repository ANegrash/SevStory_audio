package nav_com.ru.sevstoryaudio.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import nav_com.ru.sevstoryaudio.R
import nav_com.ru.sevstoryaudio.models.AllTripsModel

class AllTripsAdapter (
        context: Context?,
        resource: Int,
        jsonObjects: List<AllTripsModel>
    ) : ArrayAdapter<AllTripsModel?>(context!!, resource, jsonObjects) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val layout: Int = resource
    private val jsonObject: List<AllTripsModel> = jsonObjects

    override fun getView (
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val view = inflater.inflate(layout, parent, false)
        val tripObj = jsonObject[position]

        val nameTrip = view.findViewById<TextView>(R.id.trip_name)
        val minutes = view.findViewById<TextView>(R.id.time_trip)
        val points = view.findViewById<TextView>(R.id.trip_points)
        val image = view.findViewById<ImageView>(R.id.iconTrip)
        val isLikedImg = view.findViewById<ImageView>(R.id.liked_inAllTrips)

        image.clipToOutline = true
        val url = "https://sevstory.nav-com.ru/app/img/tour_icons/" + tripObj.tripIcon

        Picasso.get()
            .load(url)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(image)

        nameTrip.text = tripObj.tripName
        minutes.text = getTrueMinutes(tripObj.minutes)
        points.text = getTruePoints(tripObj.countMonuments)
        if (tripObj.isLiked)
            isLikedImg.visibility = View.VISIBLE
        else
            isLikedImg.visibility = View.GONE

        return view
    }

    private fun getTrueMinutes (
        minutes: Int
    ) : String {
        var result = ""
        val hours = minutes / 60

        if (hours != 0) {
            result +=  " $hours "
            result += when (hours) {
                1 -> {
                    context.resources.getString(R.string.declensions_hour_1)
                }
                2, 3, 4 -> {
                    context.resources.getString(R.string.declensions_hour_2)
                }
                else -> {
                    context.resources.getString(R.string.declensions_hour_3)
                }
            }
        }

        val leftMinutes = minutes % 60

        if (leftMinutes != 0) {
            result += " $leftMinutes "
            val lastCharOfMin = leftMinutes % 10

            result += if (leftMinutes in 11..19) {
                context.resources.getString(R.string.declensions_minute_3)
            } else {
                when (lastCharOfMin) {
                    1 -> {
                        context.resources.getString(R.string.declensions_minute_1)
                    }
                    2, 3, 4 -> {
                        context.resources.getString(R.string.declensions_minute_2)
                    }
                    else -> {
                        context.resources.getString(R.string.declensions_minute_3)
                    }
                }
            }
        }

        return result
    }

    private fun getTruePoints (countPoints : Int) : String {
        val p1 = context.resources.getString(R.string.declensions_point_1)
        val p2 = context.resources.getString(R.string.declensions_point_2)
        val p3 = context.resources.getString(R.string.declensions_point_3)
        return when (countPoints) {
            1, 21 -> {
                "$countPoints $p1"
            }
            in 5..20, in 25..30 -> {
                "$countPoints $p3"
            }
            in 2..4, in 22..24 -> {
                "$countPoints $p2"
            }
            else -> {
                "$countPoints $p3"
            }
        }
    }
}