package com.example.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface ShopifyStorefrontService {

  @GET("/")
  suspend fun pingStorefront(): Response<ResponseBody>

  @GET("pages/app-config.json")
  suspend fun fetchRemoteAppConfig(): Response<RemoteAppConfig>

  @Headers("Content-Type: application/json", "Accept: application/json")
  @POST("api/{apiVersion}/graphql.json")
  suspend fun queryStorefrontGraphQl(
    @Path("apiVersion") apiVersion: String,
    @Header("X-Shopify-Storefront-Access-Token") storefrontAccessToken: String,
    @Body request: ShopifyGraphQlRequest,
  ): Response<ShopifyGraphQlResponse>

  companion object {
    const val BASE_URL = "https://mynovacart.in/"

    fun create(): ShopifyStorefrontService {
      val logging =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
      val client =
        OkHttpClient.Builder()
          .connectTimeout(8, TimeUnit.SECONDS)
          .readTimeout(8, TimeUnit.SECONDS)
          .addInterceptor(logging)
          .build()

      val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

      return Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(ShopifyStorefrontService::class.java)
    }
  }
}

object ShopifyGraphQlQueries {
  const val SHOP_AND_COLLECTIONS =
    """
    query NovaCartStoreBootstrap {
      shop {
        name
        primaryDomain {
          url
        }
      }
      collections(first: 10) {
        nodes {
          id
          handle
          title
        }
      }
    }
    """
}
