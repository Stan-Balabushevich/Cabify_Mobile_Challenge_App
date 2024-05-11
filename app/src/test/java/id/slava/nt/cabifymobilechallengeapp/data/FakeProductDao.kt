package id.slava.nt.cabifymobilechallengeapp.data

import id.slava.nt.cabifymobilechallengeapp.data.local.database.ProductDao
import id.slava.nt.cabifymobilechallengeapp.data.local.database.db_object.ProductEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeProductDao: ProductDao {

    private val productsDB = mutableListOf<ProductEntity>()
    var shouldReturnError = false

    override fun getAllProducts(): Flow<List<ProductEntity>> = flow {
        if (shouldReturnError) {
            throw Exception("Database error")
        }
        emit(productsDB)
    }

    override suspend fun getMostRecentUpdate(): Long? {
        return productsDB.maxOfOrNull { it.lastUpdated }
    }

    override suspend fun saveProducts(products: List<ProductEntity>) {
        productsDB.addAll(products)
    }

    override suspend fun deleteAllProducts() {
        productsDB.clear()
    }
}