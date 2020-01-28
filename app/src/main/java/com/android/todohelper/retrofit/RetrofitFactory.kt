package com.android.todohelper.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL = "http://uncroptv.000webhostapp.com/"

object RetrofitFactory {
    private var retrofit: API? = null

    val retrofitInstance: API?
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(API::class.java)
            }
            return retrofit
        }

    private fun getClient(): OkHttpClient {

        val httpClient = OkHttpClient.Builder()

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        httpClient
            .addNetworkInterceptor(logging)
            .connectTimeout(90, TimeUnit.SECONDS) // connect timeout
            .writeTimeout(90, TimeUnit.SECONDS) // write timeout
            .readTimeout(90, TimeUnit.SECONDS) // read timeout

        return httpClient.build()
    }
}