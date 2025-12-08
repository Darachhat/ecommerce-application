package kh.sothun.darachhat.rupp.fe.ecommerce_app.model

import android.accessibilityservice.GestureDescription
import android.media.Rating
import androidx.appcompat.widget.DialogTitle
import java.io.Serializable

data class ItemModel(
    var title: String="",
    var description: String="",
    var picUrl: ArrayList<String> = ArrayList(),
    var size: ArrayList<String> = ArrayList(),
    var color: ArrayList<String> = ArrayList(),
    var price: Double=0.0,
    var oldPrice: Double=0.0,
    var rating: Double=0.0,
    var numberInCart: Int=1
) : Serializable