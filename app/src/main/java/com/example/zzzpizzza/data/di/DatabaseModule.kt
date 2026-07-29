package com.example.zzzpizzza.data.di

import android.content.Context
import androidx.room.Room
import com.example.zzzpizzza.data.DATABASE_NAME
import com.example.zzzpizzza.data.local.db.Database
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
    fun provideRoom(@ApplicationContext context: Context) = Room.databaseBuilder(
        context = context,
        klass = Database::class.java,
        name = DATABASE_NAME,
    )
        // The database is only a cache of the server data, so it is recreated instead of migrated.
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()

    @Provides
    @Singleton
    fun providePizzaDao(database: Database) = database.pizzaDao()

}
