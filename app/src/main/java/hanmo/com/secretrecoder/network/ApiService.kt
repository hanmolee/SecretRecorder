package hanmo.com.secretrecoder.network

import android.util.Log
import hanmo.com.secretrecoder.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.HashMap
import java.util.concurrent.TimeUnit

/**
 * Created by hanmo on 2018. 4. 27..
 */
interface ApiService {

    companion object {
        private var headers = HashMap<String, String>()

        var BASE_URL = if (!BuildConfig.DEBUG) {
            "release server"
        } else {
            "test server"
        }//real : dev

        fun addHeaders(headers: HashMap<String, String>): Companion {
            ApiService.headers.putAll(headers)
            return ApiService
        }

        fun addHeader(key: String, value: String) {
            headers[key] = value
        }

        fun create(): ApiService {

            val nullOnEmptyConverterFactory = object : Converter.Factory() {
                fun converterFactory() = this
                override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit) = object : Converter<ResponseBody, Any?> {
                    val nextResponseBodyConverter = retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)
                    override fun convert(value: ResponseBody) = if (value.contentLength() != 0L) nextResponseBodyConverter.convert(value) else null
                }
            }

            val loggingInterceptor = HttpLoggingInterceptor()
            if (BuildConfig.DEBUG) {
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            } else {
                loggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
            }

            val okHttpClient = OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor { chain ->
                        val original = chain.request()
                        val builder = original.newBuilder()

                        for ((key, value) in headers) {
                            Log.e("Network", "key : $key / value :$value")
                            builder.header(key, value)
                        }

                        val request = builder.method(original.method(), original.body()).build()
                        chain.proceed(request)
                    }
                    .retryOnConnectionFailure(true)
                    .build()

            val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(nullOnEmptyConverterFactory)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)
                    .build()
            return retrofit.create(ApiService::class.java)
        }
    }

}