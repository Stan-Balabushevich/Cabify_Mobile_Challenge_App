package id.slava.nt.cabifymobilechallengeapp.presentation.products_list

import id.slava.nt.cabifymobilechallengeapp.domain.model.Product

data class ProductListState(
    val isLoading: Boolean = false,
    val coins: List<Product> = emptyList(),
    val error: String = ""
)
