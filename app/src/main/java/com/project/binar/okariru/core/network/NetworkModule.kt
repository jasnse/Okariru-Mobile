package com.project.binar.okariru.core.network

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.project.binar.okariru.data.auth.local.AuthSessionLocalDataSource
import com.project.binar.okariru.data.auth.local.SessionAuthTokenProvider
import com.project.binar.okariru.data.auth.remote.AuthApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import com.project.binar.okariru.BuildConfig
import retrofit2.converter.scalars.ScalarsConverterFactory

private const val TIMEOUT_SECONDS = 30L
private const val HEADER_AUTHORIZATION = "Authorization"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideAuthTokenProvider(
        localDataSource: AuthSessionLocalDataSource,
    ): AuthTokenProvider = SessionAuthTokenProvider(localDataSource)

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        tokenProvider: AuthTokenProvider,
        localDataSource: AuthSessionLocalDataSource,
    ): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .addInterceptor(AuthHeaderInterceptor(tokenProvider))
        .addInterceptor(SessionExpiryInterceptor(localDataSource))

        // Di build release, artifact chucker-no-op membuat interceptor ini tidak melakukan apa pun.
        .addInterceptor(
            ChuckerInterceptor.Builder(context)
                .redactHeaders(HEADER_AUTHORIZATION)
                .alwaysReadResponseBody(true)
                .build()
        )
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, json: Json): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(client)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)
}