package network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


object RequestInterceptor : Interceptor {


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val token = TokenHolder.token

        var request: Request = chain.request()
//        println("Outgoing request to ${request.url}")

        request = if (token.isEmpty()) {
            request
                .newBuilder()
                .build()
        } else {
//            println("Authorization: Bearer $token")
            request
                .newBuilder()
                .addHeader(
                    "Authorization",
                    "Bearer $token"
                )
                .build()

        }
        return chain.proceed(request)           //response
    }
}