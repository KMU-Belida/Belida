package com.example.belida

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(
    private val context: Context, private val dataList:ArrayList<DataVo>
):RecyclerView.Adapter<CustomAdapter.ItemViewHolder>(){
    var mPosition = 0

    fun getPosition():Int{
        return mPosition
    }
    private fun setPosition(position:Int){
        mPosition = position
    }
    fun addItem(dataVo:DataVo){
        dataList.add(dataVo)
        notifyDataSetChanged()
    }
    fun removeItem(position:Int){
        if (position>0){
            dataList.removeAt(position)
            notifyDataSetChanged()
        }
    }
    inner class ItemViewHolder(itemView: View):
RecyclerView.ViewHolder(itemView){
    private val userPhoto = itemView.findViewById<ImageView>(R.id.iv_grid_image)
    private val userName = itemView.findViewById<TextView>(R.id.tv_grid_title)

    fun bind(dataVo:DataVo,context:Context){
        if(dataVo.photo!=null){
            val resourcedId = context.resources.getIdentifier(dataVo.photo.toString(),"drawble",context.packageName)
            if(resourcedId>0){
                userPhoto.setImageResource(resourcedId)
            }
            else{
                userPhoto.setImageResource(R.mipmap.ic_launcher_round)
            }
        }
        else{
            userPhoto.setImageResource(R.mipmap.ic_launcher_round)
        }
        userName.text = dataVo.name
    }

}

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_layout_grid,parent,false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(dataList[position], context)

        holder.itemView.setOnClickListener { view ->
            setPosition(position)
            Toast.makeText(view.context, "$position 아이템클릭!", Toast.LENGTH_SHORT).show()

        }
        holder.itemView.setOnLongClickListener { view ->
            setPosition(position)
            Toast.makeText(view.context, "$position 아이템롱클릭!", Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }
    }
}