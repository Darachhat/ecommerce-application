package kh.sothun.darachhat.rupp.fe.ecommerce_app.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kh.sothun.darachhat.rupp.fe.ecommerce_app.R
import kh.sothun.darachhat.rupp.fe.ecommerce_app.databinding.ViewholderBrandBinding
import kh.sothun.darachhat.rupp.fe.ecommerce_app.model.BrandModel

class BrandsAdapter(private val items: MutableList<BrandModel>) :
    RecyclerView.Adapter<BrandsAdapter.Viewholder>() {
        private var selectedPosition = -1
        private var lastSelectedPosition = -1

    fun updateData(newData: List<BrandModel>){
        items.clear()
        items.addAll(newData)
        notifyDataSetChanged()

    }

    class Viewholder(val binding: ViewholderBrandBinding):
    RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BrandsAdapter.Viewholder {
        val binding = ViewholderBrandBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: BrandsAdapter.Viewholder, position: Int) {
        val item = items[position]

        Glide.with(holder.itemView.context)
            .load(item.picUrl).into(holder.binding.pic)

        holder.binding.root.setOnClickListener {
            lastSelectedPosition = selectedPosition
            selectedPosition = position
            if(lastSelectedPosition != -1) notifyItemChanged(lastSelectedPosition)
            notifyItemChanged(selectedPosition)
        }

        val isSelected = selectedPosition == position

        holder.binding.pic.setBackgroundResource(
            if(isSelected){
                R.drawable.blue_bg
            }else {
                R.drawable.gray_bg
            }
        )

        ImageViewCompat.setImageTintList(
            holder.binding.pic,
            ColorStateList.valueOf(
                holder.itemView.context.getColor(
                    if(isSelected) R.color.white else R.color.black
                )
            )
        )
    }

    override fun getItemCount(): Int = items.size
}