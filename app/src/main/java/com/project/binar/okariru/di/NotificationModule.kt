package com.project.binar.okariru.di

import android.content.Context
import com.project.binar.okariru.MainActivity
import com.project.binar.okariru.core.notification.AndroidAppNotifier
import com.project.binar.okariru.core.notification.AppNotifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {
    @Provides
    @Singleton
    fun provideAppNotifier(
        @ApplicationContext context: Context,
    ): AppNotifier = AndroidAppNotifier(
        context = context,
        target = MainActivity::class.java,
    )

//
//    @Provides
//    @Singleton
//    fun provideFirebaseMessagingClient(): FirebaseMessagingClient = FirebaseMessagingClient()


}