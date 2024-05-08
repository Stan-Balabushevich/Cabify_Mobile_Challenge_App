package id.slava.nt.cabifymobilechallengeapp.presentation.products_list

import id.slava.nt.cabifymobilechallengeapp.domain.model.Product

/**
 * Data class `ProductListState` represents the state of the UI related to displaying a list of products.
 * This class is used to encapsulate all necessary information the UI needs to render itself appropriately
 * in various conditions of the product list retrieval process.
 *
 * Components:
 * - `isLoading`: A Boolean flag indicating whether the data is currently being loaded. This helps the UI
 *   decide whether to show a loading spinner or other loading indicators.
 * - `products`: A list of `Product` objects that holds the actual product data retrieved. This is what the UI
 *   lists in the product display section.
 * - `error`: A string that contains error messages if any issues occurred during the data retrieval process.
 *   This allows the UI to display informative error messages to the user, improving the user experience
 *   by providing clear feedback on what went wrong.
 *
 * Usage:
 * This class is typically used in conjunction with a ViewModel, where an instance of `ProductListState` is
 * maintained to reflect the current state of the product list UI. The ViewModel will update this state object
 * in response to changes in the underlying data layer or user interactions, and the UI layer will observe these
 * changes to re-render itself with the updated state. This approach adheres to the principles of modern UI
 * architecture patterns such as MVVM (Model-View-ViewModel), promoting a clear separation of concerns and making
 * the codebase easier to manage and test.
 *
 * By keeping the UI state in a single mutable data structure that the UI observes, changes to the state are
 * centralized and predictable, reducing the potential for bugs and inconsistencies in the UI. This pattern
 * also facilitates easier state restoration and testing.
 */
data class ProductListState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val error: String = ""
)
