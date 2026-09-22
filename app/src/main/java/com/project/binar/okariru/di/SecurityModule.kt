package com.project.binar.okariru.di

import android.content.Context
import com.project.binar.okariru.core.security.RootChecker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {
    @Provides
    @Singleton
    fun provideRootChecker(
        @ApplicationContext context: Context,
    ): RootChecker = RootChecker(context)
}
