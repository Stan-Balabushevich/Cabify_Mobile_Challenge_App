package id.slava.nt.cabifymobilechallengeapp.data.repository

import com.google.gson.Gson
import id.slava.nt.cabifymobilechallengeapp.common.Resource
import id.slava.nt.cabifymobilechallengeapp.data.FakeFileManager
import id.slava.nt.cabifymobilechallengeapp.data.FakeProductDao
import id.slava.nt.cabifymobilechallengeapp.data.local.database.ProductDao
import id.slava.nt.cabifymobilechallengeapp.data.local.files.FileManager
import id.slava.nt.cabifymobilechallengeapp.data.remote.DiscountApi
import id.slava.nt.cabifymobilechallengeapp.data.remote.ProductApi
import id.slava.nt.cabifymobilechallengeapp.data.util.getGsonWithTypeAdapterFactory
import id.slava.nt.cabifymobilechallengeapp.domain.model.Product
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class ProductRepositoryImplTest {

    private lateinit var productApi: ProductApi
    private lateinit var  discountApi: DiscountApi
    private lateinit var  dao: ProductDao
    private lateinit var  fileManager: FileManager
    private lateinit var mockWebServer: MockWebServer
    private lateinit var productRepository: ProductRepositoryImpl

    private lateinit var productsJson: String
    private lateinit var discountRulesJson: String

    @org.junit.jupiter.api.BeforeEach
    fun setUp() {

        // Gson with RuntimeTypeAdapterFactory for handling complex JSON for discounts
        val gsonWithAdapterFactory: Gson = getGsonWithTypeAdapterFactory()

        dao = FakeProductDao()
        fileManager = FakeFileManager()
        mockWebServer = MockWebServer()

        productApi = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(mockWebServer.url("/"))
            .build()
            .create()

        discountApi = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gsonWithAdapterFactory))
            .baseUrl(mockWebServer.url("/"))
            .build()
            .create()

        productRepository = ProductRepositoryImpl(productApi, discountApi, dao, fileManager)

        productsJson = """
            {
              "products": [
                {
                  "code": "VOUCHER",
                  "name": "Cabify Voucher",
                  "price": 5
                },
                {
                  "code": "TSHIRT",
                  "name": "Cabify T-Shirt",
                  "price": 20
                },
                {
                  "code": "MUG",
                  "name": "Cabify Coffee Mug",
                  "price": 7.5
                }
              ]
            }
        """.trimIndent()


        discountRulesJson = """
            {
            "discounts": {
            "VOUCHER": {
            "type": "BuyXGetYFree",
            "x": 2,
            "y": 1
        },
            "TSHIRT": {
            "type": "BulkDiscount",
            "threshold": 3,
            "discountedPrice": 19.00
        }
        }
        }""".trimIndent()

    }

    @Test
    fun `getProducts fetches from API when local data is outdated and updates database`() = runTest {
        // Arrange
        mockWebServer.enqueue(MockResponse().setBody(productsJson).setResponseCode(200))
        (dao as FakeProductDao).recentUpdate = System.currentTimeMillis() - (24 * 60 * 60 * 1000 + 1000)

        // Act
        val results = productRepository.getProducts().toList()

        // Assert
        val successResults = results.filterIsInstance<Resource.Success<List<Product>>>()
        assertEquals(2, successResults.size)
        successResults.first().data?.let { assertEquals(3, it.size) }
        assertNotNull((dao as FakeProductDao).getAllProducts().firstOrNull())
    }

    @Test
    fun `getProducts emits loading and then success on successful API fetch`() = runTest {

        mockWebServer.enqueue(MockResponse().setBody(productsJson).setResponseCode(200))

        // Act
        val results = productRepository.getProducts().toList()

        // Assert
        assertEquals(3, results.size) // Loading, Success from API, Success from DB
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Success)
        assertTrue(results[2] is Resource.Success)
        val successData = (results[1] as Resource.Success).data
        if (successData != null) {
            assertEquals(3, successData.size)
            assertEquals("Cabify Voucher", successData[0].name)
            assertEquals("Cabify T-Shirt", successData[1].name)
        }
    }

    @Test
    fun `getProducts emits loading and then error on API failure`() = runTest {
        // Arrange
        mockWebServer.enqueue(MockResponse().setResponseCode(500)) // Simulate API failure

        // Act
        val results = productRepository.getProducts().toList()

        // Assert
        assertEquals(2, results.size) // Loading, Error
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Error)
        assertEquals("HTTP 500 Server Error", (results[1] as Resource.Error).message)
    }

    @Test
    fun `getProducts handles database save data failure after API update`() = runTest {
        // Arrange
        mockWebServer.enqueue(MockResponse().setBody(productsJson).setResponseCode(200))
        (dao as FakeProductDao).shouldReturnErrorSaveToDatabase = true

        // Act
        val results = productRepository.getProducts().toList()

        // Assert
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Success) // Success from API
        assertTrue(results[2] is Resource.Error) // Failure when trying to save to database
    }

    @Test
    fun `getProducts handles database save data success and read data failure after API update`() = runTest {
        // Arrange
        mockWebServer.enqueue(MockResponse().setBody(productsJson).setResponseCode(200))
        (dao as FakeProductDao).shouldReturnErrorGetFromDatabase = true

        // Act
        val results = productRepository.getProducts().toList()

        // Assert
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Success) // Success from API
        assertTrue(results[2] is Resource.Error) // Failure when trying to read from database
    }

    @Test
    fun `getProducts uses local data when it is up-to-date`() = runTest {
        // Arrange
        (dao as FakeProductDao).recentUpdate = System.currentTimeMillis() - (12 * 60 * 60 * 1000) // 12 hours ago

        // Act
        val results = productRepository.getProducts().toList()

        // Assert
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Success)
        assertEquals(0, mockWebServer.requestCount) // Ensure no API call was made
    }

    @Test
    fun `getProducts handles API returning empty list`() = runTest {
        // Arrange
        val emptyProductsJson = """{ "products": [] }"""
        mockWebServer.enqueue(MockResponse().setBody(emptyProductsJson).setResponseCode(200))

        // Act
        val results = productRepository.getProducts().toList()

        // Assert
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Success)
        (results[1] as Resource.Success).data?.let { assertTrue(it.isEmpty()) }
    }

    @Test
    fun `getProducts handles corrupted data from API`() = runTest {
        // Arrange
        mockWebServer.enqueue(MockResponse().setBody("Not a valid JSON").setResponseCode(200))

        // Act
        val results = productRepository.getProducts().toList()

        // Assert
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Error) // Parsing error should result in an error state
    }

    @Test
    fun `getDiscountRules fetches from API and saves to file successfully`() = runTest {
        // Arrange

        mockWebServer.enqueue(MockResponse().setBody(discountRulesJson).setResponseCode(200))

        // Act
        val results = productRepository.getDiscountRules().toList()

        // Assert
        assertTrue(results[0] is Resource.Success)
        val successData = (results[0] as Resource.Success).data
        if (successData != null) {
            assertEquals(2, successData.discounts.size)
        }

        // Ensure file is saved
        assertTrue((fileManager as FakeFileManager).isFileSaved)
    }

    @Test
    fun `getDiscountRules falls back to local file on API failure`() = runTest {
        // Arrange
        mockWebServer.enqueue(MockResponse().setResponseCode(500)) // Simulate API failure

        (fileManager as FakeFileManager).setLocalFileContent(discountRulesJson)

        // Act
        val results = productRepository.getDiscountRules().toList()

        // Assert
        assertTrue(results[0] is Resource.Error)
        assertTrue(results[1] is Resource.Success)
        val successData = (results[1] as Resource.Success).data
        if (successData != null) {
            assertEquals(2, successData.discounts.size)
        }
    }

    @Test
    fun `getDiscountRules falls back to raw resource on API and local file failure`() = runTest {
        // Arrange
        mockWebServer.enqueue(MockResponse().setResponseCode(500)) // Simulate API failure
        (fileManager as FakeFileManager).setLocalFileContent(null) // No local file available

        (fileManager as FakeFileManager).setRawResourceContent(discountRulesJson) // Raw resource available

        // Act
        val results = productRepository.getDiscountRules().toList()

        // Assert
        assertTrue(results[0] is Resource.Error)
        assertTrue(results[1] is Resource.Success)
        val successData = (results[1] as Resource.Success).data
        if (successData != null) {
            assertEquals(2, successData.discounts.size)
        }
    }

    @Test
    fun `getDiscountRules emits error when API and all fallbacks fail`() = runTest {
        // Arrange
        mockWebServer.enqueue(MockResponse().setResponseCode(500)) // Simulate API failure
        (fileManager as FakeFileManager).setLocalFileContent(null) // No local file available
        (fileManager as FakeFileManager).setRawResourceContent(null) // No raw resource available

        // Act
        val results = productRepository.getDiscountRules().toList()

        // Assert
        assertTrue(results[0] is Resource.Error)
        assertEquals(1, results.size) // Only one error emission
    }


}