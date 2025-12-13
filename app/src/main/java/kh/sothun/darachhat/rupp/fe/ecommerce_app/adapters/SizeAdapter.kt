package kh.sothun.darachhat.rupp.fe.ecommerce_app.adapters

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import kh.sothun.darachhat.rupp.fe.ecommerce_app.R
import kh.sothun.darachhat.rupp.fe.ecommerce_app.databinding.ViewholderColorBinding
import kh.sothun.darachhat.rupp.fe.ecommerce_app.databinding.ViewholderSizeBinding

class SizeAdapter(private val items: ArrayList<String>):
RecyclerView.Adapter<SizeAdapter.Viewholder>()
{

    private var selectedPosition = -1
    private var lastSelectedPosition = -1
    class Viewholder(val binding: ViewholderSizeBinding):
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SizeAdapter.Viewholder {
        val binding = ViewholderSizeBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        )
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: SizeAdapter.Viewholder, position: Int){
        holder.binding.apply {
            sizeText.text = items[position]

            root.setOnClickListener {
                if (selectedPosition != position){
                    lastSelectedPosition = selectedPosition
                    selectedPosition = position
                    if (lastSelectedPosition != -1){
                        notifyItemChanged(lastSelectedPosition)
                    }
                    notifyItemChanged(selectedPosition)
                }
            }
            if (selectedPosition == position){
                colorLayout.setBackgroundResource(R.drawable.blue_bg)
                sizeText.setTextColor(androidx.core.content.ContextCompat.getColor(holder.itemView.context, R.color.white))
            } else{
                colorLayout.setBackgroundResource(R.drawable.stroke_pink_bg)
                sizeText.setTextColor(androidx.core.content.ContextCompat.getColor(holder.itemView.context, R.color.black))
            }
        }
    }

    override fun getItemCount(): Int = items.size
    
    fun getSelectedSize(): String? {
        return if (selectedPosition != -1) items[selectedPosition] else null
    }
}