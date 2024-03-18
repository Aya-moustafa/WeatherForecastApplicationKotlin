package com.example.weatherforecastapplicationkotlin.home_page.view

import android.content.Context
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

class WeeklyForecastListAdapter(context: Context)  : ListAdapter<WeatherItem, MyViewHolder>(MyDiffUtil()) {

    private lateinit var weekly_forecast_List : List<WeatherItem>
    private val context : Context = context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater : LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view : View = inflater.inflate(R.layout.weekly_forecast_weather, parent , false)
        return MyViewHolder(view)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val weatherItem : WeatherItem = getItem(position)
        var weather : Weather = weatherItem.weather.get(0)
        val currentDate = weatherItem.dt_txt.substring(0, 10) // Extracting date without time
        val currentTime = weatherItem.dt_txt.substring(11) // Extracting time

        var formattedDate  = HomeFragment.formatDate(weatherItem.dt)
        var indexComma = formattedDate.indexOf(",")+1
        val monthAndYear = formattedDate.substring(indexComma)
        var FirstThreeLetterFromDay = formattedDate.substring(0,3)
        holder.week_day.text = FirstThreeLetterFromDay
        holder.weekly_date.text = monthAndYear.toString()
        holder.weeklyTemp.text = weatherItem.getTemperature().toString()
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
}


class MyDiffUtil : DiffUtil.ItemCallback<WeatherItem>(){
    override fun areItemsTheSame(oldItem: WeatherItem, newItem: WeatherItem): Boolean {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: WeatherItem, newItem: WeatherItem): Boolean {
        return oldItem == newItem
    }

}

class MyViewHolder (private val itemView : View) : RecyclerView.ViewHolder(itemView){
    val week_day : TextView = itemView.findViewById(R.id.dayTxt)
    val weeklyTemp : TextView = itemView.findViewById(R.id.weekly_temp)
    val weekly_date : TextView = itemView.findViewById(R.id.myTxt)
    val weekDayImg   : ImageView = itemView.findViewById(R.id.weekly_img)
    val productCardView : CardView = itemView.findViewById(R.id.Weekly_Card)


}