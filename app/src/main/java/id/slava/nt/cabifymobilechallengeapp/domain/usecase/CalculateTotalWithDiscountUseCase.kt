package id.slava.nt.cabifymobilechallengeapp.domain.usecase

import id.slava.nt.cabifymobilechallengeapp.data.remote.dt_object.DiscountConfig
import id.slava.nt.cabifymobilechallengeapp.data.remote.dt_object.ProductDiscount
import id.slava.nt.cabifymobilechallengeapp.domain.model.CartItem

class CalculateTotalWithDiscountUseCase {

    operator fun invoke(cart: List<CartItem>, discountRules: DiscountConfig): Double{
        var total = 0.0
        cart.forEach { cartItem ->
            // Fetch the discount configuration for the product code.
            val discount = discountRules.discounts[cartItem.product.code]
            when (discount) {
                is ProductDiscount.BuyXGetYFree -> total += calculateBuyXGetYFreeDiscount(cartItem, discount)
                is ProductDiscount.BulkDiscount -> total += calculateBulkDiscount(cartItem, discount)
                else -> total += cartItem.product.price * cartItem.quantity // No discount applied
            }
        }
        return total
    }

    private fun calculateBuyXGetYFreeDiscount(cartItem: CartItem, discount: ProductDiscount.BuyXGetYFree): Double {
        val freeItems = cartItem.quantity / (discount.x + discount.y)
        return cartItem.product.price * (cartItem.quantity - freeItems)
    }

    private fun calculateBulkDiscount(cartItem: CartItem, discount: ProductDiscount.BulkDiscount): Double {
        val discountedPrice = if (cartItem.quantity >= discount.threshold) discount.discountedPrice else cartItem.product.price
        return discountedPrice * cartItem.quantity
    }
}