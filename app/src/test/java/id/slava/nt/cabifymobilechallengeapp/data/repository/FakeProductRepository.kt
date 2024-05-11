package id.slava.nt.cabifymobilechallengeapp.data.repository

import id.slava.nt.cabifymobilechallengeapp.common.Resource
import id.slava.nt.cabifymobilechallengeapp.data.remote.dt_object.DiscountConfig
import id.slava.nt.cabifymobilechallengeapp.data.remote.dt_object.ProductDiscount
import id.slava.nt.cabifymobilechallengeapp.domain.model.Product
import id.slava.nt.cabifymobilechallengeapp.domain.repository.ProductRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeProductRepository : ProductRepository {

    var shouldReturnError = false

    // Define your predefined list of products for testing
    val productList = listOf(
        Product(1, "product1", "Product 1", 10.0),
        Product(2, "product2", "Product 2", 15.0),
        Product(3, "product3", "Product 3", 20.0)
    )

    // Define your predefined DiscountConfig for testing
    val discountConfig = DiscountConfig(
        mapOf(
            "product1" to ProductDiscount.BuyXGetYFree(2, 1),
            "product2" to ProductDiscount.BulkDiscount(5, 10.0)
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
