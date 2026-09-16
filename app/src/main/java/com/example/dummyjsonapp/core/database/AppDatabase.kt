package com.example.dummyjsonapp.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.dummyjsonapp.model.CartEntity
import com.example.dummyjsonapp.model.Converters
import com.example.dummyjsonapp.model.FavoriteEntity
import com.example.dummyjsonapp.model.Product

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