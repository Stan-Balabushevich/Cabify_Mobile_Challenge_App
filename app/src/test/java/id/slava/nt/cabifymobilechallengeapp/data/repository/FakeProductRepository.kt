package id.slava.nt.cabifymobilechallengeapp.data.repository

import id.slava.nt.cabifymobilechallengeapp.common.Resource
import id.slava.nt.cabifymobilechallengeapp.data.remote.dt_object.DiscountConfig
import id.slava.nt.cabifymobilechallengeapp.domain.model.Product
import id.slava.nt.cabifymobilechallengeapp.domain.repository.ProductRepository
import id.slava.nt.cabifymobilechallengeapp.util.product
import id.slava.nt.cabifymobilechallengeapp.util.productBulkDiscount
import id.slava.nt.cabifymobilechallengeapp.util.productBuyXGetYFreeDiscount
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeProductRepository : ProductRepository {

    var shouldReturnError = false

    // Define your predefined list of products for testing
    val productList = listOf(
        product().copy(id = 1, price = 10.0),
        product().copy(id = 2, price = 15.0),
        product().copy(id = 3, price = 20.0),
        product().copy(id = 4, price = 30.0),
        product().copy(id = 5, price = 40.0),

    )

    // Define your predefined DiscountConfig for testing
    val discountConfig = DiscountConfig(
        mapOf(
            "product1" to productBuyXGetYFreeDiscount(),
            "product2" to productBulkDiscount()
        )
    )
    override suspend fun getProducts(): Flow<Resource<List<Product>>> = flow{

        emit(Resource.Loading())

        delay(200)

        if (shouldReturnError) {
            emit(Resource.Error("Test error"))
        } else {
            emit(Resource.Success(productList))
        }

    }

    override suspend fun getDiscountRules(): Flow<Resource<DiscountConfig>> = flow{

        delay(200)

        if (shouldReturnError) {
            emit(Resource.Error("Test error"))
        } else {
            emit(Resource.Success(discountConfig))
        }
    }
}
