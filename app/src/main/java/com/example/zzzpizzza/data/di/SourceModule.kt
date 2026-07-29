package com.example.zzzpizzza.data.di

import com.example.zzzpizzza.data.local.source.PizzaLocalSourceImpl
import com.example.zzzpizzza.data.remote.source.PizzaRemoteSourceImpl
import com.example.zzzpizzza.domain.source.PizzaLocalSource
import com.example.zzzpizzza.domain.source.PizzaRemoteSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SourceModule {

    @Binds
    abstract fun bindRemotePizzaSource(source: PizzaRemoteSourceImpl): PizzaRemoteSource

    @Binds
    abstract fun bindLocalPizzaSource(source: PizzaLocalSourceImpl): PizzaLocalSource
}
