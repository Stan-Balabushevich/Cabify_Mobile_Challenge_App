package id.slava.nt.cabifymobilechallengeapp.data.remote

import id.slava.nt.cabifymobilechallengeapp.data.remote.dt_object.ProductsDto
import retrofit2.http.GET

const val BASE_URL = "https://gist.githubusercontent.com"

interface ProductApi {

    @GET("/palcalde/6c19259bd32dd6aafa327fa557859c2f/raw/ba51779474a150ee4367cda4f4ffacdcca479887/Products.json")
    suspend fun getProducts(): ProductsDto
}