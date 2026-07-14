package com.example.cafeapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cafeapp.data.local.dao.DraftCartDao
import com.example.cafeapp.data.local.dao.MenuDao
import com.example.cafeapp.data.local.entity.DraftCartEntity
import com.example.cafeapp.data.local.entity.MenuEntity

@Database(entities = [MenuEntity::class, DraftCartEntity::class], version = 3, exportSchema = false)
abstract class CafeDatabase : RoomDatabase() {

    abstract fun menuDao(): MenuDao
    abstract fun draftCartDao(): DraftCartDao

    companion object {
        @Volatile
        private var INSTANCE: CafeDatabase? = null

        fun getDatabase(context: Context): CafeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CafeDatabase::class.java,
                    "cafe_local_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}