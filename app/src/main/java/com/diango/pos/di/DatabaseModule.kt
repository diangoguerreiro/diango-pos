// di/DatabaseModule.kt
package com.diango.pos.di

import android.content.Context
import androidx.room.Room
import com.diango.pos.data.local.DiangoPosDatabase
import com.diango.pos.data.local.PaymentDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DiangoPosDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            DiangoPosDatabase::class.java,
            "diango_pos_database"
        ).fallbackToDestructiveMigration().build()
    }
    
    @Provides
    fun providePaymentDao(database: DiangoPosDatabase): PaymentDao {
        return database.paymentDao()
    }
}