package id.slava.nt.cabifymobilechallengeapp.data.repository

import id.slava.nt.cabifymobilechallengeapp.common.Resource
import id.slava.nt.cabifymobilechallengeapp.data.local.ProductDao
import id.slava.nt.cabifymobilechallengeapp.data.local.db_object.toProduct
import id.slava.nt.cabifymobilechallengeapp.data.remote.ProductApi
import id.slava.nt.cabifymobilechallengeapp.data.remote.dt_object.toProductEntity
import id.slava.nt.cabifymobilechallengeapp.domain.model.Product
import id.slava.nt.cabifymobilechallengeapp.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ProductRepositoryImpl(
    private val api: ProductApi,
    private val dao: ProductDao
): ProductRepository {

    override suspend fun getProducts(): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading())
        // Retrieve the most recent update timestamp from the database.
        val lastUpdate = dao.getMostRecentUpdate() ?: 0L
        val currentTime = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000  // Represents one day in milliseconds.

        // Check if the current data is older than one day.
        if (currentTime - lastUpdate > oneDayMillis) {
            try {
                // Fetch new data from the API.
                val remoteProducts = api.getProducts().products.map { it.toProductEntity() }
                // Update each product's lastUpdated timestamp to the current time.
                remoteProducts.forEach { it.lastUpdated = currentTime }
                // Save the updated list of products to the database.
                dao.saveProducts(remoteProducts)
            } catch (e: Exception) {
                // Log or handle the exception appropriately.
                // Consider how the UI should respond to errors.
                emit(Resource.Error(e.message ?: "An error occurred"))
            }
        }

        // Emit the current list of products from the database.
        emitAll(dao.getAllProducts().map { entities -> Resource.Success(entities.map { it.toProduct() } )})
    }
}
