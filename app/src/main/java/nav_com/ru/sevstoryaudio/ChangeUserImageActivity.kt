package nav_com.ru.sevstoryaudio

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.gson.Gson
import nav_com.ru.sevstoryaudio.connection.Get
import nav_com.ru.sevstoryaudio.models.AnswerModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class ChangeUserImageActivity : AppCompatActivity() {

    private val sharedPrefs by lazy {  getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
    private var pictureToSend : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_user_image)

        val defBtn = findViewById<ConstraintLayout>(R.id.constraintLayout15)
        val amphoraBtn = findViewById<ConstraintLayout>(R.id.constraintLayout14)
        val camBtn = findViewById<ConstraintLayout>(R.id.constraintLayout16)
        val hotelBtn = findViewById<ConstraintLayout>(R.id.constraintLayout17)
        val planeBtn = findViewById<ConstraintLayout>(R.id.constraintLayout18)
        val mapBtn = findViewById<ConstraintLayout>(R.id.constraintLayout19)
        val toProfile = findViewById<Button>(R.id.backButton)
        val savePicture = findViewById<Button>(R.id.save_user_image)

        toProfile.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("return","profile")
            startActivity(intent)
            finish()
        }

        var currentPicture = getCurrentPicture()
        if (currentPicture == null) currentPicture = "default"

        selectPic(currentPicture)

        defBtn.setOnClickListener {
            selectPic()
        }

        amphoraBtn.setOnClickListener {
            selectPic("amphora")
        }

        camBtn.setOnClickListener {
            selectPic("camera")
        }

        hotelBtn.setOnClickListener {
            selectPic("hotel")
        }

        planeBtn.setOnClickListener {
            selectPic("plane")
        }

        mapBtn.setOnClickListener {
            selectPic("world_map")
        }

        savePicture.setOnClickListener {

            val token = getToken()
            val url = "user/profile/picture/$pictureToSend?token=$token"
            val getResponse = Get()

            getResponse.run(
                url,
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        runOnUiThread {
                            Toast.makeText(
                                this@ChangeUserImageActivity,
                                resources.getString(R.string.err_set_picture),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        val stringResponse = response.body.string()
                        val gson = Gson()

                        val answer: AnswerModel = gson.fromJson(stringResponse, AnswerModel::class.java)
                        if (answer.responseBody) {
                            runOnUiThread {
                                setPicture(pictureToSend)
                                val intent = Intent(this@ChangeUserImageActivity, MainActivity::class.java)
                                intent.putExtra("return","profile")
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(
                                    this@ChangeUserImageActivity,
                                    resources.getString(R.string.err_set_picture),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                })
        }
    }

    private fun selectPic(pic: String = "default"){
        pictureToSend = pic

        val d = findViewById<ConstraintLayout>(R.id.constraintLayout15)
        val a = findViewById<ConstraintLayout>(R.id.constraintLayout14)
        val c = findViewById<ConstraintLayout>(R.id.constraintLayout16)
        val h = findViewById<ConstraintLayout>(R.id.constraintLayout17)
        val p = findViewById<ConstraintLayout>(R.id.constraintLayout18)
        val m = findViewById<ConstraintLayout>(R.id.constraintLayout19)

        clearAllLayouts()
        when (pic) {
            "default" -> d.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.links))
            "amphora" -> a.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.links))
            "camera" -> c.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.links))
            "hotel" -> h.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.links))
            "plane" -> p.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.links))
            "world_map" -> m.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.links))
        }
    }

    private fun clearAllLayouts() {
        val d = findViewById<ConstraintLayout>(R.id.constraintLayout15)
        val a = findViewById<ConstraintLayout>(R.id.constraintLayout14)
        val c = findViewById<ConstraintLayout>(R.id.constraintLayout16)
        val h = findViewById<ConstraintLayout>(R.id.constraintLayout17)
        val p = findViewById<ConstraintLayout>(R.id.constraintLayout18)
        val m = findViewById<ConstraintLayout>(R.id.constraintLayout19)

        d.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.dark_white))
        a.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.dark_white))
        c.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.dark_white))
        h.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.dark_white))
        p.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.dark_white))
        m.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.dark_white))

    }

    private fun getCurrentPicture() = sharedPrefs.getString(USER_PICTURE_KEY, "default")

    private fun setPicture(picture: String) = sharedPrefs.edit().putString(USER_PICTURE_KEY, picture).apply()

    private fun getToken() = sharedPrefs.getString(TOKEN_KEY, "")
}