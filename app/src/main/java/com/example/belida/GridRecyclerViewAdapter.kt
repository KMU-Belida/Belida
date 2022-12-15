package com.example.belida

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.belida.R
import com.example.belida.GridItem
import kotlinx.android.synthetic.main.item_layout_grid.view.*

class GridRecyclerViewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var gridItemList: List<GridItem>? = null
    private var category_fillter = arrayListOf<String>(
        "디지털기기", "sport", "clothing", "living", "interior",
        "게임", "pet", "all"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType:Int): RecyclerView.ViewHolder{
        return GridItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_layout_grid,parent,false)
        )
    }
    override fun getItemCount(): Int{
        return gridItemList?.size ?:0
    }
    override fun onBindViewHolder(holder:RecyclerView.ViewHolder,position:Int){
        gridItemList?.let{
            (holder as GridItemViewHolder).bind(it[position])
        }
        holder.itemView.setOnClickListener {
            var intent = Intent(holder.itemView?.context, HomePage::class.java)
            Toast.makeText(holder.itemView?.context, category_fillter[position], Toast.LENGTH_SHORT).show()
            intent.putExtra("category_fillter", category_fillter[position])
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }
//    functions
    fun submitList(list: List<GridItem>?){
        gridItemList = list
        notifyDataSetChanged()
    }
    class GridItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(gridItem:GridItem) {
            itemView.iv_grid_image.setImageResource(gridItem.image)
            itemView.tv_grid_title.text = gridItem.title
        }
    }
}