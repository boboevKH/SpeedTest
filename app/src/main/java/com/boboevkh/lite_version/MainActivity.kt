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
            testing(webView)
        }

    }


    private fun testing(webView: WebView) {

        Handler().postDelayed({
            binding.button.apply {
                isEnabled = true
                text = "Тест"

            }

            webView.evaluateJavascript(
                "(function() { return { userIp: document.getElementById('user-ip').textContent, speedValue: document.getElementById('speed-value').textContent, speedUnits: document.getElementById('speed-units').textContent, latencyLabel: document.getElementById('latency-label').textContent, latencyValue: document.getElementById('latency-value').textContent, latencyUnits: document.getElementById('latency-units').textContent, bufferbloatLabel: document.getElementById('bufferbloat-label').textContent, bufferbloatValue: document.getElementById('bufferbloat-value').textContent, bufferbloatUnits: document.getElementById('bufferbloat-units').textContent, uploadValue: document.getElementById('upload-value').textContent, uploadUnits: document.getElementById('upload-units').textContent } })();"
            ) { result ->
                val data = JSONObject(result)
                val speedValue = data.getString("speedValue")
                val speedUnits = data.getString("speedUnits")
                val latencyLabel = data.getString("latencyLabel")
                val latencyValue = data.getString("latencyValue")
                val latencyUnits = data.getString("latencyUnits")
                val bufferbloatLabel = data.getString("bufferbloatLabel")
                val bufferbloatValue = data.getString("bufferbloatValue")
                val bufferbloatUnits = data.getString("bufferbloatUnits")
                val uploadValue = data.getString("uploadValue")
                val uploadUnits = data.getString("uploadUnits")
                val userIp = data.getString("userIp")


                val message = Message(
                    "$speedValue $speedUnits",
                    "$uploadValue $uploadUnits",
                    "$bufferbloatValue $bufferbloatUnits",
                    "$latencyValue $latencyUnits"
                )

                Log.d("JSONDATA",data.toString())
                Log.d("JSONDATAlist",message.toString())


                val interceptor = HttpLoggingInterceptor()
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

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
                service.sendData(message)

            }
        }, 50000)

        binding.button.apply {
            isEnabled = false
            text = "..."
        }
    }

}