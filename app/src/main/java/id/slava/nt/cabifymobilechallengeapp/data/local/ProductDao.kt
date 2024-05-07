package id.slava.nt.cabifymobilechallengeapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.slava.nt.cabifymobilechallengeapp.data.local.db_object.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM database_product")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT MAX(lastUpdated) FROM database_product")
    suspend fun getMostRecentUpdate(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProducts(products: List<ProductEntity>)

}