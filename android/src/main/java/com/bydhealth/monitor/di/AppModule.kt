package com.bydhealth.monitor.di

import android.content.Context
import com.bydhealth.monitor.data.local.MaintenanceDatabase
import com.bydhealth.monitor.data.local.MaintenanceRepositoryImpl
import com.bydhealth.monitor.data.local.dao.MaintenanceDao
import com.bydhealth.monitor.domain.maintenance.MaintenanceRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MaintenanceDatabase =
        MaintenanceDatabase.create(context)

    @Provides @Singleton
    fun provideMaintenanceDao(db: MaintenanceDatabase): MaintenanceDao = db.maintenanceDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindMaintenanceRepository(impl: MaintenanceRepositoryImpl): MaintenanceRepository
}
