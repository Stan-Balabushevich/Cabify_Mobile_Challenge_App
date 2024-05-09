package id.slava.nt.cabifymobilechallengeapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import id.slava.nt.cabifymobilechallengeapp.data.local.database.db_object.ProductEntity

@Database(
    entities = [ProductEntity::class],
    version = 1
)
abstract class ProductDatabase(): RoomDatabase() {

    abstract val productDao: ProductDao

    companion object{
        const val DATABASE_NAME = "product_db"
    }
}