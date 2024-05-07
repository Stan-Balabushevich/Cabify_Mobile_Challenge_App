package id.slava.nt.cabifymobilechallengeapp.domain.usecase

import id.slava.nt.cabifymobilechallengeapp.common.Resource
import id.slava.nt.cabifymobilechallengeapp.domain.model.Product
import id.slava.nt.cabifymobilechallengeapp.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow


//If project is relatively small and the data operations are straightforward,
// passing the repository directly to the ViewModel might be sufficient and more pragmatic.
class GetProductsUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(): Flow<Resource<List<Product>>> {
        return repository.getProducts()
    }
}