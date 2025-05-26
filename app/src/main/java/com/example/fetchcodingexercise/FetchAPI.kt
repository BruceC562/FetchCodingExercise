package com.example.fetchcodingexercise

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

@JsonClass(generateAdapter = true)
data class Item(
    val id: Int,
    val listId: Int,
    val name: String?
)

interface APIService {
    @GET("hiring.json")
    suspend fun getItems(): List<Item>
}

object FetchAPI {
    const val BASE_URL = "https://hiring.fetch.com/"

    private val moshi : Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: APIService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        // Optional logging interceptor for debugging
        //.client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(APIService::class.java)
    
    suspend fun getItems(): Map<Int, List<Item>> {
        return retrofit.getItems()
            .filter { !it.name.isNullOrBlank() } // First removes any items with blank or null names
            .groupBy { it.listId } // Then groups the items by listId
    }
}

