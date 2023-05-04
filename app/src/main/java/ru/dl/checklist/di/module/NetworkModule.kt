package ru.dl.checklist.di.module

import com.google.gson.GsonBuilder
import com.skydoves.sandwich.adapters.ApiResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import ru.dl.checklist.BuildConfig.BASEURL
import timber.log.Timber
import javax.inject.Singleton

@Module
object NetworkModule {
    @Provides
    fun provideOkHttpClientFood(): OkHttpClient {
        val logging = HttpLoggingInterceptor { message -> Timber.i(message) }
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC)


        return OkHttpClient.Builder()
//            .addInterceptor(BasicAuthInterceptor(username, password))
            .addInterceptor(logging)
            /*  .eventListener(BugsnagOkHttpPlugin())
              .connectTimeout(OK_HTTP_SETTINGS_TIMEOUT)
              .readTimeout(OK_HTTP_SETTINGS_TIMEOUT)
              .writeTimeout(OK_HTTP_SETTINGS_TIMEOUT)*/
            .build()
    }

    @Provides
    fun provideRetrofitFood(okHttpClient: OkHttpClient): Retrofit {
        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .baseUrl(BASEURL)
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun provideFoodApi(retrofit: Retrofit): FoodApi {
        return retrofit.create(FoodApi::class.java)
    }
}