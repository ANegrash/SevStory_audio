package nav_com.ru.sevstoryaudio.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import nav_com.ru.sevstoryaudio.R
import nav_com.ru.sevstoryaudio.models.MasterSightsModel

class SightsAdapter (
    context: Context?,
    resource: Int,
    jsonObjects: List<MasterSightsModel>
) : ArrayAdapter<MasterSightsModel?>(context!!, resource, jsonObjects) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val layout: Int = resource
    private val jsonObject: List<MasterSightsModel> = jsonObjects

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val view = inflater.inflate(layout, parent, false)
        val sightsListItem = jsonObject[position]

        val nameSight = view.findViewById<TextView>(R.id.sightName_selectSights)
        val image = view.findViewById<ImageView>(R.id.is_sight_has_audio)

        nameSight.text = sightsListItem.nameSight

        if (sightsListItem.isAudio)
            image.visibility = View.VISIBLE
        else
            image.visibility = View.INVISIBLE

        return view
    }

}