package kh.sothun.darachhat.rupp.fe.ecommerce_app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kh.sothun.darachhat.rupp.fe.ecommerce_app.databinding.ViewholderCartBinding
import kh.sothun.darachhat.rupp.fe.ecommerce_app.helpers.ChangeNumberItemsListener
import kh.sothun.darachhat.rupp.fe.ecommerce_app.helpers.ManagmentCart
import kh.sothun.darachhat.rupp.fe.ecommerce_app.model.ItemModel

class CartAdapter(private val listItemSelected: ArrayList<ItemModel>, context: Context,
                  var changeNumberItemsListener: ChangeNumberItemsListener?=null):
RecyclerView.Adapter<CartAdapter.Viewholder>()
{
    private val  managementCart = ManagmentCart(context)
    class Viewholder (val binding: ViewholderCartBinding) :
    RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartAdapter.Viewholder {
        val binding = ViewholderCartBinding.inflate(LayoutInflater
            .from(parent.context),parent,false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: CartAdapter.Viewholder, position: Int) {

        val item = listItemSelected[position]
        holder.binding.apply {
            titleTxt.text = item.title
            feeEachItemTxt.text = "$${item.price}"
            totalEachItemTxt.text = "$${item.numberInCart * item.price}"
            numberItemTxt.text = item.numberInCart.toString()

            Glide.with(holder.itemView.context)
                .load(item.picUrl[0])
                .into(pic)

            plusCartBtn.setOnClickListener {
                managementCart.plusItem(listItemSelected,position,object : ChangeNumberItemsListener{
                    override fun onChanged() {
                        notifyDataSetChanged()
                        changeNumberItemsListener?.onChanged()
                    }

                } )
            }

            minusCartBtn.setOnClickListener {
                managementCart.minusItem(listItemSelected,position,object : ChangeNumberItemsListener{
                    override fun onChanged() {
                        notifyDataSetChanged()
                        changeNumberItemsListener?.onChanged()
                    }

                } )
            }
        }
    }

    override fun getItemCount(): Int = listItemSelected.size
}