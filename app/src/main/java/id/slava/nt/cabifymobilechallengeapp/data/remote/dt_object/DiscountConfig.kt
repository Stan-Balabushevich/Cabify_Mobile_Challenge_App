package id.slava.nt.cabifymobilechallengeapp.data.remote.dt_object

import com.google.gson.annotations.SerializedName

data class DiscountConfig(
    @SerializedName("discounts") val discounts: Map<String, ProductDiscount>
)

sealed class ProductDiscount {
    data class BuyXGetYFree(
        @SerializedName("x") val x: Int,
        @SerializedName("y") val y: Int
    ) : ProductDiscount()

    data class BulkDiscount(
        @SerializedName("threshold") val threshold: Int,
        @SerializedName("discountedPrice") val discountedPrice: Double
    ) : ProductDiscount()
}
