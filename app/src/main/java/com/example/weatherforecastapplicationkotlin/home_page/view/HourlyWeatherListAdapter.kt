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

class HourlyWeatherListAdapter(context: Context, setting  : SettingOptions)  : ListAdapter<WeatherItem, MyHourlyViewHolder>(MyHourlyDiffUtil()) {

    private lateinit var weekly_forecast_List : List<WeatherItem>
    private val context : Context = context
    private val setting = setting
    private lateinit var translateArabic : Translator

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHourlyViewHolder {
        val inflater : LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view : View = inflater.inflate(R.layout.weekly_forecast_weather, parent , false)
        return MyHourlyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyHourlyViewHolder, position: Int) {
        val weatherItem : WeatherItem = getItem(position)
        var weather : Weather = weatherItem.weather.get(0)
        val currentDate = weatherItem.dt_txt.substring(0, 10) // Extracting date without time
        val currentTime = weatherItem.dt_txt.substring(11) // Extracting time

        downloadModelToTranslate()
        var formattedDate  = HomeFragment.formatDate(weatherItem.dt)
        Log.i("Adapter", "onBindViewHolder: formattedDate $formattedDate ")
        var FirstThreeLetterFromDay = formatTime(weatherItem.dt_txt)
        if(setting.language == "en") {
            var indexComma = formattedDate.indexOf(",") + 1
            val monthAndYear = formattedDate.substring(indexComma)
            Log.i("TimePart", "onBindViewHolder: formattedDate $FirstThreeLetterFromDay ")
            holder.week_day.text = FirstThreeLetterFromDay
            holder.weekly_date.text = monthAndYear.toString()
        }else if (setting.language == "ar") {
            translateDate(formattedDate) { translatedDate ->
                var indexComma = translatedDate.indexOf("،") + 1
                val monthAndYear = translatedDate.substring(indexComma)
                val parts = translatedDate.split("،")
                val day = parts.first()
                holder.week_day.text = day
                holder.weekly_date.text = monthAndYear
            }
            translateDate(FirstThreeLetterFromDay) { translatedDate ->
                holder.week_day.text = translatedDate
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


class MyHourlyDiffUtil : DiffUtil.ItemCallback<WeatherItem>(){
    override fun areItemsTheSame(oldItem: WeatherItem, newItem: WeatherItem): Boolean {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: WeatherItem, newItem: WeatherItem): Boolean {
        return oldItem == newItem
    }

}

fun getTimeOnly(dateTimeString: String): String {
    // Split the date and time parts
    val parts = dateTimeString.split(" ")

    // Get the time part from the second element
    val timePart = parts.getOrNull(1) ?: return "" // Return empty string if the format is incorrect

    // Return the time part
    return timePart
}

class MyHourlyViewHolder (private val itemView : View) : RecyclerView.ViewHolder(itemView){
    val week_day : TextView = itemView.findViewById(R.id.dayTxt)
    val weeklyTemp : TextView = itemView.findViewById(R.id.weekly_temp)
    val weekly_date : TextView = itemView.findViewById(R.id.myTxt)
    val weekDayImg   : ImageView = itemView.findViewById(R.id.weekly_img)
    val productCardView : CardView = itemView.findViewById(R.id.Weekly_Card)


}