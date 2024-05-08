package id.slava.nt.cabifymobilechallengeapp.domain.usecase

import id.slava.nt.cabifymobilechallengeapp.common.Resource
import id.slava.nt.cabifymobilechallengeapp.domain.model.Product
import id.slava.nt.cabifymobilechallengeapp.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving a list of products. This class abstracts the logic for fetching products
 * from the underlying data layers and provides a clean interface for the UI layer to invoke this logic.
 *
 * By using the `invoke` operator, this use case can be called like a function, enhancing readability
 * and maintainability of the code where this use case is used, typically in ViewModels.
 */
class GetProductsUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(): Flow<Resource<List<Product>>> {
        return repository.getProducts()
    }
}