package id.slava.nt.cabifymobilechallengeapp.domain.usecase

import id.slava.nt.cabifymobilechallengeapp.common.MUG
import id.slava.nt.cabifymobilechallengeapp.common.TSHIRT
import id.slava.nt.cabifymobilechallengeapp.common.VOUCHER
import id.slava.nt.cabifymobilechallengeapp.domain.model.CartItem

class CountSpecificItemsUseCase {

    operator fun invoke(cart: List<CartItem>): Map<String, Int> {

        val counts = mutableMapOf<String, Int>()

        // Initialize counts for each specific product code
        counts[VOUCHER] = 0
        counts[TSHIRT] = 0
        counts[MUG] = 0

        // Iterate through the cart and add up quantities based on product code
        cart.forEach { cartItem ->
            when (cartItem.product.code) {
                VOUCHER -> counts[VOUCHER] = counts[VOUCHER]!! + cartItem.quantity
                TSHIRT -> counts[TSHIRT] = counts[TSHIRT]!! + cartItem.quantity
                MUG -> counts[MUG] = counts[MUG]!! + cartItem.quantity
            }
        }

        return counts

    }
}