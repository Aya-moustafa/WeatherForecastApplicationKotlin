package com.example.weatherforecastapplicationkotlin.notification_feature.view

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
import com.example.weatherforecastapplicationkotlin.R
import com.example.weatherforecastapplicationkotlin.model.Country
import com.example.weatherforecastapplicationkotlin.notification_feature.model.NotificationData

class NotificationListAdapter(private var context: Context) : ListAdapter<NotificationData, NotificationListAdapter.NotificationViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val inflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.notification_item, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        var notificationData : NotificationData = getItem(position)
        holder.fromD.text = notificationData.fromDate
        holder.fromT.text = notificationData.fromTime
        holder.toD.text = notificationData.toDate
        holder.toT.text = notificationData.toTime

    }

    public override fun getItem(position: Int): NotificationData {
        return currentList[position]
    }
    inner class NotificationViewHolder(private val layout: View) : RecyclerView.ViewHolder(layout) {
        val fromD: TextView = layout.findViewById(R.id.fromTime_name)
        val fromT: TextView = layout.findViewById(R.id.fromD_name)
        val toD: TextView = layout.findViewById(R.id.toT_name)
        val toT: TextView = layout.findViewById(R.id.toD_name)
        val placeCard : CardView = layout.findViewById(R.id.place_card_noti)
    }

    companion object {
        private const val TAG = "UsersAdapter"
    }
}

class UserDiffCallback : DiffUtil.ItemCallback<NotificationData>() {
    override fun areItemsTheSame(oldItem: NotificationData, newItem: NotificationData): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: NotificationData, newItem: NotificationData ): Boolean {
        return oldItem == newItem
    }
}