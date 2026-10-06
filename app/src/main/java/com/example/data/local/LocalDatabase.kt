package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "cart_items")
data class LocalCartItem(
    @PrimaryKey val productId: String,
    val sellerId: String,
    val sellerName: String,
    val productName: String,
    val brand: String,
    val category: String,
    val price: Double,
    val discountPrice: Double?,
    val unit: String,
    val imageUrl: String,
    val quantity: Int,
    val stockQuantity: Int
)

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): Flow<List<LocalCartItem>>

    @Query("SELECT * FROM cart_items")
    suspend fun getCartItemsList(): List<LocalCartItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: LocalCartItem)

    @Update
    suspend fun update(item: LocalCartItem)

    @Delete
    suspend fun delete(item: LocalCartItem)

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun deleteByProductId(productId: String)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}

@Database(entities = [LocalCartItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kisansetu_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
