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
) : ProductRepository {

    /**
     * Retrieves the list of products, updating from the API only if the local data is older than one day and
     * when the user opens the products list screen. This method first emits the locally cached products for immediate display,
     * then checks if an update is needed based on the last update timestamp.
     *
     * If the products are older than one day, it fetches fresh data from the API, updates the local database,
     * and then emits the updated list of products. This approach avoids frequent background data fetching and
     * minimizes network usage, making it suitable for a test application where product updates do not require
     * real-time synchronization.
     *
     * Flow is used to handle this operation reactively, ensuring that any updates to the database
     * immediately reflect in the UI.
     *
     * Note: If the product list changes frequently or requires more timely updates in a future scenario,
     * implementing a background fetch strategy using WorkManager to update data at regular intervals
     * (e.g., every 5-10 minutes) might be appropriate. However, for the current scope of this test application,
     * and to keep the architecture straightforward, updates are managed manually when the user accesses the app.
     * This decision helps to balance between data freshness and resource efficiency, avoiding unnecessary
     * background operations and network requests in a simple test environment.
     */
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
                //Delete all products from the database to clean it before saving the updated list.
                dao.deleteAllProducts()
                // Save the updated list of products to the database.
                dao.saveProducts(remoteProducts)
            } catch (e: Exception) {
                // Log or handle the exception appropriately.
                // Consider how the UI should respond to errors.
                emit(Resource.Error(e.message ?: "An error occurred"))
            }
        }

        // Emit the current list of products from the database.
        emitAll(
            dao.getAllProducts()
                .map { entities -> Resource.Success(entities.map { it.toProduct() }) })
    }
}
