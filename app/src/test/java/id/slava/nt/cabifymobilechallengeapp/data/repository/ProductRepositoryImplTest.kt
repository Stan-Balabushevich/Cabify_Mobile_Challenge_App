package id.slava.nt.cabifymobilechallengeapp.data.repository

import com.google.gson.Gson
import id.slava.nt.cabifymobilechallengeapp.data.FakeFileManager
import id.slava.nt.cabifymobilechallengeapp.data.FakeProductDao
import id.slava.nt.cabifymobilechallengeapp.data.local.database.ProductDao
import id.slava.nt.cabifymobilechallengeapp.data.local.files.FileManager
import id.slava.nt.cabifymobilechallengeapp.data.remote.DiscountApi
import id.slava.nt.cabifymobilechallengeapp.data.remote.ProductApi
import id.slava.nt.cabifymobilechallengeapp.data.util.getGsonWithTypeAdapterFactory
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Assertions.*
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


    }




}