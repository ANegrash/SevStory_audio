package nav_com.ru.sevstoryaudio.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import nav_com.ru.sevstoryaudio.R
import nav_com.ru.sevstoryaudio.models.AllCitiesModel

class AllCitiesAdapter (
    context: Context?,
    resource: Int,
    jsonObjects: List<AllCitiesModel>
) : ArrayAdapter<AllCitiesModel?>(context!!, resource, jsonObjects) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val layout: Int = resource
    private val jsonObject: List<AllCitiesModel> = jsonObjects

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val view = inflater.inflate(layout, parent, false)
        val citiesListItem = jsonObject[position]

        val nameCity = view.findViewById<TextView>(R.id.cityName_selectCity)
        val image = view.findViewById<ImageView>(R.id.is_city_selected_tick)

        nameCity.text = citiesListItem.cityName

        if (citiesListItem.isCurrent)
            image.visibility = View.VISIBLE
        else
            image.visibility = View.INVISIBLE

        return view
    }
}