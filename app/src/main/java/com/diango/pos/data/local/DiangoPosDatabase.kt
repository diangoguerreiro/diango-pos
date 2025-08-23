// data/local/DiangoPosDatabase.kt
package com.diango.pos.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.diango.pos.data.model.Payment

@Database(
    entities = [Payment::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class DiangoPosDatabase : RoomDatabase() {
    
    abstract fun paymentDao(): PaymentDao
    
    companion object {
        @Volatile
        private var INSTANCE: DiangoPosDatabase? = null
        
        fun getDatabase(context: Context): DiangoPosDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DiangoPosDatabase::class.java,
                    "diango_pos_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}