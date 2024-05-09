package id.slava.nt.cabifymobilechallengeapp.presentation.products_list

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import id.slava.nt.cabifymobilechallengeapp.common.MUG
import id.slava.nt.cabifymobilechallengeapp.common.Resource
import id.slava.nt.cabifymobilechallengeapp.common.TSHIRT
import id.slava.nt.cabifymobilechallengeapp.common.VOUCHER
import id.slava.nt.cabifymobilechallengeapp.domain.model.CartItem
import id.slava.nt.cabifymobilechallengeapp.domain.model.Product
import id.slava.nt.cabifymobilechallengeapp.domain.usecase.GetDiscountRulesUseCase
import id.slava.nt.cabifymobilechallengeapp.domain.usecase.GetProductsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * If project is relatively small and the data operations are straightforward,
 * passing the repository directly to the ViewModel constructor might be sufficient and more pragmatic.
 * class ProductListViewModel(private val repository: ProductRepository): ViewModel(){
 *            repository.getProducts().collect{...}
 *             }
 */

class ProductListViewModel(private val getProductsUseCase: GetProductsUseCase,
                           private val getDiscountRulesUseCase: GetDiscountRulesUseCase) :
    ViewModel() {

    private val _products = mutableStateOf(ProductListState())
    val products: State<ProductListState> = _products

    private val cart = mutableListOf<CartItem>()

    //if your state is only being observed and modified within the Composables, using MutableState<T> is perfectly fine.
    // If you foresee a need for more complex state management or sharing state across different parts of your app, consider using StateFlow<T>.
    private val _cartProducts = MutableStateFlow(listOf<Product>())
    val cartProducts: StateFlow<List<Product>> = _cartProducts

    fun addCartProduct(product: Product) {
        _cartProducts.value  += product
        addToCart(product)
    }
    fun removeCartProduct(product: Product) {
        _cartProducts.value  -= product
        removeFromCart(product)
    }

    fun removeAllCartProducts() {
        _cartProducts.value = emptyList()
        cart.clear()
    }

   private fun getDiscountRules() {
       viewModelScope.launch {
           getDiscountRulesUseCase().collect {
                   resource ->
               when (resource) {
                   is Resource.Loading -> {}

                   is Resource.Success -> {
                       Log.d("ProductListViewModel", "getDiscountRules: ${resource.data?.discounts}")
                   }

                   is Resource.Error -> {}
               }
           }
       }
    }

    private fun addToCart(product: Product) {
        val existingItem = cart.find { it.product.code == product.code }
        if (existingItem != null) {
            existingItem.quantity++
        } else {
            cart.add(CartItem(product, 1))
        }
    }

    private fun removeFromCart(product: Product) {
        cart.let { list ->
            val item = list.find { it.product.code == product.code }
            item?.let {
                if (it.quantity > 1) {
                    it.quantity--
                } else {
                    list.remove(it)
                }
            }
        }
    }

    fun calculateTotal(): Double {
        var total = 0.0
        cart.forEach { cartItem ->
            when (cartItem.product.code) {
                VOUCHER -> total += calculateVoucherDiscount(cartItem)
                TSHIRT -> total += calculateTshirtDiscount(cartItem)
                else -> total += cartItem.product.price * cartItem.quantity
            }
        }
        return total
    }

    private fun calculateVoucherDiscount(cartItem: CartItem): Double {
        val freeItems = cartItem.quantity / 3
        return cartItem.product.price * (cartItem.quantity - freeItems)
    }

    private fun calculateTshirtDiscount(cartItem: CartItem): Double {
        val discountedPrice = if (cartItem.quantity >= 3) 19.00 else cartItem.product.price
        return discountedPrice * cartItem.quantity
    }

    fun countSpecificItems(): Map<String, Int> {
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

    init {
        loadProducts()
        getDiscountRules()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            getProductsUseCase().collect { resource ->
                when (resource) {
                    is Resource.Loading -> _products.value =
                        ProductListState(isLoading = true)

                    is Resource.Success -> _products.value =
                        ProductListState(products = resource.data ?: emptyList())

                    is Resource.Error -> _products.value = ProductListState(
                        error = resource.message ?: "An unexpected error occurred"
                    )
                }
            }
        }
    }

}