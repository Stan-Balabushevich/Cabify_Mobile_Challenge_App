package id.slava.nt.cabifymobilechallengeapp.di

import androidx.room.Room
import id.slava.nt.cabifymobilechallengeapp.data.local.ProductDatabase
import id.slava.nt.cabifymobilechallengeapp.data.remote.BASE_URL
import id.slava.nt.cabifymobilechallengeapp.data.remote.ProductApi
import id.slava.nt.cabifymobilechallengeapp.data.repository.ProductRepositoryImpl
import id.slava.nt.cabifymobilechallengeapp.domain.repository.ProductRepository
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductApi::class.java)
    }

    single {
        Room.databaseBuilder(get(),
            ProductDatabase::class.java,
            ProductDatabase.DATABASE_NAME)
            .build()
    }

    single {
        get<ProductDatabase>().productDao
    }

    single<ProductRepository> {
        ProductRepositoryImpl(api = get(), dao =  get())
    }


}