package id.slava.nt.cabifymobilechallengeapp.data.repository

import id.slava.nt.cabifymobilechallengeapp.R
import id.slava.nt.cabifymobilechallengeapp.common.DISCOUNTS_RULES_FILE_NAME
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
import kotlinx.coroutines.flow.flowOn
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

        val lastUpdate = dao.getMostRecentUpdate() ?: 0L
        val currentTime = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000  // Represents one day in milliseconds.

        // Check if the current data is older than one day.
        if (currentTime - lastUpdate > oneDayMillis) {
            try {
                // Fetch new data from the API
                    val products = productApi.getProducts().products.map { it.toProductEntity() }
                    products.forEach { it.lastUpdated = currentTime }

                    // Emit the freshly fetched and mapped products immediately
                    emit(Resource.Success(products.map { it.toProduct() }))

                    // Perform database operations
                    try {
                        dao.deleteAllProducts()
                        dao.saveProducts(products)
                    } catch (dbException: Exception) {
                        emit(Resource.Error(dbException.message ?: "Database operation failed"))
                    }

                    products.map { it.toProduct() }

            } catch (apiException: Exception) {
                // Handle API exceptions
                emit(Resource.Error(apiException.message ?: "Server error occurred"))
                return@flow
            }
        }

        // Emit the current list of products from the database.
        try {
            emitAll(
                dao.getAllProducts()
                    .map { entities -> Resource.Success(entities.map { it.toProduct() }) }
            )
        } catch (dbException: Exception) {
            emit(Resource.Error(dbException.message ?: "An error occurred while reading from the database"))
        }
    }.flowOn(Dispatchers.IO)



    /**
     * This segment of the code is part of a repository class responsible for fetching, caching, and providing fallback mechanisms
     * for discount rules in an application. It uses a combination of Retrofit for network requests, Gson for JSON parsing, and a custom
     * FileManager interface for file operations.
     * Key Components
     *
     *     gsonWithRuntimeTypeAdapter:
     *         A pre-configured Gson instance equipped with a RuntimeTypeAdapterFactory to handle polymorphic deserialization of
     *         JSON data into Kotlin data classes.
     *
     *     File Management Methods:
     *         loadDiscountRulesFromRawResource(): Loads discount rules from a raw resource file if network and local cache fail.
     *         loadDiscountRulesFromLocalFile(): Attempts to load discount rules from a locally cached file.
     *
     *     Main Repository Function getDiscountRules():
     *         Manages the primary logic to fetch, cache, and retrieve discount rules using a combination of API calls and local file operations.
     * */



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
        val jsonData = fileManager.readFromFile(DISCOUNTS_RULES_FILE_NAME)
        return jsonData?.let {
            try {
                gsonWithRuntimeTypeAdapter.fromJson(it, DiscountConfig::class.java)
            } catch (e: Exception) {
                null // Handle parsing error
            }
        }
    }

    /**
     *    Network Request: Initiates an API call to fetch discount rules. If successful,
     *    the data is serialized into JSON and saved to a local file for caching.
     *     Error Handling: If the API call fails (e.g., due to network issues),
     *     the method attempts to load the discount rules from a locally cached file.
     *     Fallback Mechanism: If both the network call and local file read fail, it attempts to load the discount rules from a
     *     raw resource bundled with the app, ensuring that some form of data is always available.
     *  */

    override suspend fun getDiscountRules(): Flow<Resource<DiscountConfig>> = flow {
        try {
            val discountRules = withContext(Dispatchers.IO) {
                discountApi.getDiscountRules()
            }
            val json = withContext(Dispatchers.IO) {
                gsonWithRuntimeTypeAdapter.toJson(discountRules)
            }
            withContext(Dispatchers.IO) {
                fileManager.saveToFile(DISCOUNTS_RULES_FILE_NAME, json)
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
