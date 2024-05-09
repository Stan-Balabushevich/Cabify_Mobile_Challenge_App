package id.slava.nt.cabifymobilechallengeapp.data.repository

import id.slava.nt.cabifymobilechallengeapp.R
import id.slava.nt.cabifymobilechallengeapp.common.DISCOUNTS_RULES_FILE
import id.slava.nt.cabifymobilechallengeapp.common.Resource
import id.slava.nt.cabifymobilechallengeapp.data.local.database.ProductDao
import id.slava.nt.cabifymobilechallengeapp.data.local.database.db_object.toProduct
import id.slava.nt.cabifymobilechallengeapp.data.local.files.FileManager
import id.slava.nt.cabifymobilechallengeapp.data.remote.DiscountApi
import id.slava.nt.cabifymobilechallengeapp.data.remote.ProductApi
import id.slava.nt.cabifymobilechallengeapp.data.remote.dt_object.DiscountConfig
import id.slava.nt.cabifymobilechallengeapp.data.remote.dt_object.toProductEntity
import id.slava.nt.cabifymobilechallengeapp.data.util.getGsonWithTypeAdapterFactory
import id.slava.nt.cabifymobilechallengeapp.domain.model.Product
import id.slava.nt.cabifymobilechallengeapp.domain.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ProductRepositoryImpl(
    private val productApi: ProductApi,
    private val discountApi: DiscountApi,
    private val dao: ProductDao,
    private val fileManager: FileManager
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

        val lastUpdate = withContext(Dispatchers.IO) {
            dao.getMostRecentUpdate() ?: 0L
        }
        val currentTime = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000  // Represents one day in milliseconds.

        // Check if the current data is older than one day.
        if (currentTime - lastUpdate > oneDayMillis) {
            try {
                // Fetch new data from the API and perform database operations in IO dispatcher.
                val remoteProducts = withContext(Dispatchers.IO) {
                    val products = productApi.getProducts().products.map { it.toProductEntity() }
                    products.forEach { it.lastUpdated = currentTime }

                    // Perform database operations
                    dao.deleteAllProducts()
                    dao.saveProducts(products)

                    products
                }
            } catch (e: Exception) {
                // Log or handle the exception appropriately.
                emit(Resource.Error(e.message ?: "An error occurred"))
                return@flow
            }
        }

        // Emit the current list of products from the database.
        emitAll(
            dao.getAllProducts()
                .map { entities -> Resource.Success(entities.map { it.toProduct() }) }
        )
    }


    private val gsonWithRuntimeTypeAdapter = getGsonWithTypeAdapterFactory()

    private fun loadDiscountRulesFromRawResource(): DiscountConfig? {
        val jsonData = fileManager.readFromRawResource(R.raw.discounts_raw)
        return jsonData?.let {
            try {
                gsonWithRuntimeTypeAdapter.fromJson(it, DiscountConfig::class.java)
            } catch (e: Exception) {
                null // Handle parsing error
            }
        }
    }

    private fun loadDiscountRulesFromLocalFile(): DiscountConfig? {
        val jsonData = fileManager.readFromFile(DISCOUNTS_RULES_FILE)
        return jsonData?.let {
            try {
                gsonWithRuntimeTypeAdapter.fromJson(it, DiscountConfig::class.java)
            } catch (e: Exception) {
                null // Handle parsing error
            }
        }
    }

    override suspend fun getDiscountRules(): Flow<Resource<DiscountConfig>> = flow {
        try {
            val discountRules = withContext(Dispatchers.IO) {
                discountApi.getDiscountRules()
            }
            val json = withContext(Dispatchers.IO) {
                gsonWithRuntimeTypeAdapter.toJson(discountRules)
            }
            withContext(Dispatchers.IO) {
                fileManager.saveToFile(DISCOUNTS_RULES_FILE, json)
            }
            emit(Resource.Success(discountRules))
        } catch (apiException: Exception) {
            emit(Resource.Error(apiException.message ?: "Network error occurred, attempting local fallback..."))
            val localRules = withContext(Dispatchers.IO) { loadDiscountRulesFromLocalFile() }
            localRules?.let {
                emit(Resource.Success(it))
            } ?: run {
                val fallbackRules = withContext(Dispatchers.IO) { loadDiscountRulesFromRawResource() }
                fallbackRules?.let {
                    emit(Resource.Success(fallbackRules))
                }
                return@flow
            }
        }
    }




}
