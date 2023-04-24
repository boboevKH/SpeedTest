package com.boboevkh.lite_version

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.boboevkh.speed.databinding.ActivityMainBinding
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        webView = binding.webView
        setContentView(binding.root)

        binding.apply {
            webView.settings.apply {
                domStorageEnabled = true
                javaScriptEnabled = true
            }
            webView.clearCache(true)
        }

        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()

        binding.button.setOnClickListener {

            webView.loadUrl("https://fast.com")
            webView.webChromeClient = object : WebChromeClient() {
            }
//            testing(webView)
            second(webView)
        }

    }


    private fun second(webView: WebView){

        val js =
            "(function() {return document.querySelector('.extra-details-container.align-container').className.includes('succeeded');})();"
        val handler = Handler()

        val runnable = object : Runnable {
            override fun run() {
                webView.evaluateJavascript(js) { result ->
                    val isSucceeded = result == "true"

                    if (isSucceeded) {

//                        ***   Получаем данные +++
                        webView.evaluateJavascript(
                            "(function() { return { userIp: document.getElementById('user-ip').textContent, speedValue: document.getElementById('speed-value').textContent, speedUnits: document.getElementById('speed-units').textContent, latencyLabel: document.getElementById('latency-label').textContent, latencyValue: document.getElementById('latency-value').textContent, latencyUnits: document.getElementById('latency-units').textContent, bufferbloatLabel: document.getElementById('bufferbloat-label').textContent, bufferbloatValue: document.getElementById('bufferbloat-value').textContent, bufferbloatUnits: document.getElementById('bufferbloat-units').textContent, uploadValue: document.getElementById('upload-value').textContent, uploadUnits: document.getElementById('upload-units').textContent } })();"
                        ) { result ->
                            val data = JSONObject(result)
                            val speedValue = data.getString("speedValue")
                            val speedUnits = data.getString("speedUnits")
                            val latencyValue = data.getString("latencyValue")
                            val latencyUnits = data.getString("latencyUnits")
                            val bufferbloatValue = data.getString("bufferbloatValue")
                            val bufferbloatUnits = data.getString("bufferbloatUnits")
                            val uploadValue = data.getString("uploadValue")
                            val uploadUnits = data.getString("uploadUnits")
//                        ***   Получаем данные ---

                            val request = JSONObject()
                            request.put("download_speed","$speedValue $speedUnits")
                            request.put("upload_speed", "$uploadValue $uploadUnits")
                            request.put("loaded_latency","$bufferbloatValue $bufferbloatUnits")
                            request.put("unloaded_latency", "$latencyValue $latencyUnits")

                            val interceptor = HttpLoggingInterceptor()
                            interceptor.level = HttpLoggingInterceptor.Level.BODY

                            val okHttpClient = OkHttpClient.Builder()
                                .addInterceptor(interceptor)
                                .build()


                            // POST-запрос с использованием Retrofit2
                            val retrofit = Retrofit.Builder()
                                .baseUrl("https://api-life3.megafon.tj")
                                .addConverterFactory(GsonConverterFactory.create())
                                .client(okHttpClient)
                                .build()

                            val service = retrofit.create(Service::class.java)
                            service.sendData(request).enqueue(object : Callback<Void> {
                                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                    Toast.makeText(this@MainActivity, ""+response.code(),Toast.LENGTH_SHORT).show()
                                }

                                override fun onFailure(call: Call<Void>, t: Throwable) {

                                }
                            })
                            Log.d("JSONDATA", request.toString())

                            binding.apply {
                                button.isEnabled = true
                                button.text = "Тест"
                            }

                        }

                    } else {
                        handler.postDelayed(this, 1)
                        binding.apply {
                            button.isEnabled = false
                            button.text = "..."
                        }
                    }
                }
            }
        }
        handler.postDelayed(runnable, 1)

    }

}