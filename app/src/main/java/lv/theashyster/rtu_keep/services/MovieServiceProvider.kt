package lv.theashyster.rtu_keep.services

import lv.theashyster.rtu_keep.BuildConfig.REEL_GOOD_API
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MovieServiceProvider {

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            setLevel(BODY)
        }).build()

    val instance: MovieService by lazy {
        Retrofit.Builder()
            .baseUrl(REEL_GOOD_API)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(MovieService::class.java)
    }
}
