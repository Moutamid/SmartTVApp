package com.ixidev.player

import okhttp3.*
import java.io.IOException

object HlsCastUrlExtractor {

    fun extractUrl(url:String,onResult:(resultUrl:String?)->Unit){
        val client= OkHttpClient()
        val request= Request.Builder().url(url).build()
        client.newCall(request).enqueue(object:Callback{
            override fun onFailure(call: Call, e: IOException) {
               e.printStackTrace()
                onResult(null)
            }

            override fun onResponse(call: Call, response: Response) {
                onResult(response.request.url.toString())
            }

        })
    }
}