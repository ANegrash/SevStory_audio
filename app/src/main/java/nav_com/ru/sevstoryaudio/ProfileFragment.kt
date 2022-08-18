package nav_com.ru.sevstoryaudio

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout

class ProfileFragment : Fragment() {

    private val sharedPrefs by lazy { activity?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view1: View = inflater.inflate(R.layout.fragment_profile, container, false)

        val changePhoto = view1.findViewById<ConstraintLayout>(R.id.photoRedaction)
        val emailField = view1.findViewById<ConstraintLayout>(R.id.emailField)
        val changePersonalData = view1.findViewById<ConstraintLayout>(R.id.redactPersonalInfo)
        val tourHistory = view1.findViewById<ConstraintLayout>(R.id.viewedTours)
        val favourites = view1.findViewById<ConstraintLayout>(R.id.favourites)
        val aboutApp = view1.findViewById<ConstraintLayout>(R.id.aboutApp)

        val email = getEmail()
        if (!email.isNullOrEmpty()) {
            val emailTV = view?.findViewById<TextView>(R.id.email_textView)
            emailTV?.text = email
            emailField?.visibility = View.VISIBLE
        } else {
            emailField?.visibility = View.GONE
        }

        changePhoto.setOnClickListener {
            Toast.makeText(context, "Изменение фотографии пока недоступно", Toast.LENGTH_LONG).show()
        }

        changePersonalData.setOnClickListener {
            Toast.makeText(context, "Изменение личных данных пока недоступно", Toast.LENGTH_LONG).show()
        }

        tourHistory.setOnClickListener {
            Toast.makeText(context, "Просмотр истории туров пока недоступен", Toast.LENGTH_LONG).show()
        }

        favourites.setOnClickListener {
            Toast.makeText(context, "Работа с избранным пока что недоступна", Toast.LENGTH_LONG).show()
        }

        aboutApp.setOnClickListener {
            Toast.makeText(context, "Тут будет чё-то эпичное, но пока что тут ничего нет", Toast.LENGTH_LONG).show()
        }

        return view1
    }

    private fun getEmail() = sharedPrefs?.getString(EMAIL_KEY, "")
}