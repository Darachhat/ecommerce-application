package kh.sothun.darachhat.rupp.fe.ecommerce_app.adapters

import android.graphics.Color
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
    
    // Color name to hex mapping
    private val colorMap = mapOf(
        "black" to "#000000",
        "white" to "#FFFFFF",
        "red" to "#FF0000",
        "blue" to "#0000FF",
        "green" to "#00FF00",
        "yellow" to "#FFFF00",
        "orange" to "#FFA500",
        "purple" to "#800080",
        "pink" to "#FFC0CB",
        "gray" to "#808080",
        "grey" to "#808080",
        "brown" to "#A52A2A",
        "navy" to "#000080",
        "burgundy" to "#800020",
        "multi" to "#FF6347",
        "zebra" to "#000000",
        "cream" to "#FFFDD0",
        "panda" to "#000000",
        "syracuse" to "#FF6600",
        "kentucky" to "#0033A0",
        "infrared" to "#FF006E",
        "carmine" to "#960018",
        "grape" to "#6F2DA8",
        "fire red" to "#FF2400",
        "bred" to "#C41E3A",
        "concord" to "#702963",
        "space jam" to "#000080",
        "cement" to "#8B8680",
        "galaxy" to "#4B0082",
        "gold" to "#FFD700",
        "checkerboard" to "#000000",
        "checkered" to "#000000"
    )
    
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
        val colorName = items[position]
        
        // Convert color name to hex or use it directly if it's already a hex code
        val color = when {
            colorName.startsWith("#") -> {
                try {
                    colorName.toColorInt()
                } catch (e: IllegalArgumentException) {
                    Color.BLACK
                }
            }
            else -> {
                val hexColor = colorMap[colorName.lowercase()]
                if (hexColor != null) {
                    hexColor.toColorInt()
                } else {
                    Color.BLACK
                }
            }
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