package kh.sothun.darachhat.rupp.fe.ecommerce_app.adapters

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import kh.sothun.darachhat.rupp.fe.ecommerce_app.databinding.ViewholderColorBinding

class ColorAdapter(private val items: ArrayList<String>):
RecyclerView.Adapter<ColorAdapter.Viewholder>()
{

    private var selectedPosition = -1
    private var lastSelectedPosition = -1
    class Viewholder(val binding: ViewholderColorBinding):
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ColorAdapter.Viewholder {
        val binding = ViewholderColorBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        )
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: ColorAdapter.Viewholder, position: Int){
        // Ensure the color string has a '#' prefix if it doesn't already
        val colorString = if (items[position].startsWith("#")) {
            items[position]
        } else {
            "#${items[position]}"
        }
        
        val color = try {
            colorString.toColorInt()
        } catch (e: IllegalArgumentException) {
            // Default to black if color parsing fails
            android.graphics.Color.BLACK
        }
        
        holder.binding.apply {
            colorCircle.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            strokeView.visibility = if (selectedPosition == position)
                View.VISIBLE else View.GONE

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
        }
    }

    override fun getItemCount(): Int = items.size
    
    fun getSelectedColor(): String? {
        return if (selectedPosition != -1) items[selectedPosition] else null
    }
}