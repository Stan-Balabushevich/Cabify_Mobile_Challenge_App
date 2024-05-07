package id.slava.nt.cabifymobilechallengeapp.data.local.db_object

import androidx.room.Entity
import androidx.room.PrimaryKey
import id.slava.nt.cabifymobilechallengeapp.domain.model.Product

@Entity(tableName = "database_product")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val code: String,
    val name: String,
    val price: Double,
    var lastUpdated: Long = System.currentTimeMillis()
)

fun ProductEntity.toProduct(): Product {
    return Product(
        int = id,
        code = code,
        name = name,
        price = price
    )
}