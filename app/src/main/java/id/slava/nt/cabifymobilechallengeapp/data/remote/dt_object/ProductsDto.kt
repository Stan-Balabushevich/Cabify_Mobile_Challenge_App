package id.slava.nt.cabifymobilechallengeapp.data.remote.dt_object
import com.google.gson.annotations.SerializedName
import id.slava.nt.cabifymobilechallengeapp.data.local.database.db_object.ProductEntity


data class ProductsDto(
    @SerializedName("products")
    val products: List<ProductDto>
)

data class ProductDto(
    @SerializedName("code")
    val code: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Double
)

fun ProductDto.toProductEntity(): ProductEntity {
    return ProductEntity(
        code = code,
        name = name,
        price = price
    )
}