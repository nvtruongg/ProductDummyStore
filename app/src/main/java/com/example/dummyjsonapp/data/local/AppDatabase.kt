package com.example.dummyjsonapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.dummyjsonapp.data.local.dao.ProductDao
import com.example.dummyjsonapp.data.local.entity.CartEntity
import com.example.dummyjsonapp.data.local.entity.FavoriteEntity
import com.example.dummyjsonapp.data.local.entity.Product

@Database(entities = [Product::class, FavoriteEntity::class, CartEntity::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao() : ProductDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "product_database"
                ).fallbackToDestructiveMigration()// xóa data cũ, tránh crash
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}