package id.slava.nt.cabifymobilechallengeapp.presentation.products_list

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.slava.nt.cabifymobilechallengeapp.R
import id.slava.nt.cabifymobilechallengeapp.common.MUG
import id.slava.nt.cabifymobilechallengeapp.common.Resource
import id.slava.nt.cabifymobilechallengeapp.common.TSHIRT
import id.slava.nt.cabifymobilechallengeapp.common.VOUCHER
import id.slava.nt.cabifymobilechallengeapp.data.remote.dt_object.DiscountConfig
import id.slava.nt.cabifymobilechallengeapp.data.remote.dt_object.ProductDiscount
import id.slava.nt.cabifymobilechallengeapp.domain.model.CartItem
import id.slava.nt.cabifymobilechallengeapp.domain.model.Product
import id.slava.nt.cabifymobilechallengeapp.domain.usecase.GetDiscountRulesUseCase
import id.slava.nt.cabifymobilechallengeapp.domain.usecase.GetProductsUseCase
import id.slava.nt.cabifymobilechallengeapp.presentation.resource.ResourceProvider
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

class ProductListViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val getDiscountRulesUseCase: GetDiscountRulesUseCase,
    private val resourceProvider: ResourceProvider
) :
    ViewModel() {

    //if your state is only being observed and modified within the Composables, using MutableState<T> is perfectly fine.
    // If you foresee a need for more complex state management or sharing state across different parts of your app, consider using StateFlow<T>.

    // StateFlow for tracking the list of products.
    private val _products = MutableStateFlow(ProductListState())
    val products: StateFlow<ProductListState> = _products

    // Mutable list to manage the shopping cart items.
    private val cart = mutableListOf<CartItem>()

    // StateFlow for managing products in the shopping cart, primarily for UI display.
    private val _cartProducts = MutableStateFlow(listOf<Product>())
    val cartProducts: StateFlow<List<Product>> = _cartProducts

    // StateFlow for storing discount rules fetched from an external source.
    private val discountRules = MutableStateFlow<DiscountConfig?>(null)

    // Discounts explanation text
    private val _discountDescriptions = mutableStateOf(listOf<String>())
    val discountDescriptions: State<List<String>> = _discountDescriptions

    // Adds a product to the cart and updates the cart StateFlow.
    fun addCartProduct(product: Product) {
        _cartProducts.value += product
        addToCart(product)
    }

    // Removes a product from the cart and updates the cart StateFlow.
    fun removeCartProduct(product: Product) {
        _cartProducts.value -= product
        removeFromCart(product)
    }

    // Clears all products from the cart.
    fun removeAllCartProducts() {
        _cartProducts.value = emptyList()
        cart.clear()
    }

    init {
        loadProducts()
        getDiscountRules()
    }

    // Initial load products from the repository
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

    /**
     * Fetches discount rules using the GetDiscountRulesUseCase and updates the discountRules StateFlow.
     * Upon successful fetch, it composes descriptive text for each product based on their discount type
     * and updates the _discountDescriptions StateFlow to be observed by the UI.
     * In case of an error during fetch, it logs the error and updates _discountDescriptions with an error message.
     */
    private fun getDiscountRules() {
        viewModelScope.launch {
            getDiscountRulesUseCase().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        discountRules.value = resource.data
                        // Compose text for each product
                        val descriptions = products.value.products.map { product ->
                            val discount = discountRules.value?.discounts?.get(product.code)
                            when (discount) {
                                is ProductDiscount.BuyXGetYFree -> resourceProvider.getString(
                                    R.string.buy_x_get_y_discount, discount.x, discount.y, product.code)
                                is ProductDiscount.BulkDiscount -> resourceProvider.getString(
                                    R.string.bulk_discount, discount.threshold, product.code, discount.discountedPrice)
                                else -> resourceProvider.getString(R.string.no_discount, product.code)
                            }
                        }
                        _discountDescriptions.value = descriptions

                    }

                    is Resource.Error -> {
                        _discountDescriptions.value = listOf(resourceProvider.getString(R.string.error_loading_discounts))
                    }
                }
            }
        }
    }

    // Adds a product to the mutable list used for the shopping cart.
    private fun addToCart(product: Product) {
        val existingItem = cart.find { it.product.code == product.code }
        if (existingItem != null) {
            existingItem.quantity++
        } else {
            cart.add(CartItem(product, 1))
        }
    }

    // Removes a product from the mutable list used for the shopping cart.
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

    fun calculateTotalWithDiscount(): Double {
        var total = 0.0
        cart.forEach { cartItem ->
            // Fetch the discount configuration for the product code.
            val discount = discountRules.value?.discounts?.get(cartItem.product.code)
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

    // Counts the specific items in the cart and returns a map of product codes to quantities for UI display.
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
}