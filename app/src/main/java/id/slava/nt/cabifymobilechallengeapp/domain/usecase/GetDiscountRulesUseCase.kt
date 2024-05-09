package id.slava.nt.cabifymobilechallengeapp.domain.usecase

import id.slava.nt.cabifymobilechallengeapp.common.Resource
import id.slava.nt.cabifymobilechallengeapp.data.remote.dt_object.DiscountConfig
import id.slava.nt.cabifymobilechallengeapp.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

class GetDiscountRulesUseCase(private val repository: ProductRepository) {

    suspend operator fun invoke(): Flow<Resource<DiscountConfig>> {
        return repository.getDiscountRules()
    }
}