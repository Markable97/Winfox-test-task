package com.glushko.winfox_test_task.presentation_layer.ui.main_screen.list_places_screen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.glushko.winfox_test_task.R
import com.glushko.winfox_test_task.business_logic_layer.domain.Place
import com.glushko.winfox_test_task.business_logic_layer.interactor.GlideApp

class AdapterPlaces(val list: List<Place> = listOf()) : RecyclerView.Adapter<AdapterPlaces.PlacesViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesViewHolder {
        return PlacesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_place, parent, false))
    }

    override fun onBindViewHolder(holder: PlacesViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size


    inner class PlacesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val imagePlace = itemView.findViewById<ImageView>(R.id.imagePlace)
        private val namePlace = itemView.findViewById<TextView>(R.id.namePlace)
        fun onBind(place: Place){
            namePlace.text = place.name
            GlideApp.with(itemView.context).load(place.image).error(R.drawable.ic_launcher_foreground).into(imagePlace)

        }

    }

}
