package id.slava.nt.cabifymobilechallengeapp.data

import id.slava.nt.cabifymobilechallengeapp.data.local.database.ProductDao
import id.slava.nt.cabifymobilechallengeapp.data.local.database.db_object.ProductEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeProductDao: ProductDao {

    private val productsDB = mutableListOf<ProductEntity>()
    var shouldReturnErrorSaveToDatabase = false
    var shouldReturnErrorGetFromDatabase = false
    var recentUpdate: Long? = null

    override fun getAllProducts(): Flow<List<ProductEntity>> = flow {
        if (shouldReturnErrorGetFromDatabase) {
            throw Exception("Database error")
        }
        emit(productsDB)
    }

    override suspend fun getMostRecentUpdate(): Long? {
        return recentUpdate
    }

    override suspend fun saveProducts(products: List<ProductEntity>) {
        if (shouldReturnErrorSaveToDatabase) {
            throw Exception("Database error")
        }
        productsDB.addAll(products)
    }

    override suspend fun deleteAllProducts() {
        productsDB.clear()
    }
}