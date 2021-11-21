package com.glushko.winfox_test_task.presentation_layer.ui.main_screen.menu_screen.adapter

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.glushko.winfox_test_task.R
import com.glushko.winfox_test_task.business_logic_layer.domain.Menu
import com.glushko.winfox_test_task.business_logic_layer.interactor.GlideApp

class AdapterMenu(val list: List<Menu> = listOf()): RecyclerView.Adapter<AdapterMenu.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        return MenuViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_menu, parent, false))
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size


    inner class MenuViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        private val imgMenu =  itemView.findViewById<ImageView>(R.id.image_menu)
        private val textDesc = itemView.findViewById<TextView>(R.id.desc_menu)
        private val price = itemView.findViewById<TextView>(R.id.price_menu)
        private val weight = itemView.findViewById<TextView>(R.id.weight_menu)

        fun onBind(menu: Menu){
            GlideApp.with(itemView.context).load(menu.image).circleCrop().error(R.drawable.ic_launcher_foreground).into(imgMenu)
            textDesc.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(menu.desc, Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml(menu.desc)
            }
            price.text = "${menu.price} "
            weight.text = "${menu.weight} гр."
        }

    }

}