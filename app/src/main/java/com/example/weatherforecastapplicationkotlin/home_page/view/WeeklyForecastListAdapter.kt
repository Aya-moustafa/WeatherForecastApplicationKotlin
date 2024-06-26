package com.example.weatherforecastapplicationkotlin.home_page.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.weatherforecastapplicationkotlin.R
import com.example.weatherforecastapplicationkotlin.model.Weather
import com.example.weatherforecastapplicationkotlin.model.WeatherItem
import com.example.weatherforecastapplicationkotlin.setting.model.SettingOptions
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import java.text.SimpleDateFormat
import java.util.Locale

class WeeklyForecastListAdapter(context: Context , setting  :SettingOptions)  : ListAdapter<WeatherItem, MyViewHolder>(MyDiffUtil()) {

    private lateinit var weekly_forecast_List : List<WeatherItem>
    private val context : Context = context
    private val setting = setting
    private lateinit var translateArabic : Translator

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater : LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view : View = inflater.inflate(R.layout.hourly_weather_item, parent , false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val weatherItem : WeatherItem = getItem(position)
        var weather : Weather = weatherItem.weather.get(0)
        val currentDate = weatherItem.dt_txt.substring(0, 10) // Extracting date without time
        val currentTime = weatherItem.dt_txt.substring(11) // Extracting time

        downloadModelToTranslate()
        var formattedDate  = HomeFragment.formatDate(weatherItem.dt)
        Log.i("Adapter", "onBindViewHolder: formattedDate $formattedDate ")

        if(setting.language == "en") {
            var indexComma = formattedDate.indexOf(",") + 1
            val monthAndYear = formattedDate.substring(indexComma)
            var FirstThreeLetterFromDay = formattedDate.substring(0, 3)
            holder.week_day.text = FirstThreeLetterFromDay
            holder.weekly_date.text = monthAndYear.toString()
            holder.description.text = weather.description
        }else if (setting.language == "ar") {
            translateDate(formattedDate) { translatedDate ->
                var indexComma = translatedDate.indexOf("،") + 1
                val monthAndYear = translatedDate.substring(indexComma)
                val parts = translatedDate.split("،")
                val day = parts.first()
                holder.week_day.text = day
                holder.weekly_date.text = monthAndYear
            }
            translateDate(weather.description) { translatedDesc ->
               holder.description.text = translatedDesc
            }
        }

        if(setting.unitsTemp == "standard"){
            holder.weeklyTemp.text = weatherItem.getTemperatureInInt().toString()
        }else if (setting.unitsTemp == "metric"){
            holder.weeklyTemp.text = kelvinToCelsius( weatherItem.main.temp)?.toInt().toString()
        }else if (setting.unitsTemp == "imperial"){
            holder.weeklyTemp.text = kelvinToFahrenheit( weatherItem.main.temp)?.toInt().toString()
        }

        val url_img = "https://openweathermap.org/img/wn/" + weather.icon + ".png"
        Glide.with(context).load(url_img)
            .apply(
                RequestOptions()
                //  .placeholder(R.drawable.placeholder) // don't forget the placeholder image
                //  .error(R.drawable.placeholder)
            )
            .into(holder.weekDayImg)
        //holder.productPrice.text = product.price.toString()
        /*Glide.with(holder.productImg.context)
            .load(product.thumbnail)
            .into(holder.productImg)*/
    }

    private fun downloadModelToTranslate() {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.ARABIC)
            .build()
        translateArabic = Translation.getClient(options)
        var conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        translateArabic.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                // Model downloaded successfully. Okay to start translating.
                // (Set a flag, unhide the translation UI, etc.)
            }
            .addOnFailureListener { exception ->
                // Model couldn’t be downloaded or other internal error.
                // ...
            }

    }
    private fun translateDate(date: String, onSuccess: (String) -> Unit) {
        translateArabic.translate(date)
            .addOnSuccessListener { translatedText ->
                onSuccess(translatedText)
            }
            .addOnFailureListener { exception ->
                Log.e("TAG", "Translation failed with exception: ${exception.message}")
            }
    }
}


class MyDiffUtil : DiffUtil.ItemCallback<WeatherItem>(){
    override fun areItemsTheSame(oldItem: WeatherItem, newItem: WeatherItem): Boolean {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: WeatherItem, newItem: WeatherItem): Boolean {
        return oldItem == newItem
    }

}


fun kelvinToCelsius(kelvin: Double): Double? {
    return (kelvin - 273.15)
}

fun kelvinToFahrenheit(kelvin: Double): Double? {
    return (kelvin * 9/5 - 459.67)
}

fun formatTime(inputDate: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    val outputFormat = SimpleDateFormat("h a", Locale.ENGLISH)

    val date = inputFormat.parse(inputDate)

    return outputFormat.format(date)
}

class MyViewHolder (private val itemView : View) : RecyclerView.ViewHolder(itemView){
    val week_day : TextView = itemView.findViewById(R.id.hourldayTxt)
    val weeklyTemp : TextView = itemView.findViewById(R.id.hourly_temp)
    val weekly_date : TextView = itemView.findViewById(R.id.hourlmyTxt)
    val description : TextView = itemView.findViewById(R.id.descr)
    val weekDayImg   : ImageView = itemView.findViewById(R.id.hourly_img)
    val productCardView : CardView = itemView.findViewById(R.id.hourly_Card)


}