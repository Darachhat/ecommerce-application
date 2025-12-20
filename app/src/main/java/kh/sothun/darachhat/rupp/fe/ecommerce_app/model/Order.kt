package kh.sothun.darachhat.rupp.fe.ecommerce_app.model

import java.io.Serializable

data class Order(
    val id: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val orderDate: Long = System.currentTimeMillis(),
    val status: String = "pending", // pending, processing, shipped, delivered, cancelled
    val deliveryInfo: DeliveryInfo = DeliveryInfo(),
    val paymentMethod: String = "",
    val items: List<OrderItemDetail> = emptyList(),
    val pricing: OrderPricing = OrderPricing()
) : Serializable

data class DeliveryInfo(
    val fullName: String = "",
    val phone: String = "",
    val address: String = "",
    val city: String = "",
    val postalCode: String = ""
) : Serializable

data class OrderItemDetail(
    val productId: String = "",
    val title: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val size: String = "",
    val color: String = "",
    val thumbnail: String = ""
) : Serializable

data class OrderPricing(
    val subtotal: Double = 0.0,
    val tax: Double = 0.0,
    val delivery: Double = 0.0,
    val total: Double = 0.0
) : Serializable
