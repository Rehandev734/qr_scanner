package com.example.scanify.di

import android.content.Context
import com.example.scanify.data.local.dao.GeneratedQRDao
import com.example.scanify.data.local.dao.ScanHistoryDao
import com.example.scanify.data.local.database.ScanifyDatabase
import com.example.scanify.data.repository.GeneratedQRRepositoryImpl
import com.example.scanify.data.repository.ScanRepositoryImpl
import com.example.scanify.data.datasource.SettingsDataStore
import com.example.scanify.data.repository.SettingsRepositoryImpl
import com.example.scanify.domain.repository.GeneratedQRRepository
import com.example.scanify.domain.repository.ScanRepository
import com.example.scanify.domain.repository.SettingsRepository
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
    fun provideDatabase(@ApplicationContext context: Context): ScanifyDatabase {
        return ScanifyDatabase.getInstance(context)
    }

    @Provides
    fun provideScanHistoryDao(database: ScanifyDatabase): ScanHistoryDao {
        return database.scanHistoryDao()
    }

    @Provides
    fun provideGeneratedQRDao(database: ScanifyDatabase): GeneratedQRDao {
        return database.generatedQRDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideScanRepository(dao: ScanHistoryDao): ScanRepository {
        return ScanRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideGeneratedQRRepository(dao: GeneratedQRDao): GeneratedQRRepository {
        return GeneratedQRRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideSettingsDataStore(
        @ApplicationContext context: Context
    ): SettingsDataStore {
        return SettingsDataStore(context)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        dataStore: SettingsDataStore
    ): SettingsRepository {
        return SettingsRepositoryImpl(dataStore)
    }
}
