package id.slava.nt.cabifymobilechallengeapp.di

import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import id.slava.nt.cabifymobilechallengeapp.data.local.database.ProductDatabase
import id.slava.nt.cabifymobilechallengeapp.data.local.files.AndroidFileManager
import id.slava.nt.cabifymobilechallengeapp.data.local.files.FileManager
import id.slava.nt.cabifymobilechallengeapp.data.remote.BASE_URL
import id.slava.nt.cabifymobilechallengeapp.data.remote.DiscountApi
import id.slava.nt.cabifymobilechallengeapp.data.remote.ProductApi
import id.slava.nt.cabifymobilechallengeapp.data.remote.RuntimeTypeAdapterFactory
import id.slava.nt.cabifymobilechallengeapp.data.remote.dt_object.ProductDiscount
import id.slava.nt.cabifymobilechallengeapp.data.repository.ProductRepositoryImpl
import id.slava.nt.cabifymobilechallengeapp.domain.repository.ProductRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    // Gson with RuntimeTypeAdapterFactory for handling complex JSON for discounts
    val gsonWithAdapterFactory: Gson = GsonBuilder()
        .registerTypeAdapterFactory(
            RuntimeTypeAdapterFactory.of(ProductDiscount::class.java, "type")
                .registerSubtype(ProductDiscount.BuyXGetYFree::class.java, "BuyXGetYFree")
                .registerSubtype(ProductDiscount.BulkDiscount::class.java, "BulkDiscount"))
        .create()

    // Retrofit instance for Product API that does not require custom Gson
    single(named("productApi")) {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())  // Standard Gson converter
            .build()
            .create(ProductApi::class.java)
    }

    // Retrofit instance for Discount API that requires custom Gson
    single(named("discountApi")) {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gsonWithAdapterFactory))
            .build()
            .create(DiscountApi::class.java)
    }

    // Room database setup
    single {
        Room.databaseBuilder(
            get(),
            ProductDatabase::class.java,
            ProductDatabase.DATABASE_NAME
        ).build()
    }

    // Product DAO access
    single {
        get<ProductDatabase>().productDao
    }

    single<FileManager> {
        AndroidFileManager(get())
    }

    // ProductRepository implementation
    single<ProductRepository> {
        ProductRepositoryImpl(
            productApi = get(named("productApi")),
            discountApi = get(named("discountApi")),
            dao = get(),
            fileManager = get()
        )
    }
}
