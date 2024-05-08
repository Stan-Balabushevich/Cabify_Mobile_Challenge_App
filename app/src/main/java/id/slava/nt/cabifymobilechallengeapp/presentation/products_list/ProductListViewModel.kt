package id.slava.nt.cabifymobilechallengeapp.presentation.products_list

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.slava.nt.cabifymobilechallengeapp.common.Resource
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

class ProductListViewModel(private val getProductsUseCase: GetProductsUseCase): ViewModel() {

    private val _products = mutableStateOf(ProductListState())
    val products: State<ProductListState> = _products

    //if your state is only being observed and modified within the Composables, using MutableState<T> is perfectly fine.
    // If you foresee a need for more complex state management or sharing state across different parts of your app, consider using StateFlow<T>.
    private val _productsFlow = MutableStateFlow(ProductListState())
    val productsFlow: StateFlow<ProductListState> = _productsFlow

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            getProductsUseCase().collect { resource ->
                when (resource) {
                    is Resource.Loading -> _products.value = ProductListState(isLoading = true)
                    is Resource.Success -> _products.value = ProductListState(products = resource.data ?: emptyList())
                    is Resource.Error -> _products.value = ProductListState(error = resource.message ?: "An unexpected error occurred")
                }
            }
        }
    }



}