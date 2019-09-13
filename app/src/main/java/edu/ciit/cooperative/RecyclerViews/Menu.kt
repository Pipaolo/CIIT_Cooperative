package edu.ciit.cooperative.RecyclerViews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import edu.ciit.cooperative.Models.Menu
import edu.ciit.cooperative.R


class MenuAdapter(
    val context: Context?,
    val menuList: ArrayList<Menu>,
    val clickListener: (Menu) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.home_dashboard_menu, parent, false)
        return MenuViewHolder(view)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        (holder as MenuViewHolder).bind(menuList[position], clickListener)

    }

    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val menuTitle: TextView = view.findViewById(R.id.home_cardView_dashboardMenuTitle)
        val menuCardView: CardView = view.findViewById(R.id.home_cardView_dashboardMenu)
        fun bind(menu: Menu, clickListener: (Menu) -> Unit) {
            menuTitle.text = menu.title
            menuCardView.setOnClickListener { clickListener(menu) }
        }
    }

}